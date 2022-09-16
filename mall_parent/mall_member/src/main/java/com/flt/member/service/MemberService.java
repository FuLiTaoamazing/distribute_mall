package com.flt.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flt.common.utils.PageUtils;
import com.flt.member.entity.MemberEntity;
import com.flt.member.vo.MemberLoginVO;
import com.flt.member.vo.MemberRegisterVO;

import java.util.Map;

/**
 * 会员
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:27:39
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVO vo);

    void checkUserNameUnique(String userName);

    void checkPhoneUnique(String phone);

    MemberEntity login(MemberLoginVO vo);
}

