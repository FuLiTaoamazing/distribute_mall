package com.flt.thirdparty.component;

import com.flt.thirdparty.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "alibaba.cloud.sms")
public class SmsComponent {
    private static final Logger logger = LoggerFactory.getLogger(SmsComponent.class);
    private String host;
    private String path;
    private String method;
    private String appcode;


    public void sendSms(String phone, String code) throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        // 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        // 根据API的要求，定义相对应的Content-Type
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", "**code**:"+code+",**minute**:5");
        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();


        /**
         * 重要提示如下:
         * HttpUtils请从
         * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
         * 下载
         *
         * 相应的依赖请参照
         * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
         */
        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        logger.info("{}",response.toString());
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }
}
