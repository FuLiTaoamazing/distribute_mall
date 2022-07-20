package com.flt.member.dao;

import com.flt.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:27:39
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
