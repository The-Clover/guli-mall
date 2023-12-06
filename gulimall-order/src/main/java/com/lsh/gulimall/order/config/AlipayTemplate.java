package com.lsh.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.lsh.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2021000118630915";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCp4PTiYCbddeVzZati8PtZvlliJgNO9/dM4iK5NDWkc/ACw4gb+JMMDBqk6oSsQ+8E3lDU+bijYEnLD+YmwZwb+E3q4F65roolJlfPFb6vL9OWIpSByoal/KR7hkewCuxOudCjmAxgwG2ZxtCDSwmEv2dkMAHQAqPIS4GfILEpO7HYeQvpqmIJVe5oVx6w2vwgHr8aLS57UtHD2tIfbZy2NsJrcIWOT9wsv8s1N9e5/hm8VlIW2FkhdkaPVyQwI7fwNhyCHC7kNEHTNU1CIx1ecFku9hNg1fx1yRBHSEzkipd7Y5vyRa6dV2bNRAdUsNz3y7am9yyCYqXhJ7kShaodAgMBAAECggEAYpCUZVhWlH4YQGlIFKMvozVvVSQrHUGAakp2zTB+w0lFg8UBjwEeCIppVzB/BBhycUewbLHrsFdo2XoDFo+VTSE3zESckW2017M0lYHPXoQIZv7vXZMtptc8dDNIGttrmo3zgUfyPpzHusaBKvx5rF0F1zqHgrJVZxxwO+zS/dGj3fGtolKeQXHAjSllEpK5rhAUNkJTj2aPXmZ89UsqtLZ+To+cCXxIRcr6VDvJYT4PO25mX2hDP0T6BlvdXMnFRqsIK9qW9Rh7IMkCUhFCqFeBEkFioJX/ae0h9pv6/BT1IfwJw9mpmy03aYBhzx+JXyb+fmLp2Q5EDBN+YePMgQKBgQDV/49836sVIxiGYOGKRLQfC2qyKnuTXv07TNr/w9mVUnsr0TJw+5xG63JW6dfXPwM5fwmVF8AhG/cUwQGVcm4GnW7pZK3ZI69hhftQfPRmCajQLKy7pjA4U66TGNqkQHWXo4ov0O7/oeSQDGcJvMkd7S28CpRnJ+XYQ0T3zqCBvQKBgQDLOJb35Xjj/pdIuxEk4UwSap4FLaRNym8H9cqzV8zuZJfaPiFUNQm7jG2IjQPnsrwgbEfwewOLwXGxr5hB6cCV1PsdSFMml/XFP8EIOnGZCAB9FmLrf7+H9ZyspYgnTdxDjuOVUePP4LxOROFpZWcxuTaJrceWU+jAAGJRTzDf4QKBgAiHavybHYJ2J6ATdGnCWztxhQsczlgNTSJz6baP47gj1WzlpZEDNjNNZv03zLZzeHluKr+adrnbF3noR1vhd6kj1SvQK22gEnlckaufj04RzHyfizLfaMSV8TtDnlM1jnpAtl05go0Fsspa9DBbKdjbfxsjgM6rtGluJO2LerIxAoGBAMWm44Z1xG+ThZZygwkoqzFaSxjkPGKAmh7W23A03Bec9OiJ9yaMc0vbGAlwhbTLbRfn2Mw3CMrbuXXFQEQTUoEn5ZikaGMn9x0Clf8nwKbDVp5SsKE13ZYht2Ffmh4IJ0v55LWtMPJVNTC247n7qzN8XWPpBi7aq4xak0VbzUBhAoGAJ1rXilAtcOyGSMGKFXC9yCRFLv+M0oSp4+Q3N1taSrmBTGv87QAspNCz081vSvwhS/g99nrqPpUqzkVLKTFw2/ZaiG4R1IcEP4wSNkfFOdJpOfti4KVBnTV25ihqP/LOklyjZ22VovoQyudzSdLNr1OL4u6e9SE6wMKrOuMbvm0=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApb6Cupn0FalaKSLO3/9cBratJAu2aoX8CjjBB8SGNwtHlk2FBTLpTmKSM5r4GdVnQt/awmvagF7WpZLfQ8NVLuURVMRhDKVCnA4FtAbdssnyGYeAIFYLJ1KOvMGN414Kd5mjjbkqEA5NzUBCq1EjUj7nCv1AgV9Gr7BYsJ2Q98zBGfe9iiLtpLbldmTblVQksHYNqrZMXOsJTCE66KU7VLzcNxUOCkEGReMQp2826BhVLKtmKy23B1erzTLPbcALpi65EPDhuxV7j1raYZ7MiOzbX3pnsYR1r5YwkDTArGRNz1hOEEjkksJKFox1oUaHP3Glw9O5BYqmJtMz/rGokwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url;

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 自动收单时间

    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\"," //一分钟超时收单 不能付款
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
