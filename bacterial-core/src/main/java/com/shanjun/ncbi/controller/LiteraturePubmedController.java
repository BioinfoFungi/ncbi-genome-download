package com.shanjun.ncbi.controller;

import com.shanjun.ncbi.pojo.PubMed;
import com.shanjun.ncbi.services.IPubMedService;
import com.shanjun.ncbi.uitls.RequestUtils;
import com.wangyang.pojo.annotation.Anonymous;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequestMapping("/literature")
@Slf4j
public class LiteraturePubmedController {


    @Autowired
    IPubMedService pubMedService;

    @GetMapping("list")
    @Anonymous
    public String list(Model model, @PageableDefault(sort = {"id"},direction = DESC) Pageable pageable, HttpServletRequest request, PubMed pubMed, String keywords){
        Set<String> key = new HashSet<>();
        Page<PubMed> taxonomies = pubMedService.pageBy(pageable,pubMed,keywords,key);
        model.addAttribute("view", taxonomies);
        String param = RequestUtils.getParam(request);
        model.addAttribute("paramUrl", param);
        model.addAttribute("totalElements",taxonomies.getTotalElements());
        return "tf:literature/list";
    }

    @GetMapping("/find/{id}")
    @Anonymous
    public String showFullText(@PathVariable("id") Integer id, Model model){
        PubMed pubMed = pubMedService.findById(id);
        model.addAttribute("view", pubMed);
        return "tf:literature/details";

    }
}
