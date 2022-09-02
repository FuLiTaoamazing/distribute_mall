package com.flt.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.flt.product.entity.BrandEntity;
import com.flt.product.entity.CategoryEntity;
import com.flt.product.service.BrandService;
import com.flt.product.service.CategoryService;
import com.flt.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flt.product.entity.CategoryBrandRelationEntity;
import com.flt.product.service.CategoryBrandRelationService;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    @RequestMapping("/catelog/list")
    public R catelogList(@RequestParam Long brandId) {
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.categoryEntityList(brandId);
        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {

        categoryBrandRelationService.save(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    ///product/categorybrandrelation/brands/list
    @GetMapping("/brands/list")
    public R relationBrandList(@RequestParam(value = "catId") Long catId) {
        List<BrandEntity> brands = categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandVO> vos = brands
                .stream()
                .map(item -> {
                    BrandVO vo = new BrandVO();
                    vo.setBrandId(item.getBrandId());
                    vo.setBrandName(item.getName());
                    return vo;
                })
                .collect(Collectors.toList());
        return R.ok().put("data", vos);
    }

}
