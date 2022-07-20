package com.flt.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.order.entity.OrderReturnApplyEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单退货申请
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:38:29
 */
@Mapper
public interface OrderReturnApplyDao extends BaseMapper<OrderReturnApplyEntity> {
	
}
