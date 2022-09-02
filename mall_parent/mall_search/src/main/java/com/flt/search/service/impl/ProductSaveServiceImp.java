package com.flt.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.flt.common.to.es.SkuEsModeTO;
import com.flt.search.config.MallElasticSearchConfig;
import com.flt.search.constant.EsConstant;
import com.flt.search.service.ProductSaveService;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSaveServiceImp implements ProductSaveService {
    private static final Logger logger = LoggerFactory.getLogger(ProductSaveServiceImp.class);
    @Autowired
    RestHighLevelClient client;

    @Override
    public boolean productUp(List<SkuEsModeTO> skuEsModeTOList) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModeTO skuEsModeTO : skuEsModeTOList) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModeTO.getSkuId().toString());
            String s = JSON.toJSONString(skuEsModeTO);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        // TODO 如果Es服务出错
        BulkResponse bulk = client.bulk(bulkRequest, MallElasticSearchConfig.defaultOptions());
        boolean b = bulk.hasFailures();

        if (!b) {
            List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
            logger.info("上架完成{}", collect);
        }

        return b;
    }
}
