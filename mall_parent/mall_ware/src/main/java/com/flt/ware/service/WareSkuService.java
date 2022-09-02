package com.flt.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.ware.entity.WareSkuEntity;
import com.flt.ware.vo.SkuHasStockVO;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:41:09
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds);
}

