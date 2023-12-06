package com.lsh.gulimall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.lsh.gulimall.order.config.AlipayTemplate;
import com.lsh.gulimall.order.service.OrderService;
import com.lsh.gulimall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@Slf4j
public class OrderPayListener {

    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * //TODO
     *
     * @param payAsyncVo
     * @return: String
     * @Description: 支付宝支付成功异步通知 返回success 否则支付宝会再次通知（最大努力通知方案） 直到超过24小时
     */
    // alipay.notify_url=https://order.gulimall.com/order/pay/alipay/success
    @PostMapping("/order/pay/alipay/success")
    public String paySuccess(PayAsyncVo payAsyncVo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        log.warn("收到支付宝支付结果异步通知!");
        // 验证签名 防止伪造数据
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        if (signVerified) {
            log.info("签名验证成功!");
            String result = orderService.handlePayResult(payAsyncVo);
            // 收到了支付宝支付成功的请求
            return result;
        } else {
            log.info("签名验证失败!");
            return "error";
        }
    }

}
