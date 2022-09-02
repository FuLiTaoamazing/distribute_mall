package com.flt.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.flt.common.to.es.SkuEsModeTO;
import com.flt.search.config.MallElasticSearchConfig;
import com.flt.search.constant.EsConstant;
import com.flt.search.service.MallSearchService;
import com.flt.search.vo.SearchParamsVO;
import com.flt.search.vo.SearchResultVO;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImp implements MallSearchService {
    private static final Logger logger = LoggerFactory.getLogger(MallSearchServiceImp.class);
    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResultVO search(SearchParamsVO vo) {
        SearchRequest searchRequest = buildSearchRequest(vo);
        SearchResultVO result = null;
        try {
            SearchResponse response = client.search(searchRequest, MallElasticSearchConfig.defaultOptions());
            result = buildSearchResponse(response, vo.getPageNum(), vo.getKeyword());
        } catch (IOException e) {
            logger.error("elastic出错", e);
        }
        return result;
    }

    private SearchResultVO buildSearchResponse(SearchResponse response, Integer curPageNumber, String keyword) {
        SearchResultVO result = new SearchResultVO();
        SearchHits hits = response.getHits();
        // 设置商品
        List<SkuEsModeTO> esModeTOList = new ArrayList<>();
        if (hits.getHits() != null) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModeTO skuEsModeTO = JSON.parseObject(sourceAsString, SkuEsModeTO.class);
                if (StringUtils.isNotBlank(keyword)) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String s = skuTitle.getFragments()[0].toString();
                    skuEsModeTO.setSkuTitle(s);
                }
                esModeTOList.add(skuEsModeTO);
            }
        }
        result.setProducts(esModeTOList);

        // 获取商品所涉及聚合信息
        // 1.设置分类信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<SearchResultVO.CatalogVO> catalogVOS = new ArrayList<>();
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResultVO.CatalogVO catalogVO = new SearchResultVO.CatalogVO();
            String catalogId = bucket.getKeyAsString();
            catalogVO.setCatalogId(Long.parseLong(catalogId));
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVO.setCatalogName(catalogName);
            catalogVOS.add(catalogVO);
        }
        // 2设置品牌信息
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        List<SearchResultVO.BrandVO> brandVOS = new ArrayList<>();
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResultVO.BrandVO brandVO = new SearchResultVO.BrandVO();
            String brandId = bucket.getKeyAsString();
            brandVO.setBrandId(Long.parseLong(brandId));
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVO.setBrandImg(brandImg);
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVO.setBrandImg(brandName);
            brandVOS.add(brandVO);
        }
        result.setBrands(brandVOS);
        // 3.设置所有的属性
        ParsedNested attrAgg = response.getAggregations().get("attr_agg");
        List<SearchResultVO.AttrVO> attrVOS = new ArrayList<>();
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResultVO.AttrVO attrVO = new SearchResultVO.AttrVO();
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVO.setAttrId(attrId);
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVO.setAttrName(attrName);
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVO.setAttrValue(attrValues);
            attrVOS.add(attrVO);
        }

        result.setAttrs(attrVOS);
        result.setBrands(brandVOS);
        result.setCatalogs(catalogVOS);

        // 分页信息
        // 总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        // 总页码
        long totalPage = total % EsConstant.SEARCH_PAGE_SIZE == 0 ? (total / EsConstant.SEARCH_PAGE_SIZE) : (EsConstant.SEARCH_PAGE_SIZE) + 1;
        result.setTotalPages(totalPage);
        // 当前页码
        result.setPageNum(curPageNumber);
        return result;
    }

    private SearchRequest buildSearchRequest(SearchParamsVO parms) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();// 构建DSL语句
        /*
          模糊匹配，过滤(属性 价格 品牌 价格区间 库存）
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 模糊查询
        if (StringUtils.isNotBlank(parms.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", parms.getKeyword()));
        }
        // 构建filter
        if (parms.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catelogId", parms.getCatalog3Id()));
        }
        if (parms.getBrandId() != null && parms.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", parms.getBrandId()));
        }
        if (parms.getAttrs() != null && parms.getAttrs().size() > 0) {
            // TODO 按照所有指定的属性进行查询
            for (String attr : parms.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        // 按照库存
        boolQuery.filter(QueryBuilders.termQuery("hasStock", parms.getHasStock().compareTo(1) == 0));
        // 按照价格区间 _500 代表小于500 500_代表大于500 0_500
        if (StringUtils.isNotBlank(parms.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String skuPrice = parms.getSkuPrice();
            String[] split = skuPrice.split("_");
            if (split.length == 2) {
                rangeQuery.gte(split[0]);
                rangeQuery.lte(split[1]);
            } else {
                if (StringUtils.indexOf(skuPrice, "_") == 0) {
                    rangeQuery.lte(split[0]);
                    rangeQuery.gte(Integer.MIN_VALUE);
                } else {
                    rangeQuery.gte(split[0]);
                    rangeQuery.lte(Integer.MAX_VALUE);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        searchSourceBuilder.query(boolQuery);
        /*
            排序  高亮 分页
         */
        // 排序
        if (StringUtils.isNotBlank(parms.getSort())) {
            String sort = parms.getSort();
            String[] s = sort.split("_");
            SortOrder order = StringUtils.equals(s[1], "asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }
        // 分页
        searchSourceBuilder.from((parms.getPageNum() - 1) * EsConstant.SEARCH_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.SEARCH_PAGE_SIZE);
        // 高亮
        if (StringUtils.isNotBlank(parms.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /*
            聚合分析
         */
        // 品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        // 品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        // 分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catelogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catelogName").size(1));

        // 属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue"));
        attr_agg.subAggregation(attr_id_agg);


        searchSourceBuilder.aggregation(brand_agg);
        searchSourceBuilder.aggregation(catalog_agg);
        searchSourceBuilder.aggregation(attr_agg);

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }
}
