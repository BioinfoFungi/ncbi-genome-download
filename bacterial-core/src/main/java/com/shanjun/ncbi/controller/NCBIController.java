package com.shanjun.ncbi.controller;

import com.shanjun.ncbi.entrez.EFetch;
import com.shanjun.ncbi.pojo.PubMed;
import com.shanjun.ncbi.services.INCBIService;
import com.shanjun.ncbi.services.IPubMedService;
import com.shanjun.ncbi.services.impl.NCBIServiceImpl;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.pojo.annotation.Anonymous;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/ncbi")
@Slf4j
public class NCBIController {

    @Autowired
    INCBIService ncbiService;

    @Autowired
    IPubMedService pubMedService;

    @GetMapping("/addPubmed")
    @Anonymous
    public BaseResponse addPubmed(Integer pmid){
        PubMed pubMed = pubMedService.findByPMID(pmid);
        if(pubMed==null){
            pubMed = new PubMed();
            pubMed.setPId(pmid);
            pubMedService.save(pubMed);
        }
        List<PubMed> pubMedList = Arrays.asList(pubMed);
        if(pubMed.getIsEFetch()==null || (pubMed.getIsEFetch()!=null && !pubMed.getIsEFetch())){
            ncbiService.runPubmed(pubMedList,1);
        }

        if(pubMed.getPmc()!=null){
            ncbiService.runPMC(pubMedList,1);
        }

        return BaseResponse.ok("success!");
    }

    @GetMapping("/efEtchAbstract")
    @Anonymous
    public BaseResponse efEtchAbstract(Integer size){
        if (NCBIServiceImpl.isAlreadyRun){
            throw new ObjectException("efEtchAbstract 已经运行！");
        }
        if(size==null){
            size=100;
        }
        ncbiService.runPubmed(size);
        return BaseResponse.ok("success!");
    }

    @GetMapping("/efEtchPMC")
    @Anonymous
    public BaseResponse efEtchPMC(Integer size){
        if (NCBIServiceImpl.isPMCAlreadyRun){
            throw new ObjectException("efEtchAbstract 已经运行！");
        }
        if(size==null){
            size=100;
        }
        ncbiService.runPMC(size);
        return BaseResponse.ok("success!");
    }


    public  void runEFetch(){
        int maxRetries = 5;
        int retryCount = 0;
        boolean success = false;

        while (retryCount < maxRetries && !success) {
            try {
                // 执行可能抛出异常的操作
                ncbiService.runPubmed(1000);
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
}
