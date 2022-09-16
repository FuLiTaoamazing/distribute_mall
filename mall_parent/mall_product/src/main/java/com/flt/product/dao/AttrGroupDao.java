package com.flt.product.dao;

import com.flt.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.product.vo.SkuItemVO;
import com.flt.product.vo.SpuItemBaseAttrVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemBaseAttrVO> getAttrGroupWithAttrsBySpuId(@Param("spuId")Long spuId, @Param("catelogId") Long catelogId);
}
