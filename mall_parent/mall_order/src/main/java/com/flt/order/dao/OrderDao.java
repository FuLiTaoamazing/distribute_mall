package com.flt.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:38:29
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
