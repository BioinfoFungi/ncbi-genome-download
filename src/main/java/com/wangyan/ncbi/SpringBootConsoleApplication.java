package com.wangyan.ncbi;

import com.wangyan.ncbi.pojo.Taxonomy;
import com.wangyan.ncbi.services.ITaxonomyServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@Slf4j
public class SpringBootConsoleApplication  implements CommandLineRunner {
//    private static Logger LOG = LoggerFactory
//            .getLogger(SpringBootConsoleApplication.class);



    @Autowired
    ITaxonomyServices taxonomyServices;
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
}
