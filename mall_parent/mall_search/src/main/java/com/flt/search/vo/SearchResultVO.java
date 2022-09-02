package com.flt.search.vo;

import com.flt.common.to.es.SkuEsModeTO;
import lombok.Data;

import java.util.List;

@Data
public class SearchResultVO {
    private List<SkuEsModeTO> products;
    private Integer pageNum;
    private Long total;
    private Long totalPages;
    private List<BrandVO> brands;//所涉及到的品牌
    private List<AttrVO> attrs;//当前查询到的结果所涉及到的属性
    private List<CatalogVO> catalogs;//当前查询到的结果所涉及到的属性


    @Data
    public static class BrandVO{
        private Long brandId;
        private String brandImg;
        private String brandName;
    }


    @Data
    public static class AttrVO{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
    @Data
    public static class CatalogVO{
     private Long catalogId;
     private String catalogName;
    }
}
