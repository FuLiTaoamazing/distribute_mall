package com.flt.member.service.impl;

import com.flt.member.dao.MemberLevelDao;
import com.flt.member.entity.MemberLevelEntity;
import com.flt.member.exception.PhoneExitsException;
import com.flt.member.exception.UserNameExistException;
import com.flt.member.vo.MemberLoginVO;
import com.flt.member.vo.MemberRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.Query;

import com.flt.member.dao.MemberDao;
import com.flt.member.entity.MemberEntity;
import com.flt.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(new Query<MemberEntity>().getPage(params), new QueryWrapper<MemberEntity>());

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVO vo) {
        MemberEntity memberEntity = new MemberEntity();
        // 检查用户名和手机号是否唯一
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());


        // 密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        // 设置默认等级
        MemberLevelEntity defaultLevel = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(defaultLevel.getId());

        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkUserNameUnique(String userName) {
        if (this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName)) == 0) {
            return;
        }
        throw new UserNameExistException();
    }

    @Override
    public void checkPhoneUnique(String phone) {
        if (this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone)) == 0) {
            return;
        }
        throw new PhoneExitsException();
    }

    @Override
    public MemberEntity login(MemberLoginVO vo) {
        MemberEntity one = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", vo.getUserAccount()).or().eq("mobile", vo.getUserAccount()));
        if (one == null) {
            return null;
        }
        String passwordDB = one.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(vo.getPassword(), passwordDB);
        if (!matches) {
            return null;
        }
        return one;
    }
}