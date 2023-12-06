package com.lsh.gulimall.member.web;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.member.Feign.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
public class MemberWebController {

    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * //TODO
     *
     * @param pageNum
     * @param model
     * @return: String
     * @Description: 查出当前登陆的用户的所有订单列表数据
     */
    @RequestMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model) {

        HashMap<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());
        R r = orderFeignClient.listWithItem(page);

        model.addAttribute("orders", r);

        return "orderList";
    }

}
