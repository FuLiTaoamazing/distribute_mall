package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.AttrEntity;
import com.flt.product.vo.AttrRelationGroupVO;
import com.flt.product.vo.AttrRespVO;
import com.flt.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVO attr);

    PageUtils baseList(Long catelogId, Map<String, Object> params, String attrType);

    AttrRespVO attrInfo(Long attrId);

    void updateAttr(AttrVO attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrRelationGroupVO[] attrRelationGroupVO);

    PageUtils noattrRelationPage(Map<String, Object> params, Long attrgoupId);

    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

