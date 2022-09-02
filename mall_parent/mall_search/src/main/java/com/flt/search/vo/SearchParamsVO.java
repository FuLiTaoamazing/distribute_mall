package com.flt.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParamsVO {
    //关键字
    private String keyword;
    //三级分类ID
    private Long catalog3Id;
    //排序条件
    //sort=saleCont_asc/desc
    //sort=skuPrice_asc/desc
    //sor=hotScore_asc/desc
    private String sort;
    //hasStock=1/0 代表有货无货
    private Integer hasStock=0;
    //价格区间
    private String skuPrice;
    //品牌Id
    private List<Long> brandId;
    //按照属性进行搜索
    private List<String> attrs;
    private Integer pageNum=1;
}
