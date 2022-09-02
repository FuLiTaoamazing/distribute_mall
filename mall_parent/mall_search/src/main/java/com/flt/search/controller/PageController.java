package com.flt.search.controller;

import com.flt.search.service.MallSearchService;
import com.flt.search.vo.SearchParamsVO;
import com.flt.search.vo.SearchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String page(SearchParamsVO vo, Model model) {
        SearchResultVO result = mallSearchService.search(vo);
        model.addAttribute("result", result);
        return "list";
    }
}
