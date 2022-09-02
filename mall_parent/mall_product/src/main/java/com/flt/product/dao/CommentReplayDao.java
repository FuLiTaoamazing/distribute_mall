package com.flt.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.product.entity.CommentReplayEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author flt
 * @email fulitao1998@163.com
 * @date 2022-08-15 14:57:03
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
