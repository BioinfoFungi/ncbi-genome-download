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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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
    ITaxonomyService taxonomyNamesService;

    @Autowired
    IReferenceService referenceService;
    @Autowired
    ITaxonomyNodesServices taxonomyNodesServices;

    @Autowired
    IDiseaseService diseaseService;

    @Autowired
    IJournalService journalService;


    @Autowired
    IAuthorService authorService;

    @Autowired
    IPubMedAuthorService pubMedAuthorService;


    public static Boolean isAlreadyRun=false;
    public static Boolean isPMCAlreadyRun=false;

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
        List<Taxonomy> taxonomyNames = tsvToTaxonomyNames( "/home/wangyang/workspace/ncbi-genome-download/names.dmp");
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
    public List<EFetch>    spiderEFetch(Set<Integer> ids, Integer retMax)  {
        try {
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


            return fetches;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }





    @Override
    public List<EFetch>    spiderPMCEFetch(Set<Integer> ids, Integer retMax)  {
        try {
            List<String> stringList = new ArrayList<>();
            for (Integer num : ids) {
                stringList.add(num.toString());
            }

            String joinIds = String.join(",", stringList);
            String apiKey = "8145c95381a416f75f7f3245637414470807";
            log.info(joinIds);

            String apiUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pmc&retmode=xml&&api_key=" + apiKey+"&retmax="+retMax;
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
                List<Element> journalMetas = element.element("front").elements();
                eFetch.setArticleFullText(element.asXML());
                for (Element journalMeta : journalMetas){
                    List<Element> articleIds = journalMeta.elements();
                    if(articleIds!=null){
                        for (Element articleId: articleIds){
                            String pubIdType = articleId.attributeValue("pub-id-type");
                            if(pubIdType!=null &&pubIdType.equals("pmid")){
                                String text = articleId.getText();
                                int pid = Integer.parseInt(text);
                                eFetch.setPId(pid);
                                break;
                            }

                        }

                    }

                }
                fetches.add(eFetch);
            }

            return fetches;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async
    public void runPMC(Integer batchSize)  {
        List<PubMed> pubMedList = pubMedService.listAllNoPMCEFetch();
        runPMC(pubMedList,batchSize);

    }
    @Override
    public void runPMC(List<PubMed> pubMedList,Integer batchSize)  {
        this.isPMCAlreadyRun = true;
        try {

            Map<Integer, PubMed> pubMedMap = ServiceUtil.convertToMap(pubMedList, PubMed::getPId);
            Set<String> pids = ServiceUtil.fetchProperty(pubMedList, PubMed::getPmc);
            Set<Integer> pmc = pids.stream().map(item->Integer.parseInt(item.replace("PMC",""))).collect(Collectors.toSet());

            Iterator<Integer> iterator = pmc.iterator();

            // 遍历Set并分批处理
            while (iterator.hasNext()) {
                // 创建一个新的批次
                Set<Integer> batch = new HashSet<>();

                // 在当前批次中添加元素
                for (int i = 0; i < batchSize && iterator.hasNext(); i++) {
                    batch.add(iterator.next());
                }
                List<EFetch> fetches = spiderPMCEFetch(batch, 10000);
                System.out.println();
                for (EFetch eFetch : fetches) {
                    PubMed pubMed = pubMedMap.get(eFetch.getPId());
                    pubMed.setIsPMCEFetch(true);
                    pubMed.setArticleFullText(eFetch.getArticleFullText());
                    pubMedService.save(pubMed);

                }

                System.out.println(pubMedService.listAllNoEFetch().size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.isPMCAlreadyRun =false;
        }
    }





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

    public static  List<Taxonomy> tsvToTaxonomyNames(String filePath) {
        // 创建 CsvParserSettings 对象
        CsvParserSettings settings = new CsvParserSettings();

        // 设置分隔符
        settings.getFormat().setDelimiter('|');

        // 创建 CsvParser 对象
        CsvParser parser = new CsvParser(settings);

        // 读取文件并解析
        parser.beginParsing(new File(filePath));

        String[] row;
        List<Taxonomy> taxonomyNamesList = new ArrayList<>();
        while ((row = parser.parseNext()) != null) {
            // 处理每一行数据
//            for (String value : row) {
//                System.out.println(value);
//            }
            Taxonomy taxonomyNames = new Taxonomy();
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




    @Override
    @Async
    public void runPubmed(Integer batchSize)  {
        List<PubMed> pubMedList = pubMedService.listAllNoEFetch();
        runPubmed(pubMedList,batchSize);

    }

    @Override
    public void runPubmed(List<PubMed> pubMedList, Integer batchSize)  {
        this.isAlreadyRun = true;
        try {
            Map<Integer, PubMed> pubMedMap = ServiceUtil.convertToMap(pubMedList, PubMed::getPId);
            Set<Integer> pids = ServiceUtil.fetchProperty(pubMedList, PubMed::getPId);
            Iterator<Integer> iterator = pids.iterator();

            // 遍历Set并分批处理
            while (iterator.hasNext()) {
                // 创建一个新的批次
                Set<Integer> batch = new HashSet<>();

                // 在当前批次中添加元素
                for (int i = 0; i < batchSize && iterator.hasNext(); i++) {
                    batch.add(iterator.next());
                }
                List<EFetch> fetches = spiderEFetch(batch, 10000);
                for (EFetch eFetch : fetches) {
                    PubMed pubMed = pubMedMap.get(eFetch.getPId());
                    if(pubMed!=null){
                        pubMed.setIsEFetch(true);
                        BeanUtils.copyProperties(eFetch, pubMed, "pId","publishDate");
                        String publishDate = eFetch.getPublishDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(publishDate);
                        pubMed.setPublishDate(date);
                        Journal journalInput = eFetch.getJournal();
                        Journal journal = journalService.findSave(journalInput);
                        pubMed.setJournalId( journal.getId());

                        List<Integer> referencePids = eFetch.getReferencePids();
                        if(referencePids!=null){
                            for (Integer id:referencePids ){
                                PubMed pubMedRef = pubMedMap.get(id);
                                if(pubMedRef!=null){
                                    referenceService.findSave(pubMed.getId(), pubMedRef.getId());
                                }
                            }

                        }

                        List<Author> authors = eFetch.getAuthors();
                        if(authors!=null){
                            for (Author authorInput: authors){
                                Author author = authorService.findSave(authorInput);
                                pubMedAuthorService.findSave(pubMed.getId(),author.getId());
                            }

                        }

                        pubMedService.save(pubMed);
                    }

                }
                System.out.println(pubMedService.listAllNoEFetch().size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.isAlreadyRun =false;
        }
    }

}
