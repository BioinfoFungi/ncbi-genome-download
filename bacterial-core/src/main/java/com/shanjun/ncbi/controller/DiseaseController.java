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
import org.checkerframework.checker.units.qual.A;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
@Controller
@RequestMapping("/disease")
@Slf4j
public class DiseaseController {
    @Autowired
    IDiseaseService diseaseService;

    @Autowired
    ITaxonomyService taxonomyService;

    @Autowired
    IDiseaseTaxonomyService diseaseTaxonomyService;

    @Autowired
    IPubMedService pubMedService;

    @GetMapping("list")
    @Anonymous
    public String list(Model model, @PageableDefault(sort = {"id"},direction = DESC) Pageable pageable, HttpServletRequest request, Disease disease, String keywords){
        Set<String> key = new HashSet<>();
        Page<Disease> taxonomies = diseaseService.pageBy(pageable,disease,keywords,key);
        model.addAttribute("view", taxonomies);
        String param = RequestUtils.getParam(request);
        model.addAttribute("paramUrl", param);
        model.addAttribute("totalElements",taxonomies.getTotalElements());
        return "tf:disease/list";
    }

    @GetMapping("/find/{id}")
    @Anonymous
    public String find(@PathVariable("id") Integer id, Model model){
        Disease disease = diseaseService.findById(id);
//        diseaseTaxonomyService.findById()
        List<DiseaseTaxonomy> diseaseTaxonomies = diseaseTaxonomyService.listByDiseaseId(disease.getId());
        Set<Integer> taxonomyIds = ServiceUtil.fetchProperty(diseaseTaxonomies, DiseaseTaxonomy::getTaxonomyId);
        List<Taxonomy> taxonomies = taxonomyService.listByIds(taxonomyIds);
        Map<Integer, Taxonomy> taxonomyMap = ServiceUtil.convertToMap(taxonomies, Taxonomy::getId);

        Set<Integer> literatureIds = ServiceUtil.fetchProperty(diseaseTaxonomies, DiseaseTaxonomy::getLiteratureId);
        List<PubMed> pubMedList = pubMedService.listByIds(literatureIds);
        Map<Integer, PubMed> pubMedMap = ServiceUtil.convertToMap(pubMedList, PubMed::getId);

        List<DiseaseTaxonomyVo> taxonomyVos = diseaseTaxonomies.stream().map(item -> {
            DiseaseTaxonomyVo diseaseTaxonomyVo = new DiseaseTaxonomyVo();
            Taxonomy taxonomy = taxonomyMap.get(item.getTaxonomyId());
            PubMed pubMed = pubMedMap.get(item.getLiteratureId());
            diseaseTaxonomyVo.setDescription(item.getDescription());
            diseaseTaxonomyVo.setTaxonomy(taxonomy);
            diseaseTaxonomyVo.setPubMed(pubMed);

            return diseaseTaxonomyVo;
        }).collect(Collectors.toList());


        model.addAttribute("disease",disease);
        model.addAttribute("taxonomyVos",taxonomyVos);
        return "tf:disease/details";
    }
}
