package com.flt.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flt.product.dao.BrandDao;
import com.flt.product.dao.CategoryDao;
import com.flt.product.entity.BrandEntity;
import com.flt.product.entity.CategoryEntity;
import com.flt.product.vo.BrandVO;
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

import com.flt.product.dao.CategoryBrandRelationDao;
import com.flt.product.entity.CategoryBrandRelationEntity;
import com.flt.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    private CategoryDao categoryDao;
    private BrandDao brandDao;

    @Autowired
    public CategoryBrandRelationServiceImpl(CategoryDao categoryDao, BrandDao brandDao) {
        this.categoryDao = categoryDao;
        this.brandDao = brandDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean save(CategoryBrandRelationEntity entity) {
        Long catelogId = entity.getCatelogId();
        Long brandId = entity.getBrandId();
        if (catelogId != null) {
            CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
            entity.setCatelogName(categoryEntity.getName());
        }
        if (brandId != null) {
            BrandEntity brandEntity = brandDao.selectById(brandId);
            entity.setBrandName(brandEntity.getName());
        }
        return super.save(entity);
    }

    public List<CategoryBrandRelationEntity> categoryEntityList(Long brandId) {

        return baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }


    @Override
    public void updateBrand(String brandName, Long brandId) {
        CategoryBrandRelationEntity updateEntity = new CategoryBrandRelationEntity();
        updateEntity.setBrandName(brandName);
        updateEntity.setBrandId(brandId);
        this.update(updateEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public void updateCategory(String categoryName, Long catelogId) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(catelogId);
        categoryBrandRelationEntity.setCatelogName(categoryName);
        this.update(categoryBrandRelationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catelogId));
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> relations = baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<Long> brandIds = relations.stream().map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
        return brandDao.selectBatchIds(brandIds);
    }
}