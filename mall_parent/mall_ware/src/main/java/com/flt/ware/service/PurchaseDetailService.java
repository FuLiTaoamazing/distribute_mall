package com.flt.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:41:09
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    PageUtils queryPageByUnrecive(Map<String,Object> params);

    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

