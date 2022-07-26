package com.flt.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;

import com.flt.product.dao.SpuImagesDao;
import com.flt.product.entity.SpuImagesEntity;
import com.flt.product.service.SpuImagesService;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long id, List<String> spuImages) {
        if (spuImages == null || spuImages.size() == 0) {
            return;
        }
        List<SpuImagesEntity> entities = spuImages.stream().map(image -> {
            SpuImagesEntity entity = new SpuImagesEntity();
            entity.setSpuId(id);
            entity.setImgUrl(image);
            return entity;
        }).collect(Collectors.toList());

        this.saveBatch(entities);
    }

}