package com.shanjun;

import com.shanjun.ncbi.entrez.EFetch;
import com.shanjun.ncbi.pojo.Author;
import com.shanjun.ncbi.pojo.Journal;
import com.shanjun.ncbi.pojo.PubMed;
import com.shanjun.ncbi.pojo.*;
import com.shanjun.ncbi.services.*;
import com.wangyang.common.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;

import org.dom4j.DocumentException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication
@Slf4j
public class SpringBootConsoleApplication implements CommandLineRunner {
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
        ncbiService.initTaxonomyDB();
//        runEFetch();

//        nlpService.ParagraphVectors();
//        nlpService.deepLearning1();

//        File gModel = new File("/home/wangyang/workspace/ncbi-genome-download/GoogleNews-vectors-negative300.bin.gz");
//        Word2Vec vec = WordVectorSerializer.readWord2VecModel(gModel);
//        Collection<String> lst = vec.wordsNearest("day", 10);
//        System.out.println(lst);
//        Collection<String> kingList = vec.wordsNearest(Arrays.asList("king", "woman"), Arrays.asList("queen"), 10);
//        System.out.println(kingList);

//
//        try {
//            SentenceIterator iter = new LineSentenceIterator(new File("/home/wangyang/workspace/ncbi-genome-download/raw_sentences.txt"));
//            iter.setPreProcessor(new SentencePreProcessor() {
//                @Override
//                public String preProcess(String sentence) {
//                    return sentence.toLowerCase();
//                }
//            });
//
//            // 数据分词
//            // 在每行中按空格分词
//            TokenizerFactory t = new DefaultTokenizerFactory();
//            t.setTokenPreProcessor(new CommonPreprocessor());
//            log.info("Building model....");
//            /**
//             * batchSize是每次处理的词的数量。
//             * minWordFrequency是一个词在语料中必须出现的最少次数。本例中出现不到五次的词都不予学习。词语必须在多种上下文中出现，才能让模型学习到有用的特征。对于规模很大的语料库，理应提高出现次数的下限。
//             * useAdaGrad－AdaGrad为每个特征生成一个不同的梯度。在此处不需要考虑。
//             * layerSize指定词向量中的特征数量，与特征空间的维度数量相等。以500个特征值表示的词会成为一个500维空间中的点。
//             * iterations是网络在处理一批数据时允许更新系数的次数。迭代次数太少，网络可能来不及学习所有能学到的信息；迭代次数太多则会导致网络定型时间变长。
//             * learningRate是每一次更新系数并调整词在特征空间中的位置时的步幅。
//             * minLearningRate是学习速率的下限。学习速率会随着定型词数的减少而衰减。如果学习速率下降过多，网络学习将会缺乏效率。这会使系数不断变化。
//             * iterate告知网络当前定型的是哪一批数据集。
//             * tokenizer将当前一批的词输入网络。
//             * vec.fit()让已配置好的网络开始定型。
//             * */
//            Word2Vec vec = new Word2Vec.Builder()
//                    .minWordFrequency(5)
//                    .iterations(1)
//                    .layerSize(100)
//                    .seed(42)
//                    .windowSize(5)
//                    .iterate(iter)
//                    .tokenizerFactory(t)
//                    .build();
//
//            log.info("Fitting Word2Vec model....");
//            vec.fit();
//            // 下一步是评估特征向量的质量
//            // 编写词向量
//            WordVectorSerializer.writeWordVectors(vec, "pathToWriteto.txt");
//
//            log.info("Closest Words:");
//            Collection<String> lst = vec.wordsNearest("day", 10);
//            System.out.println(lst);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }


}
