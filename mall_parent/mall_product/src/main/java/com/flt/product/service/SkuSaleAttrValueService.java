package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.SkuSaleAttrValueEntity;
import com.flt.product.vo.SkuItemSaleAttrVO;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author flt
 * @email fulitao1998@163.com
 * @date 2022-08-15 14:57:03
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVO> getSaleAttrsBySpuId(Long spuId);
}

