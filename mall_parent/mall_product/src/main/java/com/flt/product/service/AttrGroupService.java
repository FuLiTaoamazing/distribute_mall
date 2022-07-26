package com.flt.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.product.entity.AttrGroupEntity;
import com.flt.product.vo.AttrGroupWithAttrVO;
import com.flt.product.vo.SkuItemVO;
import com.flt.product.vo.SpuItemBaseAttrVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrVO> getAttrGroupWithAttr(Long catelogId);

    List<SpuItemBaseAttrVO> getAttrGroupWithAttrsBySpuId(Long spuId, Long catelogId);
}

