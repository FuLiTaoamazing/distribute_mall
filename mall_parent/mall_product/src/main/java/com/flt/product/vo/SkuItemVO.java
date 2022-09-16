package com.flt.product.vo;

import com.flt.product.entity.SkuImagesEntity;
import com.flt.product.entity.SkuInfoEntity;
import com.flt.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVO {
    private SkuInfoEntity info;
    private List<SkuImagesEntity> images;
    private SpuInfoDescEntity desp;
    private List<SkuItemSaleAttrVO> saleAttr;
    private List<SpuItemBaseAttrVO> groupAttrs;
    private boolean hasStock=true;

}
