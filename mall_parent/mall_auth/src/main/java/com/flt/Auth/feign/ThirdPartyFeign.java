package com.flt.Auth.feign;

import com.flt.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "MALL-THIRDPARTY")
public interface ThirdPartyFeign {
    @PostMapping("/sms/send")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
