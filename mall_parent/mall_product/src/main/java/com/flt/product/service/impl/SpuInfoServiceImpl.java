package com.flt.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flt.common.constant.ProductConstant;
import com.flt.common.to.SkuReductionTO;
import com.flt.common.to.SpuBoundsTO;
import com.flt.common.to.es.SkuEsModeTO;
import com.flt.common.utils.R;
import com.flt.product.entity.*;
import com.flt.product.feign.CouponFeignService;
import com.flt.product.feign.SearchFeignService;
import com.flt.product.feign.WareFeignService;
import com.flt.product.service.*;
import com.flt.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;

import com.flt.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private static final Logger logger = LoggerFactory.getLogger(SpuInfoServiceImpl.class);
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired// 掉用远程服务的feign客户带你
    private CouponFeignService couponFeignService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.eq("id", key).or().like("spu_name", key);
        }
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVO vo) {
        // 1。保存SPU基本信息  pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        Date createTime = new Date();
        infoEntity.setCreateTime(createTime);
        infoEntity.setUpdateTime(createTime);
        this.saveBaseSpuInfo(infoEntity);
        // 2.保存SPU的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);
        // 3。保存SPU的图片集 pms_spu_images
        List<String> spuImages = vo.getImages();
        spuImagesService.saveImages(infoEntity.getId(), spuImages);
        // 4.保存SPU的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValues = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            AttrEntity byId = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(infoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(productAttrValues);
        // 5.保存SPU的积分信息 sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(bounds, spuBoundsTO);
        spuBoundsTO.setSpuId(infoEntity.getId());
        R saveSpuBoundsR = couponFeignService.saveSpuBounds(spuBoundsTO);
        if (saveSpuBoundsR.getCode() != 0) {
            logger.error("调用远程服务:[saveSpuBounds]出错,{}", saveSpuBoundsR);
        }
        // 6.保存当前SPU对应的SKU信息
        // 6.1 保存Sku的基本信息  pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() != 0) {
            skus.forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                sku.getImages().forEach(item -> {
                    if (item.getDefaultImg() == 1) {
                        skuInfoEntity.setSkuDefaultImg(item.getImgUrl());
                    }
                });
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatelogId(infoEntity.getCatelogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();


                List<Images> skuImages = sku.getImages();
                List<SkuImagesEntity> skuImagesEntities = skuImages.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    return skuImagesEntity;
                }).filter(item -> {
                    return StringUtils.isNotEmpty(item.getImgUrl());
                }).collect(Collectors.toList());
                // 6.2 保存sku的图片信息 pms_sku_images
                skuImagesService.saveBatch(skuImagesEntities);
                // 6.3 保存sku的销售属性值 pms_sku_sale_attr_value
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(item -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    BeanUtils.copyProperties(item, skuSaleAttrValueEntity);

                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                // 6.4 保存sku的满减以及优惠信息: sms_sku_ladder,sms_sku_full_reduction,sms_member_price
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(sku, skuReductionTO);
                skuReductionTO.setSkuId(skuId);
                if (skuReductionTO.getFullCount() > 0 || skuReductionTO.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
                    R saveSkuReductionR = couponFeignService.saveSkuReduction(skuReductionTO);
                    if (saveSkuReductionR.getCode() != 0) {
                        logger.error("调用远程服务[saveSkuReduction],出错:{}", saveSkuReductionR);
                    }
                }

            });
        }


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String status = (String) params.get("status");
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
        if (StringUtils.isNotBlank(status) && Long.parseLong(status) != 0) {
            queryWrapper.eq("status", status);
        }
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        List<SkuInfoEntity> skuInfoBySpuId = skuInfoService.getSkusBySpuId(spuId);
        // TODO 查询当前spu的所有规格属性(可以进行检索的)
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> searchAttrIdsSet = new HashSet<>(searchAttrIds);
        List<SkuEsModeTO.Attr> attrs = productAttrValueEntities.stream().filter(item -> {
            return searchAttrIdsSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModeTO.Attr attr = new SkuEsModeTO.Attr();
            attr.setAttrId(item.getAttrId());
            attr.setAttrName(item.getAttrName());
            attr.setAttrValue(item.getAttrValue());
            return attr;
        }).collect(Collectors.toList());
        // TODO 远程服务查询所有sku的库存信息
        Map<Long, Boolean> stockMap = null;
        try {
            R hasStockR = wareFeignService.getSkusHasStock(skuInfoBySpuId.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));
            TypeReference<List<SkuHasStockVO>> typeReference = new TypeReference<List<SkuHasStockVO>>(){
            };
            stockMap = hasStockR.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getStock));
        } catch (Exception e) {
            logger.error("远程调用库存服务出错", e);
        }
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModeTO> upProduct = skuInfoBySpuId.stream().map(sku -> {
            SkuEsModeTO skuEsModeTO = new SkuEsModeTO();
            BeanUtils.copyProperties(sku, skuEsModeTO);
            skuEsModeTO.setSkuPrice(sku.getPrice());
            skuEsModeTO.setSkuImg(sku.getSkuDefaultImg());
            // TODO 查询品牌的名字和图片
            BrandEntity brand = brandService.getById(sku.getBrandId());
            skuEsModeTO.setBrandName(brand.getName());
            skuEsModeTO.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(sku.getCatelogId());
            skuEsModeTO.setCatelogName(category.getName());
            // TODO 设置热度评分
            skuEsModeTO.setHotScore(0L);
            // TODO 设置检索属性
            skuEsModeTO.setAttrs(attrs);
            // TODO 设置库存
            if (finalStockMap != null) {
                skuEsModeTO.setHasStock(finalStockMap.getOrDefault(sku.getSkuId(), false));
            } else {
                skuEsModeTO.setHasStock(false);
            }
            return skuEsModeTO;
        }).collect(Collectors.toList());
        // TODO 发送给Es服务器进行保存
        R r = searchFeignService.productUp(upProduct);
        if (r.getCode() == 0) {
            // 上架成功 修改当前Spu的发布状态
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            this.baseMapper.update(spuInfoEntity, new UpdateWrapper<SpuInfoEntity>().eq("id", spuId));
        } else {
            //TODO 上架功能还有很多问题 比如重复调用 远程服务的重试机制
        }
    }


}