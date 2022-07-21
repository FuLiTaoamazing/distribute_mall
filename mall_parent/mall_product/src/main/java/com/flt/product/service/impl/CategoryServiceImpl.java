package com.flt.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;

import com.flt.product.dao.CategoryDao;
import com.flt.product.entity.CategoryEntity;
import com.flt.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> allCategory = baseMapper.selectList(null);

        //组装成树形结构
        //一级分类
        List<CategoryEntity> level1Menus = allCategory.stream().filter(o ->
                o.getParentCid() == 0
        ).map(o -> {
            o.setChildren(getChildren(o, allCategory));
            return o;
        }).sorted((o1, o2) -> {
            return (o1.getSort()==null?0:o1.getSort()) - (o2.getSort()==null?0:o2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    //递归查找某个菜单下的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().
                filter(o -> {
                    return root.getCatId() == o.getParentCid();
                }).map(o -> {
                    o.setChildren(getChildren(o, all));
                    return o;
                }).sorted((o1, o2) -> {
                    return (o1.getSort()==null?0:o1.getSort()) - (o2.getSort()==null?0:o2.getSort());
                }).collect(Collectors.toList());


        return children;
    }
}