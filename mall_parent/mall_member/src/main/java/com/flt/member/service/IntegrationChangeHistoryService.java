package com.flt.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:27:39
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

