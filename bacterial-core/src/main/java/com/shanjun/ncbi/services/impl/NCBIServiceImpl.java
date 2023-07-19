package com.shanjun.ncbi.services.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjun.ncbi.pojo.*;
import com.shanjun.ncbi.services.*;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.shanjun.ncbi.entrez.EFetch;
import com.shanjun.ncbi.entrez.ESearch;

import com.shanjun.ncbi.uitls.MonthConverter;
import com.wangyang.common.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NCBIServiceImpl implements INCBIService {
    @Autowired
    IPubMedService pubMedService;
    @Autowired
    ITaxonomyServices taxonomyServices;

    @Autowired
    ITaxonomyNamesService taxonomyNamesService;


    @Autowired
    ITaxonomyNodesServices taxonomyNodesServices;

    @Autowired
    IDiseaseService diseaseService;

    public List<String> rangeDate(int startYear, int endYear){
        LocalDate startDate = LocalDate.of(startYear, 1, 1);
        LocalDate endDate = LocalDate.of(endYear, 12, 31);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");
        List<String> range = new ArrayList<>();

        while (startDate.isBefore(endDate)) {
            String year = startDate.format(formatter);
            startDate = startDate.plusMonths(6);
            String nextYear = startDate.minusMonths(1).format(formatter);
            range.add("mindate="+year+"&maxdate="+nextYear);
//            System.out.println(year+"-"+nextYear);
        }
        return range;
    }


    @Override
    public void initTaxonomyDB(){
        List<TaxonomyNames> taxonomyNames = tsvToTaxonomyNames( "/home/wangyang/workspace/ncbi-genome-download/names.dmp");
        taxonomyNamesService.truncateTable();
        taxonomyNamesService.saveAll(taxonomyNames);


        List<TaxonomyNodes> taxonomyNodes = tsvToTaxonomyNodes("/home/wangyang/workspace/ncbi-genome-download/nodes.dmp");
        taxonomyNodesServices.truncateTable();
        taxonomyNodesServices.saveAll(taxonomyNodes);

        List<Disease> diseaseList = Arrays.asList(
                new Disease("结直肠癌","carcinoma of colon and rectum")
        );
        diseaseService.saveAll(diseaseList);
    }
    private ESearch spider(Integer retStart, Integer retMax, String date)  {
        String baseUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
        String apiKey = "8145c95381a416f75f7f3245637414470807";
        String searchTerm = "(microbiome AND disease) OR (microbial dysbiosis AND disease)";

        try {
//            search_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&mindate=1800/01/01&maxdate=2016/12/31&usehistory=y&retmode=json"

            searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
            // 构建API请求URL
//        String apiUrl = baseUrl + "esearch.fcgi?db=pubmed&term=" + searchTerm + "&api_key=" + apiKey;
            String apiUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=" + searchTerm + "&api_key=" + apiKey+"&retmax="+retMax+"&retmode=json&retstart="+retStart+"&"+date;
//        String apiUrl = "https://api.ncbi.nlm.nih.gov/lit/ctxp/v1/pubmed/?format=json&api_key=" + apiKey;
            // 创建HttpClient实例
            log.info(apiUrl);
            HttpClient httpClient = HttpClients.createDefault();

            // 创建HttpGet请求对象
            HttpGet httpGet = new HttpGet(apiUrl);

            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(httpGet);

            // 解析响应内容
            HttpEntity entity = response.getEntity();
            String responseText = EntityUtils.toString(entity);
            ESearch eSearch = JSONObject.parseObject(responseText, ESearch.class);
            // 处理响应数据
            System.out.println(responseText);
            return eSearch;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PubMed> save20182013(){
        List<String> rangeDates = rangeDate(2018, 2023);
        List<ESearch> eSearchList = new ArrayList<>();
        for (String rangeDate:rangeDates) {
            ESearch eSearch = spider(0, 10000, rangeDate);
            log.info(eSearch.getEsearchresult().getIdlist().size() + "");
            eSearchList.add(eSearch);
        }

        Set<String> pids = new HashSet<>();
        for (ESearch eSearch: eSearchList){
            pids.addAll(eSearch.getEsearchresult().getIdlist());
        }
        List<PubMed> pubMedList = new ArrayList<>();
        for (String item: pids){
            PubMed pubMed = new PubMed();
            pubMed.setPId(Integer.parseInt(item));
            pubMedList.add(pubMed);
        }
        return pubMedList;
    }


    public void initPubmed(){
        ESearch eSearch = spider(0, 10000, "mindate=2018/01&maxdate=2023/12");
//
        List<PubMed> pubMedList = pubMedService.listAll();
        if(eSearch.getEsearchresult().getCount()>pubMedList.size()){
            List<PubMed> inputPubMedList = save20182013();
            Set<Integer> findPids = ServiceUtil.fetchProperty(pubMedList, PubMed::getPId);
            Set<PubMed> pubMedSet = inputPubMedList.stream().filter(item->!findPids.contains(item.getPId())).collect(Collectors.toSet());
            pubMedService.saveAll(pubMedSet);
        }

    }

    @Override
    public List<EFetch>    spiderEFetch(Set<Integer> ids, Integer retMax) throws DocumentException, IOException {
        List<String> stringList = new ArrayList<>();
        for (Integer num : ids) {
            stringList.add(num.toString());
        }

        String joinIds = String.join(",", stringList);
        String apiKey = "8145c95381a416f75f7f3245637414470807";
        log.info(joinIds);

        String apiUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&&api_key=" + apiKey+"&retmax="+retMax;
        log.info(apiUrl);
        HttpClient httpClient = HttpClients.createDefault();
        // 创建HttpGet请求对象
        HttpPost httpPost = new HttpPost(apiUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", joinIds));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);

        // 发送请求并获取响应
        HttpResponse response = httpClient.execute(httpPost);

        // 解析响应内容
        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);
        // 处理响应数据
//            System.out.println(responseText);
        Document document = DocumentHelper.parseText(responseText);
        Element rootElement = document.getRootElement();

        List<EFetch> fetches = new ArrayList<>();
        List<Element> elements = rootElement.elements();
        for (Element element : elements){
            EFetch eFetch = new EFetch();
            Element medlineCitation = element.element("MedlineCitation");
            if(medlineCitation!=null){
                Element article = medlineCitation.element("Article");
                String pmid = medlineCitation.elementText("PMID");
                eFetch.setPId(Integer.parseInt(pmid));


                if(article.element("Abstract")!=null){
                    StringBuilder abstractText = new StringBuilder();
                    List<Element> abstractElement = article.element("Abstract").elements("AbstractText");
                    for (Element item : abstractElement){
                        abstractText.append(item.getText()+"\n");
                    }
                    eFetch.setArticleAbstract(abstractText.toString());
                }
                if(article.element("Journal")!=null){
                    String journalTitle = article.element("Journal").elementText("Title");
                    String journalTitleAbbreviation = article.element("Journal").elementText("ISOAbbreviation");
                    Journal journal = new Journal();
                    journal.setTitle(journalTitle);
                    journal.setAbbreviation(journalTitleAbbreviation);
                    eFetch.setJournal(journal);

                    if (article.element("Journal").element("JournalIssue")!=null){
                        Element pubDate = article.element("Journal").element("JournalIssue").element("PubDate");
                        String year = pubDate.elementText("Year");
                        if(year==null){
                            // TUDO
                            year = "2018";
                        }
                        String month = pubDate.elementText("Month");
                        if(month!=null){
                            month =String.valueOf( MonthConverter.convert(month));
                        }else {
                            month = "1";
                        }
                        String day = pubDate.elementText("Day");
                        if(day==null){
                            day="1";
                        }
                        eFetch.setPublishDate(year+"-"+month+"-"+day);

                    }
                }
                String articleTitle = article.elementText("ArticleTitle");
                eFetch.setArticleTitle(articleTitle);

                if(article.element("AuthorList")!=null){
                    List<Element> authors = article.element("AuthorList").elements("Author");
                    List<Author> authorList = new ArrayList<>();
                    for (Element item: authors){
                        Author author = new Author();
                        author.setLastName(item.elementText("LastName"));
                        author.setForeName(item.elementText("ForeName"));
                        author.setInitials(item.elementText("Initials"));
                        if(item.element("AffiliationInfo")!=null){
                            author.setAffiliation(item.element("AffiliationInfo").elementText("Affiliation"));
                        }

                        authorList.add(author);
                    }
                    eFetch.setAuthors(authorList);
                }
            }


            Element pubmedData = element.element("PubmedData");
            if(pubmedData!=null){
                if(pubmedData.element("ArticleIdList")!=null){
                    List<Element> articleIds = pubmedData.element("ArticleIdList").elements("ArticleId");
                    for (Element articleId: articleIds){
                        String idType = articleId.attributeValue("IdType");
                        String id = articleId.getText();
                        if(idType.equals("pmc")){
                            eFetch.setPmc(id);
                        } else if (idType.equals("doi")) {
                            eFetch.setDoi(id);
                        }
                    }

                }
                if(pubmedData.element("ReferenceList")!=null){
                    List<Element> references = pubmedData.element("ReferenceList").elements("Reference");
                    List<Integer> rsreferencePids = new ArrayList<>();
                    for (Element item:references){
                        if(item.element("ArticleIdList")!=null){
                            List<Element> articleIds = item.element("ArticleIdList").elements("ArticleId");
                            for (Element articleId: articleIds){
                                String idType = articleId.attributeValue("IdType");
                                String id = articleId.getText();
                                if(idType.equals("pubmed")){
                                    rsreferencePids.add(Integer.parseInt(id));
                                    break;
                                }
                            }
                        }

                    }
                    eFetch.setReferencePids(rsreferencePids);
                }
            }








            fetches.add(eFetch);
        }


//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new InputSource(new StringReader(responseText)));
//            System.out.println(document);
////            Document document = DocumentHelper.parseText(xmlString);
////
////            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
////            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
////            Document document = dBuilder.parse(new InputSource(new StringReader(responseText)));
////            Element rootElement = document.getDocumentElement();
////            NodeList nodeList = rootElement.getElementsByTagName("PubmedArticle");
//
//            System.out.println();


//            return eSearch;

            return fetches;

    }


//        System.out.println(eSearchList);


//        List<PubMed> pubMedList = new ArrayList<>();

//        int size = eSearch.getEsearchresult().getIdlist().size();
//        System.out.println(size);
//        initDataBase();
//        log.info("EXECUTING : command line runner");
//        Taxonomy taxonomy = new Taxonomy();
//        taxonomy.setName("sssssss");
//        taxonomyServices.save(taxonomy);
//        List<Taxonomy> taxonomies = taxonomyServices.listAll();
//        System.out.println(taxonomies);
//
//        for (int i = 0; i < args.length; ++i) {
//            log.info("args[{}]: {}", i, args[i]);
//        }

    public static  List<TaxonomyNodes> tsvToTaxonomyNodes(String filePath) {
        // 创建 CsvParserSettings 对象
        CsvParserSettings settings = new CsvParserSettings();

        // 设置分隔符
        settings.getFormat().setDelimiter('|');

        // 创建 CsvParser 对象
        CsvParser parser = new CsvParser(settings);

        // 读取文件并解析
        parser.beginParsing(new File(filePath));

        String[] row;
        List<TaxonomyNodes> taxonomyNamesList = new ArrayList<>();
        while ((row = parser.parseNext()) != null) {
            // 处理每一行数据
//            for (String value : row) {
//                System.out.println(value);
//            }
            TaxonomyNodes taxonomyNodes = new TaxonomyNodes();
            taxonomyNodes.setId(Integer.parseInt(row[0]));
            taxonomyNodes.setParentId(Integer.parseInt(row[1]));
            taxonomyNodes.setRank(row[2]);


//            taxonomyNames.setTaxId(Integer.parseInt(row[0]));
//            taxonomyNames.setNameTxt(row[1]);
//            taxonomyNames.setUniqueName(row[2]);
//            taxonomyNames.setNameClass(row[3]);
            taxonomyNamesList.add(taxonomyNodes);
        }

        // 关闭 CsvParser
        parser.stopParsing();
        return  taxonomyNamesList;
    }

    public static  List<TaxonomyNames> tsvToTaxonomyNames( String filePath) {
        // 创建 CsvParserSettings 对象
        CsvParserSettings settings = new CsvParserSettings();

        // 设置分隔符
        settings.getFormat().setDelimiter('|');

        // 创建 CsvParser 对象
        CsvParser parser = new CsvParser(settings);

        // 读取文件并解析
        parser.beginParsing(new File(filePath));

        String[] row;
        List<TaxonomyNames> taxonomyNamesList = new ArrayList<>();
        while ((row = parser.parseNext()) != null) {
            // 处理每一行数据
//            for (String value : row) {
//                System.out.println(value);
//            }
            TaxonomyNames taxonomyNames = new TaxonomyNames();
            taxonomyNames.setTaxId(Integer.parseInt(row[0]));
            taxonomyNames.setNameTxt(row[1]);
            taxonomyNames.setUniqueName(row[2]);
            taxonomyNames.setNameClass(row[3]);
            if(taxonomyNames.getNameClass().equals("scientific name")){
                taxonomyNames.setIsScientific(true);
            }else {
                taxonomyNames.setIsScientific(false);
            }

            taxonomyNamesList.add(taxonomyNames);
        }

        // 关闭 CsvParser
        parser.stopParsing();
        return  taxonomyNamesList;
    }





//    public static <DOMAIN> List<DOMAIN> tsvToBean(Class<DOMAIN> clz, String filePath) {
//        try {
//            FileInputStream inputStream = new FileInputStream(filePath);
//            Throwable var3 = null;
//
//            List var8;
//            try {
//                BeanListProcessor<DOMAIN> beanListProcessor = new BeanListProcessor(clz);
//                TsvParserSettings settings = new TsvParserSettings();
//                settings.getFormat().setLineSeparator("|");
//                settings.setProcessor(beanListProcessor);
//                settings.setHeaderExtractionEnabled(true);
//                TsvParser parser = new TsvParser(settings);
//                parser.parse(inputStream);
//                List<DOMAIN> beans = beanListProcessor.getBeans();
//                inputStream.close();
//                var8 = beans;
//            } catch (Throwable var18) {
//                var3 = var18;
//                throw var18;
//            } finally {
//                if (inputStream != null) {
//                    if (var3 != null) {
//                        try {
//                            inputStream.close();
//                        } catch (Throwable var17) {
//                            var3.addSuppressed(var17);
//                        }
//                    } else {
//                        inputStream.close();
//                    }
//                }
//
//            }
//
//            return var8;
//        } catch (IOException var20) {
//            var20.printStackTrace();
//            return null;
//        }
//    }
}
