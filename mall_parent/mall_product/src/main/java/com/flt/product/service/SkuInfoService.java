package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.SkuInfoEntity;
import com.flt.product.vo.SkuItemVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author flt
 * @email fulitao1998@163.com
 * @date 2022-08-15 14:57:03
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException;
}

