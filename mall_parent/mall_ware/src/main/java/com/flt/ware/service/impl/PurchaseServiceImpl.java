package com.flt.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.constant.WareConstant;
import com.flt.common.utils.Query;
import com.flt.ware.dao.PurchaseDao;
import com.flt.ware.entity.PurchaseDetailEntity;
import com.flt.ware.entity.PurchaseEntity;
import com.flt.ware.feign.ProductFeignService;
import com.flt.ware.service.PurchaseDetailService;
import com.flt.ware.service.PurchaseService;
import com.flt.ware.service.WareSkuService;
import com.flt.ware.vo.MergeVO;
import com.flt.ware.vo.PurchaseDoneItemVO;
import com.flt.ware.vo.PurchaseFinishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.flt.common.utils.PageUtils;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), new QueryWrapper<PurchaseEntity>());

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), new QueryWrapper<PurchaseEntity>().eq("status", "0").or().eq("status", "1"));

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchaseS(MergeVO vo) {
        Long purchaseId = vo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        List<Long> items = vo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> details = items.stream().map(item -> {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            detail.setId(item);
            detail.setPurchaseId(finalPurchaseId);
            detail.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detail;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(details);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void receivePurChase(List<Long> ids) {
        // 1确认当前采购单是新建或者是已分配
        List<PurchaseEntity> purchases = ids.stream().map(id -> {
            return this.getById(id);
        }).filter(item -> {
            if (item == null) return false;
            return (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()) || (item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
        }).peek(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
        }).collect(Collectors.toList());

        // 2改变采购单的状态
        this.updateBatchById(purchases);
        // 3改变采购明细的状态
        purchases.forEach(item -> {
            List<PurchaseDetailEntity> list = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect = list.stream().peek(o -> {
                o.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });
    }

    @Transactional
    @Override
    public void donePurchase(PurchaseFinishVO doneVo) {
        Long id = doneVo.getId();
        PurchaseEntity purchaseEntity = this.getById(id);

        boolean flag = true;
        List<PurchaseDoneItemVO> items = doneVo.getItems();

        List<PurchaseDetailEntity> updateDetails = new ArrayList<>();
        for (PurchaseDoneItemVO item : items) {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            detail.setId(item.getItemId());
            if (item.getStatus() != WareConstant.PurchaseDetailStatusEnum.FINISH.getCode()) {
                flag = false;
                detail.setStatus(WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode());
            } else {
                // 采购成功才进行入库
                detail.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(byId.getSkuId(), byId.getWareId(), byId.getSkuNum());

            }
            updateDetails.add(detail);
        }
        this.purchaseDetailService.updateBatchById(updateDetails);
        PurchaseEntity updateEntity = new PurchaseEntity();
        updateEntity.setId(id);
        updateEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        updateEntity.setUpdateTime(new Date());
        this.updateById(updateEntity);
    }


}