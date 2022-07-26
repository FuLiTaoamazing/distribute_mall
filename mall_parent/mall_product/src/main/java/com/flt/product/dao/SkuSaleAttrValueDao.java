package com.flt.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.product.entity.SkuSaleAttrValueEntity;
import com.flt.product.vo.SkuItemSaleAttrVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author flt
 * @email fulitao1998@163.com
 * @date 2022-08-15 14:57:03
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVO> getSaleAttrsBySpuId(@Param("spuId") Long spuId);
}
