package com.flt.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.flt.common.exception.BizCodeEnum;
import com.flt.member.exception.PhoneExitsException;
import com.flt.member.exception.UserNameExistException;
import com.flt.member.vo.MemberLoginVO;
import com.flt.member.vo.MemberRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flt.member.entity.MemberEntity;
import com.flt.member.service.MemberService;
import com.flt.common.utils.PageUtils;
import com.flt.common.utils.R;


/**
 * 会员
 *
 * @author fulitao
 * @email 850589771@qq.com
 * @date 2022-07-20 13:27:39
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    // private CouponFeignService couponFeignService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVO vo) {
        try {
            memberService.register(vo);
            return R.ok();
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnum.USER_EXITS_EXCEPTION.getCode(), BizCodeEnum.USER_EXITS_EXCEPTION.getMessage());
        } catch (PhoneExitsException e) {
            return R.error(BizCodeEnum.PHONE_EXITS_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXITS_EXCEPTION.getMessage());
        } catch (Exception e) {
            return R.error(501, "服务器异常");
        }
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVO vo) {
      MemberEntity memberEntity=  memberService.login(vo);
        if (memberEntity!=null) {
            return R.ok();
        }else{
            return R.error(BizCodeEnum.LOGINACCOUNT_PASSWORD_INVALID_EXCEPTION.getCode(), BizCodeEnum.LOGINACCOUNT_PASSWORD_INVALID_EXCEPTION.getMessage());
        }
    }

}
