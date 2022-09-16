package com.flt.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:27:39
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

