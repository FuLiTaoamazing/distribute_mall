package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author flt
 * @email fulitao1998@163.com
 * @date 2022-08-15 14:57:03
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

