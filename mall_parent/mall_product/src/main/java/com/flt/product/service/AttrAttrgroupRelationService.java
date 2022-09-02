package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.AttrAttrgroupRelationEntity;
import com.flt.product.vo.AttrRelationGroupVO;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
public interface        AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void  saveBatch(List<AttrRelationGroupVO> vos);
}

