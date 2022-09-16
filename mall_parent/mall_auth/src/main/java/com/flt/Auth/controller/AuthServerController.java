package com.flt.Auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.flt.Auth.feign.MemberFeign;
import com.flt.Auth.feign.ThirdPartyFeign;
import com.flt.Auth.vo.UserLoginVO;
import com.flt.Auth.vo.UserRegisterVO;
import com.flt.common.constant.AuthServerConstant;
import com.flt.common.exception.BizCodeEnum;
import com.flt.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class AuthServerController {
    private static final Logger logger = LoggerFactory.getLogger(AuthServerController.class);
    @Autowired
    private ThirdPartyFeign thirdPartyFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeign memberFeign;


    @GetMapping("/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        // TODO 接口防刷


        // 防止不到60s多次请求
        String preCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotBlank(preCode)) {
            long preTime = Long.parseLong(preCode.split("_")[1]);
            if (System.currentTimeMillis() - preTime < 60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
            }
        }

        String code = UUID.randomUUID().toString().substring(0, 5);
        R r = thirdPartyFeign.sendCode(phone, code);
        if (r.getCode() == 0) {
            redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code + "_" + System.currentTimeMillis(), 5, TimeUnit.MINUTES);
        }

        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVO vo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addAttribute("errors", errors);
            return "redirect:http:/auth.mall.com/register.html";

        }
        String code = vo.getCode();
        String cacheCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (StringUtils.isBlank(cacheCode)) {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addAttribute("errors", errors);
            return "redirect:http://auth.mall.com/register.html";
        } else {
            if (!StringUtils.equals(cacheCode.split("_")[0], code)) {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addAttribute("errors", errors);
                return "redirect:http://auth.mall.com/register.html";
            }
        }
        R registerR = memberFeign.register(vo);
        if (registerR.getCode() != 0) {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", registerR.getData(new TypeReference<String>() {
            }));
            redirectAttributes.addAttribute("errors", errors);
            return "redirect:http://auth.mall.com/register.html";
        }
        redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        return "redirect:http://auth.mall.com/login.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVO vo, RedirectAttributes redirectAttributes) {
        R loginResponse = memberFeign.login(vo);
        if (loginResponse.getCode() != 0) {
            String msg = loginResponse.getData("msg", new TypeReference<String>() {
            });
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", msg);
            errors.put("code", loginResponse.getCode().toString());
            redirectAttributes.addAttribute("errors", errors);
            return "redirect:http://auth.mall.com/login.html";
        }

        return "redirect:http://mall.com";
    }

}
