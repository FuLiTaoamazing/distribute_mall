package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.BrandEntity;
import com.flt.product.entity.CategoryBrandRelationEntity;
import com.flt.product.entity.CategoryEntity;
import com.flt.product.vo.BrandVO;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> categoryEntityList(Long brandId);


    void updateBrand(String brandName,Long brandId);

    void updateCategory(String categoryName,Long catelogId);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

