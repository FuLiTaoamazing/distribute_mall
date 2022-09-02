package com.flt.search.controller;

import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.handler.codec.compression.Bzip2Decoder;
import com.flt.common.exception.BizCodeEnum;
import com.flt.common.to.es.SkuEsModeTO;
import com.flt.common.utils.R;
import com.flt.search.service.ProductSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSaveController.class);
    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productUp(@RequestBody List<SkuEsModeTO> skuEsModeTOList) {
        boolean b = false;
        try {
            b = productSaveService.productUp(skuEsModeTOList);

        } catch (IOException e) {
            logger.error("ElasticSaveController商品上架出错", e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }
        if (!b) {
            return R.ok();
        } else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }
    }

}
