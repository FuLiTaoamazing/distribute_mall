package com.flt.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.flt.product.service.CategoryBrandRelationService;
import com.flt.product.vo.Catelog2VO;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;

import com.flt.product.dao.CategoryDao;
import com.flt.product.entity.CategoryEntity;
import com.flt.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(new Query<CategoryEntity>().getPage(params), new QueryWrapper<CategoryEntity>());

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 查出所有分类
        List<CategoryEntity> allCategory = baseMapper.selectList(null);

        // 组装成树形结构
        // 一级分类
        List<CategoryEntity> level1Menus = allCategory.stream().filter(o -> o.getParentCid() == 0).map(o -> {
            o.setChildren(getChildren(o, allCategory));
            return o;
        }).sorted((o1, o2) -> {
            return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    // 递归查找某个菜单下的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(o -> {
            return root.getCatId() == o.getParentCid();
        }).map(o -> {
            o.setChildren(getChildren(o, all));
            return o;
        }).sorted((o1, o2) -> {
            return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
        }).collect(Collectors.toList());


        return children;
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        findParentId(catelogId, paths);

        return paths.toArray(new Long[0]);
    }

    private void findParentId(Long catelogId, List<Long> paths) {
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentId(byId.getParentCid(), paths);
        }
        paths.add(catelogId);
    }


    @Override
    public void updateDetails(CategoryEntity entity) {
        this.updateById(entity);

        // 保证categoryBrandRelation表中的冗余字段一致性
        if (StringUtils.isNotBlank(entity.getName())) {
            // 更新CategroyBrandRelation表中的冗余字段
            categoryBrandRelationService.updateCategory(entity.getName(), entity.getCatId());
            // TODO:更新其他拥有冗余分类字段表中的信息
        }

    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Override
    public Map<String, List<Catelog2VO>> getCatalogJson() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isBlank(catalogJson)) {
            Map<String, List<Catelog2VO>> catalog = getCategoryFromDB();
            return catalog;
        }
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2VO>>>() {
        });
    }

    @Transactional
    public Map<String, List<Catelog2VO>> getCategoryFromDB() {
        RLock lock = redisson.getLock("catalogJsonLock");
        Map<String, List<Catelog2VO>> vos = null;
        lock.lock();
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isNotBlank(catalogJson)) {
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2VO>>>() {
            });
        }
        try {
            List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
            List<CategoryEntity> level1Categorys = getParent_cid(0L, categoryEntities);
            vos = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                Long level1Id = v.getCatId();
                List<CategoryEntity> level2 = getParent_cid(level1Id, categoryEntities);
                return level2.stream().map(item -> {
                    Long level2Id = item.getCatId();
                    Catelog2VO catelog2VO = new Catelog2VO(level1Id.toString(), null, level2Id.toString(), item.getName());
                    List<CategoryEntity> level3 = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", level2Id));
                    List<Catelog2VO.Catelog3VO> catelog3VOS = level3.stream().map(c -> {
                        return new Catelog2VO.Catelog3VO(level2Id.toString(), c.getCatId().toString(), c.getName());
                    }).collect(Collectors.toList());
                    catelog2VO.setCatalog3List(catelog3VOS);
                    return catelog2VO;
                }).collect(Collectors.toList());
            }));
            redisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(vos), (long) (1 + Math.random() * 10), TimeUnit.MINUTES);
        } finally {
            lock.unlock();
        }
        return vos;
    }

    private List<CategoryEntity> getParent_cid(Long parentId, List<CategoryEntity> categoryEntityList) {

        return categoryEntityList.stream().filter(item -> {
            return Objects.equals(item.getParentCid(), parentId);
        }).collect(Collectors.toList());
    }
}