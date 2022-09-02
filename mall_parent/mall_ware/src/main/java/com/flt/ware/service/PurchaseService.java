package com.flt.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.ware.entity.PurchaseEntity;
import com.flt.ware.vo.MergeVO;
import com.flt.ware.vo.PurchaseFinishVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:41:09
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByUnreceive(Map<String, Object> params);

    void mergePurchaseS(MergeVO vo);

    void receivePurChase(List<Long> ids);

    void donePurchase(PurchaseFinishVO doneVo);
}

