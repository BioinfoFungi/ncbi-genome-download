package com.shanjun.ncbi.controller;

import com.shanjun.ncbi.pojo.Disease;
import com.shanjun.ncbi.pojo.DiseaseTaxonomy;
import com.shanjun.ncbi.pojo.PubMed;
import com.shanjun.ncbi.pojo.Taxonomy;
import com.shanjun.ncbi.pojo.vo.DiseaseTaxonomyVo;
import com.shanjun.ncbi.services.IDiseaseService;
import com.shanjun.ncbi.services.IDiseaseTaxonomyService;
import com.shanjun.ncbi.services.IPubMedService;
import com.shanjun.ncbi.services.ITaxonomyService;
import com.shanjun.ncbi.uitls.RequestUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.annotation.Anonymous;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequestMapping("/taxonomy")
@Slf4j
public class TaxonomyController {



    @Autowired
    ITaxonomyService taxonomyServices;

    @Autowired
    IDiseaseTaxonomyService diseaseTaxonomyService;

    @Autowired
    IDiseaseService diseaseService;

    @Autowired
    IPubMedService pubMedService;


    @GetMapping("list")
    @Anonymous
    public String edit(Model model, @PageableDefault(sort = {"id"},direction = DESC) Pageable pageable,HttpServletRequest request,Taxonomy taxonomy,String keywords){
        Set<String> key = new HashSet<>();
        key.add("nameTxt");
        Page<Taxonomy> taxonomies = taxonomyServices.pageBy(pageable,taxonomy,keywords,key);
        model.addAttribute("view", taxonomies);
        String param = RequestUtils.getParam(request);
        model.addAttribute("paramUrl", param);
        model.addAttribute("totalElements",taxonomies.getTotalElements());
        return "tf:taxonomy/list";
    }


    @GetMapping("/find/{id}")
    @Anonymous
    public String find(@PathVariable("id") Integer id,Model model){
        Taxonomy taxonomy = taxonomyServices.findById(id);
//        diseaseTaxonomyService.findById()
        List<DiseaseTaxonomy> diseaseTaxonomies = diseaseTaxonomyService.listByTaxonomyId(taxonomy.getId());
        Set<Integer> diseaseIds = ServiceUtil.fetchProperty(diseaseTaxonomies, DiseaseTaxonomy::getDiseaseId);
        List<Disease> diseases = diseaseService.listByIds(diseaseIds);
        Map<Integer, Disease> diseaseMap = ServiceUtil.convertToMap(diseases, Disease::getId);

        Set<Integer> literatureIds = ServiceUtil.fetchProperty(diseaseTaxonomies, DiseaseTaxonomy::getLiteratureId);
        List<PubMed> pubMedList = pubMedService.listByIds(literatureIds);
        Map<Integer, PubMed> pubMedMap = ServiceUtil.convertToMap(pubMedList, PubMed::getId);

        List<DiseaseTaxonomyVo> taxonomyVos = diseaseTaxonomies.stream().map(item -> {
            DiseaseTaxonomyVo diseaseTaxonomyVo = new DiseaseTaxonomyVo();
            Disease disease = diseaseMap.get(item.getDiseaseId());
            PubMed pubMed = pubMedMap.get(item.getLiteratureId());
            diseaseTaxonomyVo.setDescription(item.getDescription());
            diseaseTaxonomyVo.setDisease(disease);
            diseaseTaxonomyVo.setPubMed(pubMed);

            return diseaseTaxonomyVo;
        }).collect(Collectors.toList());


        model.addAttribute("taxonomy",taxonomy);
        model.addAttribute("taxonomyVos",taxonomyVos);
        return "tf:taxonomy/details";
    }

}
