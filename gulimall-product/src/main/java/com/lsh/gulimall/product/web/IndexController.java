package com.lsh.gulimall.product.web;

import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.Catelog2Vo;
import com.lsh.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/")
@Slf4j
public class IndexController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	private RedissonClient redisson;

	@RequestMapping({"/", "/index", "/index.html"})
	String indexPage(Model model) {
		/*1.查询所有的一级分类*/
		List<CategoryEntity> categoryEntityList = categoryService.getOneLevelCategorys();
//		log.info("访问首页!");
		model.addAttribute("categorys", categoryEntityList);
		return "index";
	}

	/*index/catalog.json*/

	/**
	 * //TODO
	 *
	 * @param
	 * @date 2021/6/30 22:50
	 * @Description 获取前端所有分类
	 */
	@RequestMapping("index/catalog.json")
	@ResponseBody
	Map<String, List<Catelog2Vo>> getCatalogJson() {
		return categoryService.getCatalogJson();
	}




	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 22:50
	 * @Description 测试服务   redisson里的锁都是分布式锁 底层都是lua脚本保证原子性 看门狗机制解决死锁问题
	 */
	@RequestMapping("/hello")
	@ResponseBody
	String hello() {
		/*获取锁 名称一样为一把锁*/
		RLock lock = redisson.getLock("my-lock");

		/*没有死锁问题,看门狗自动续期,自动释放 默认30s 任务完成30s后不手动unlock就会自动释放锁 未指定时间,获取锁的看门狗超时时间 30s 使用调度定时任务重新设置超时时间30s 定时任务10s(超时时间/3)执行一次续期 */
//		lock.lock(); //阻塞等待 等待其他锁释放再往下执行
		/*也可以手动指定过期时间 同样自动解锁 但是不会自动续期 所以指定的过期时间要大于业务执行时间 不传时间默认为-1 传了时间 就会执行lua脚本解锁 */
		/*推荐指定时间大于业务时间就行,之后手动解说*/
		lock.lock(10, TimeUnit.SECONDS);
		try {
			System.out.println("业务代码---" + Thread.currentThread().getId());
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println(Thread.currentThread().getId() + "解锁");
			lock.unlock();
		}
		return "hello";
	}

	@RequestMapping("/lockDoor")
	@ResponseBody
	public String lockDoor() throws InterruptedException {
		RCountDownLatch door = redisson.getCountDownLatch("door");
		door.trySetCount(5);
		door.await();
		return "锁门";
	}

	@GetMapping("/go/{id}")
	@ResponseBody
	public String countDown(@PathVariable("id") long id) throws InterruptedException {
		RCountDownLatch door = redisson.getCountDownLatch("door");
		door.countDown(); // 计数-1
		return id + "结束";
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/5 下午10:52
	 * @Description 信号量
	 */
	@GetMapping("/park")
	@ResponseBody
	public String park() throws InterruptedException {
		/*获取信号量*/
		RSemaphore park = redisson.getSemaphore("park");
		park.acquire(); // 获取信号量 获取一个值 占一个车位 park=3-1 阻塞式等待
		boolean b = park.tryAcquire();// 非阻塞式 返回结果
		if (!b) {
			return "车位满了";
		}
		return "ok";
	}

	@GetMapping("/go")
	@ResponseBody
	public String unlock() throws InterruptedException {
		/*获取信号量 */
		RSemaphore park = redisson.getSemaphore("park");
		park.release();/*释放一个车位*/
		return "go";
	}

}