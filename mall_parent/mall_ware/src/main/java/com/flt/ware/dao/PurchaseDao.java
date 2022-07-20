package com.flt.ware.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.ware.entity.PurchaseEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:41:09
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {
	
}
