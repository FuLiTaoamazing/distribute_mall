package com.flt.thirdparty.controller;

import com.flt.common.utils.R;
import com.flt.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private SmsComponent smsComponent;

    /*
    提供给别的服务进行调用
     */
    @PostMapping("/send")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        try {
            smsComponent.sendSms(phone, code);
            return R.ok();
        } catch (Exception e) {
            return R.error();
        }
    }
}
