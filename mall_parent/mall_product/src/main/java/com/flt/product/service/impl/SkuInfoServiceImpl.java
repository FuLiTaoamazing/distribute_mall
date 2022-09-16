package com.flt.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;
import com.flt.product.dao.SkuInfoDao;
import com.flt.product.entity.SkuImagesEntity;
import com.flt.product.entity.SkuInfoEntity;
import com.flt.product.entity.SpuInfoDescEntity;
import com.flt.product.service.*;
import com.flt.product.vo.SkuItemSaleAttrVO;
import com.flt.product.vo.SkuItemVO;
import com.flt.product.vo.SpuItemBaseAttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(w -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        if (StringUtils.isNotBlank(catelogId) && Long.parseLong(catelogId) != 0) {
            queryWrapper.eq("cate_logid", catelogId);
        }
        if (StringUtils.isNotBlank(brandId) && Long.parseLong(brandId) != 0) {
            queryWrapper.eq("brand_id", brandId);
        }

        if (StringUtils.isNotBlank(min)) {
            queryWrapper.ge("price", min);
        }
        if (StringUtils.isNotBlank(max)) {
            BigDecimal bMax = new BigDecimal(max);
            if (bMax.compareTo(BigDecimal.ZERO) > 0) {
                queryWrapper.le("price", max);
            }
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVO result = new SkuItemVO();

        // 获取Sku的基本信息
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity info = this.getById(skuId);
            result.setInfo(info);
            return info;
        }, threadPoolExecutor);
        // 获取spu下的所有销售组合
        CompletableFuture<Void> skuItemSaleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SkuItemSaleAttrVO> skuItemSaleAttrVOS = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            result.setSaleAttr(skuItemSaleAttrVOS);
        }, threadPoolExecutor);
        // 获取spu的介绍
        CompletableFuture<Void> spuInfoDescFuture = infoFuture.thenAcceptAsync((res) -> {
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(res.getSpuId());
            result.setDesp(spuInfoDesc);
        }, threadPoolExecutor);

        // 获取spu的规格参数属性
        CompletableFuture<Void> groupAttrsFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SpuItemBaseAttrVO> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatelogId());
            result.setGroupAttrs(groupAttrs);
        }, threadPoolExecutor);

        // 获取sku的图片信息
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            result.setImages(images);
        }, threadPoolExecutor);

       CompletableFuture.allOf(infoFuture, skuItemSaleAttrFuture, spuInfoDescFuture, groupAttrsFuture, imageFuture).get();
        return result;
    }

}