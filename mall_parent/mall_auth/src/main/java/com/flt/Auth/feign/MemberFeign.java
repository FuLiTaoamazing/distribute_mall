package com.flt.Auth.feign;

import com.flt.Auth.vo.UserLoginVO;
import com.flt.Auth.vo.UserRegisterVO;
import com.flt.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("MALL-MEMBER")
public interface MemberFeign {
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVO vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVO vo);
}
