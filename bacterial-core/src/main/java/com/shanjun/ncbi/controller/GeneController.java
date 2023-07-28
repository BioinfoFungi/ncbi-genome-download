package com.shanjun.ncbi.controller;

import com.shanjun.ncbi.pojo.Gene;
import com.shanjun.ncbi.pojo.Taxonomy;
import com.shanjun.ncbi.services.IGeneService;
import com.shanjun.ncbi.services.ITaxonomyService;
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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequestMapping("/gene")
@Slf4j
public class GeneController {
    @Autowired
    IGeneService geneService;

    @GetMapping("list")
    @Anonymous
    public String edit(Model model, @PageableDefault(sort = {"id"},direction = DESC) Pageable pageable, HttpServletRequest request, Gene gene, String keywords){
        Set<String> key = new HashSet<>();
        Page<Gene> taxonomies = geneService.pageBy(pageable,gene,keywords,key);
        model.addAttribute("view", taxonomies);
        String param = RequestUtils.getParam(request);
        model.addAttribute("paramUrl", param);
        model.addAttribute("totalElements",taxonomies.getTotalElements());
        return "tf:gene/list";
    }
}
