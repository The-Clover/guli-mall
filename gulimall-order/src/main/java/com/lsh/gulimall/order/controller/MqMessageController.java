package com.lsh.gulimall.order.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.order.entity.MqMessageEntity;
import com.lsh.gulimall.order.service.MqMessageService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/mqmessage")
public class MqMessageController {
	@Autowired
	private MqMessageService mqMessageService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = mqMessageService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{messageId}")
	public R info(@PathVariable("messageId") String messageId) {
		MqMessageEntity mqMessage = mqMessageService.getById(messageId);

		return R.ok().put("mqMessage", mqMessage);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody MqMessageEntity mqMessage) {
		mqMessageService.save(mqMessage);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody MqMessageEntity mqMessage) {
		mqMessageService.updateById(mqMessage);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody String[] messageIds) {
		mqMessageService.removeByIds(Arrays.asList(messageIds));
		return R.ok();
	}

}
