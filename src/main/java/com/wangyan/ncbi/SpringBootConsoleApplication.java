package com.wangyan.ncbi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.wangyan.ncbi.entrez.EFetch;
import com.wangyan.ncbi.entrez.ESearch;
import com.wangyan.ncbi.pojo.*;
import com.wangyan.ncbi.services.*;
import com.wangyang.common.utils.File2Tsv;
import com.wangyang.common.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
public class SpringBootConsoleApplication  implements CommandLineRunner {
//    private static Logger LOG = LoggerFactory
//            .getLogger(SpringBootConsoleApplication.class);





    @Autowired
    IPubMedService pubMedService;

    @Autowired
    IJournalService journalService;
    @Autowired
    INCBIService ncbiService;

    @Autowired
    IReferenceService referenceService;

    @Autowired
    IAuthorService authorService;

    @Autowired
    IPubMedAuthorService pubMedAuthorService;


    @Autowired
    INLPService nlpService;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringBootConsoleApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
//载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/kerongao/article/details/109576388
//        log.info("STARTING THE APPLICATION");
//        SpringApplication.run(SpringBootConsoleApplication.class, args);
//        log.info("APPLICATION FINISHED");
    }

    public void runPubmed() throws DocumentException, IOException, ParseException {
        List<PubMed> pubMedList = pubMedService.listAllNoEFetch();
        Map<Integer, PubMed> pubMedMap = ServiceUtil.convertToMap(pubMedList, PubMed::getPId);
        Set<Integer> pids = ServiceUtil.fetchProperty(pubMedList, PubMed::getPId);
        Iterator<Integer> iterator = pids.iterator();
        int batchSize = 1000;
        // 遍历Set并分批处理
        while (iterator.hasNext()) {
            // 创建一个新的批次
            Set<Integer> batch = new HashSet<>();

            // 在当前批次中添加元素
            for (int i = 0; i < batchSize && iterator.hasNext(); i++) {
                batch.add(iterator.next());
            }
            List<EFetch> fetches = ncbiService.spiderEFetch(batch, 10000);
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
    }


    public  void runEFetch(){
        int maxRetries = 5;
        int retryCount = 0;
        boolean success = false;

        while (retryCount < maxRetries && !success) {
            try {
                // 执行可能抛出异常的操作
                runPubmed();
                success = true; // 如果没有抛出异常，则表示操作成功
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("操作失败，重试次数: " + (retryCount + 1));
                retryCount++;
            }
        }

        if (success) {
            System.out.println("操作成功");
        } else {
            System.out.println("操作失败");
        }
    }

    @Override
    public void run(String... args) throws ParseException {
//        runEFetch();

        nlpService.ParagraphVectors();
//        nlpService.deepLearning1();

    }


}
