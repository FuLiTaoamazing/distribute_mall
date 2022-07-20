package com.flt.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.order.entity.RefundInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款信息
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:38:29
 */
@Mapper
public interface RefundInfoDao extends BaseMapper<RefundInfoEntity> {
	
}
