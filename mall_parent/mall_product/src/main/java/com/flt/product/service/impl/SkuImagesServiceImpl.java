package com.flt.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;
import com.flt.product.dao.SkuImagesDao;
import com.flt.product.entity.SkuImagesEntity;
import com.flt.product.service.SkuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        return this.baseMapper.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
    }

}