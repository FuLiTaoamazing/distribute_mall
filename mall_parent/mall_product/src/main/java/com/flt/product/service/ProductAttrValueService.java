package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveImages(Long id, List<String> spuImages);

    void saveProductAttr(List<ProductAttrValueEntity> productAttrValueEntities);

    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);
}
