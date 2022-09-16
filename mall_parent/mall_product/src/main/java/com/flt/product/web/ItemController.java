package com.flt.product.web;

import com.flt.product.service.SkuInfoService;
import com.flt.product.vo.SkuItemVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model) {
        logger.info("准备查询当前{}skuId的信息", skuId);
        SkuItemVO vo = null;
        try {
            vo = skuInfoService.item(skuId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("item", vo);
        return "item";
    }
}
