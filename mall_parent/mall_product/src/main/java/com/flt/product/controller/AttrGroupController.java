package com.flt.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.flt.product.entity.AttrEntity;
import com.flt.product.service.AttrAttrgroupRelationService;
import com.flt.product.service.AttrService;
import com.flt.product.service.CategoryService;
import com.flt.product.vo.AttrGroupWithAttrVO;
import com.flt.product.vo.AttrRelationGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flt.product.entity.AttrGroupEntity;
import com.flt.product.service.AttrGroupService;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.R;


/**
 * 属性分组
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        // PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(catelogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    ///attrgroup/1/attr/relation?
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> attrEntityList = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", attrEntityList);
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrRelationGroupVO[] attrRelationGroupVO) {
        attrService.deleteRelation(attrRelationGroupVO);
        return R.ok();
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noattrRelation(@PathVariable("attrgroupId") Long attrgroupId, Map<String, Object> params) {
        PageUtils page = attrService.noattrRelationPage(params, attrgroupId);
        return R.ok().put("page", page);
    }

    // /product/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R relation(@RequestBody List<AttrRelationGroupVO> vos) {
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }

    ///product/attrgroup/{catelogId}/withattr
    @GetMapping(value = "/{catelogId}/withattr")
    public R getAttrGroupWithAttr(@PathVariable(value = "catelogId") Long catelogId) {
       List<AttrGroupWithAttrVO> vos = attrGroupService.getAttrGroupWithAttr(catelogId);
        return R.ok().put("data", vos);
    }


}
