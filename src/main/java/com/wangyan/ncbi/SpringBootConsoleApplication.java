package com.wangyan.ncbi;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.wangyan.ncbi.pojo.Taxonomy;
import com.wangyan.ncbi.pojo.TaxonomyNames;
import com.wangyan.ncbi.services.ITaxonomyNamesService;
import com.wangyan.ncbi.services.ITaxonomyServices;
import com.wangyang.common.utils.File2Tsv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
public class SpringBootConsoleApplication  implements CommandLineRunner {
//    private static Logger LOG = LoggerFactory
//            .getLogger(SpringBootConsoleApplication.class);



    @Autowired
    ITaxonomyServices taxonomyServices;

    @Autowired
    ITaxonomyNamesService taxonomyNamesService;

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

    @Override
    public void run(String... args) {
        List<TaxonomyNames> taxonomyNames = tsvToBean( "/home/wangyang/workspace/ncbi-genome-download/names.dmp");
        taxonomyNamesService.truncateTable();
        taxonomyNamesService.saveAll(taxonomyNames);

        log.info("EXECUTING : command line runner");
        Taxonomy taxonomy = new Taxonomy();
        taxonomy.setName("sssssss");
        taxonomyServices.save(taxonomy);
        List<Taxonomy> taxonomies = taxonomyServices.listAll();
        System.out.println(taxonomies);

        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }
    }
    public static  List<TaxonomyNames> tsvToBean( String filePath) {
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
            taxonomyNamesList.add(taxonomyNames);
        }

        // 关闭 CsvParser
        parser.stopParsing();

        return  taxonomyNamesList;

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
