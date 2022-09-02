package com.flt.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flt.common.constant.ProductConstant;
import com.flt.product.dao.AttrAttrgroupRelationDao;
import com.flt.product.dao.AttrGroupDao;
import com.flt.product.dao.CategoryDao;
import com.flt.product.entity.AttrAttrgroupRelationEntity;
import com.flt.product.entity.AttrGroupEntity;
import com.flt.product.entity.CategoryEntity;
import com.flt.product.service.CategoryService;
import com.flt.product.vo.AttrRelationGroupVO;
import com.flt.product.vo.AttrRespVO;
import com.flt.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;

import com.flt.product.dao.AttrDao;
import com.flt.product.entity.AttrEntity;
import com.flt.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttr(AttrVO attr) {
        // 保存基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        // 保存关联关系 只有基本属性需要关联关系
        if (attr.getAttrType() == ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode()&&attr.getAttrGroupId()!=null) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }


    @Override
    public PageUtils baseList(Long catelogId, Map<String, Object> params, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        queryWrapper.eq("attr_type", "base".equals(attrType) ? ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrTypeEnum.ATTR_TYPE_SALE.getCode());
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVO> respVOS = records.stream().map(attr -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attr, attrRespVO);
            // 查询类型名称
            CategoryEntity categoryEntity = categoryDao.selectById(attr.getCatelogId());
            attrRespVO.setCatelogName(categoryEntity.getName());
            // 查询分组名称
            // 1.先从中间表中查出分组的ID
            if (StringUtils.equals("base", attrType)) {
                AttrAttrgroupRelationEntity attrById = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
                if (attrById != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrById.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }

            return attrRespVO;
        }).collect(Collectors.toList());
        pageUtils.setList(respVOS);
        return pageUtils;
    }


    @Override
    public AttrRespVO attrInfo(Long attrId) {
        AttrRespVO attrRespVO = new AttrRespVO();
        AttrEntity attr = baseMapper.selectById(attrId);
        BeanUtils.copyProperties(attr, attrRespVO);
        // 查出分组的ID
        if (attr.getAttrId() == ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrGr = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrGr != null) {
                attrRespVO.setAttrGroupId(attrGr.getAttrGroupId());
                AttrGroupEntity group = attrGroupDao.selectById(attrGr.getAttrGroupId());
                if (group != null) {
                    attrRespVO.setGroupName(group.getAttrGroupName());
                }
            }
        }
        // 查出分类的所有路径
        Long catelogId = attr.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVO.setCatelogPath(catelogPath);
        return attrRespVO;
    }


    @Override
    @Transactional
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);
        // 1.修改分组 只有基本属性才需要修改分组
        if (attr.getAttrType() == ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity updateRelation = new AttrAttrgroupRelationEntity();
            updateRelation.setAttrGroupId(attr.getAttrGroupId());
            updateRelation.setAttrId(attr.getAttrId());

            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationDao.update(updateRelation, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                // 做新增操作
                attrAttrgroupRelationDao.insert(updateRelation);
            }
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        if (relations == null || relations.size() == 0) {
            return null;
        }
        List<Long> attrIds = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public void deleteRelation(AttrRelationGroupVO[] vos) {
        List<AttrAttrgroupRelationEntity> collect = Arrays.stream(vos).map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(collect);
    }

    @Override
    public PageUtils noattrRelationPage(Map<String, Object> params, Long attrgoupId) {
        // 找到当前分组所属分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgoupId);
        // 找到分类
        Long catelogId = attrGroupEntity.getCatelogId();
        // 查询当前分类下的其他分组
        List<AttrGroupEntity> curCatelogGroups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 查询出这些分组所关联的属性
        // 1当当前分类下没有其他分组就不用做筛选
        List<AttrAttrgroupRelationEntity> curGroupsAllAttr = null;
        if (curCatelogGroups.size() != 0) {
            curGroupsAllAttr = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", curCatelogGroups.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList())));
        }
        // 找到已经被关联的attrId
        List<Long> attrId = null;
        if (curGroupsAllAttr != null) {
            attrId = curGroupsAllAttr.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        }
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catelog_id", catelogId).eq("attr_type", 1);
        if (attrId != null && attrId.size() != 0) {
            queryWrapper.notIn("attr_id", attrId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.like("attr_name", key);
        }
        IPage<AttrEntity> page = this.baseMapper.selectPage(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {

      return  this.baseMapper.selectSearchAttrIds(attrIds);
    }


}