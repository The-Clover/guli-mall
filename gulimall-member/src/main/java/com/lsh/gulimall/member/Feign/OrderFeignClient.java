package com.lsh.gulimall.member.Feign;

import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Service
@FeignClient(name = "gulimall-order")
public interface OrderFeignClient {

    /**
     * 查询登陆用户的所有订单信息
     */
    @PostMapping("order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);
}
