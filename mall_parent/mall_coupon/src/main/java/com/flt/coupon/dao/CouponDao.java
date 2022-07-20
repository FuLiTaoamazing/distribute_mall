package com.flt.coupon.dao;

import com.flt.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:22:49
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
