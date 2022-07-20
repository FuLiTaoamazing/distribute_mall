package com.flt.product.dao;

import com.flt.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
