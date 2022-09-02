package com.flt.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.product.entity.SkuInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * sku信息
 * 
 * @author flt
 * @email fulitao1998@163.com
 * @date 2022-08-15 14:57:03
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {
	
}
