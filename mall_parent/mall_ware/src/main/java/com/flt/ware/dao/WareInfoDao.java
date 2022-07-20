package com.flt.ware.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flt.ware.entity.WareInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:41:09
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
