package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.CategoryEntity;
import com.flt.product.vo.Catelog2VO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    Long[] findCatelogPath(Long catelogId);
    //此方法更新保证冗余字段的一致性
    void updateDetails(CategoryEntity entity);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catelog2VO>> getCatalogJson();
}

