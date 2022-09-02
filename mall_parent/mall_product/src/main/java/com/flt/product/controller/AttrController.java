package com.flt.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.flt.product.entity.ProductAttrValueEntity;
import com.flt.product.service.ProductAttrValueService;
import com.flt.product.vo.AttrRespVO;
import com.flt.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flt.product.entity.AttrEntity;
import com.flt.product.service.AttrService;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.R;


/**
 * 商品属性
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 10:49:13
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService  productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(@PathVariable("catelogId") Long catelogId, @RequestParam Map<String, Object> params, @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.baseList(catelogId, params, attrType);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVO attr = attrService.attrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId) {
      List<ProductAttrValueEntity> entityList=  productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data", entityList);
    }

}
