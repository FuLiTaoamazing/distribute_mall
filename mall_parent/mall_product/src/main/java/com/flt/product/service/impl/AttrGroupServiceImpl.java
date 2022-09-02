package com.flt.product.service.impl;

import com.flt.product.dao.AttrDao;
import com.flt.product.entity.AttrEntity;
import com.flt.product.service.AttrService;
import com.flt.product.vo.AttrGroupWithAttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;

import com.flt.product.dao.AttrGroupDao;
import com.flt.product.entity.AttrGroupEntity;
import com.flt.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), new QueryWrapper<AttrGroupEntity>());

        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        // 如果catelogId为0就代表查询所有
        if (catelogId == null || catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), new QueryWrapper<AttrGroupEntity>());
            return new PageUtils(page);
        }
        // 按照三级分类来进行查询
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);
        String key = (String) params.get("key");
        // 当统配查询的Key不为空的时候
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<AttrGroupWithAttrVO> getAttrGroupWithAttr(Long catelogId) {
        // 查询分类下的所有分组
        List<AttrGroupEntity> allGroups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 查询所有属性
        List<AttrGroupWithAttrVO> vos = allGroups.stream().map(group -> {
            AttrGroupWithAttrVO vo = new AttrGroupWithAttrVO();
            BeanUtils.copyProperties(group, vo);
            List<AttrEntity> attrs = attrService.getRelationAttr(vo.getAttrGroupId());
            vo.setAttrs(attrs);
            return vo;
        }).collect(Collectors.toList());
        return vos;
    }


}