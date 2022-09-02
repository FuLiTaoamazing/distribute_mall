package com.flt.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.flt.common.utils.PageUtils;
import com.flt.common.utils.R;
import com.flt.ware.entity.PurchaseEntity;
import com.flt.ware.service.PurchaseDetailService;
import com.flt.ware.service.PurchaseService;
import com.flt.ware.vo.MergeVO;
import com.flt.ware.vo.PurchaseFinishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 采购信息
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:41:09
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    @GetMapping("/unreceive/list")
    public R unReceive(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPageByUnreceive(params);
        return R.ok().put("page", page);
    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeVO vo) {
        purchaseService.mergePurchaseS(vo);
        return R.ok();
    }

    @PostMapping("/received")
    public R receive(@RequestBody List<Long> ids) {
        purchaseService.receivePurChase(ids);
        return R.ok();
    }

    @PostMapping("/done")
    public R done(@RequestBody PurchaseFinishVO purchaseFinishVO) {
        purchaseService.donePurchase(purchaseFinishVO);

        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


}
