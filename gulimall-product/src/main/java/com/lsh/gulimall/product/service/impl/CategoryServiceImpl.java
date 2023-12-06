package com.lsh.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.dao.CategoryDao;
import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.Catelog2Vo;
import com.lsh.gulimall.product.service.CategoryBrandRelationService;
import com.lsh.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author codestar
 */
@Service("categoryService")
@Slf4j
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

	@Autowired
	CategoryBrandRelationService categoryBrandRelationService;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	RedissonClient redissonClient;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryEntity> page = this.page(
				new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<CategoryEntity> getCategoryServiceList(String info) {
		if (info != null) {
			log.info("查询 " + info);
		}
		List<CategoryEntity> list = StringUtils.isEmpty(info) ? this.list() : this.list(new QueryWrapper<CategoryEntity>().like("name", info));
		/*map -- peek*/
		List<CategoryEntity> collect = list.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(0L)).peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, list))).sorted(Comparator.comparingInt(CategoryEntity::getSort)).collect(Collectors.toList());
		return collect;
	}

	@Override
	public boolean deleteById(Long[] catIds) {
		for (Long catId : catIds) {
			CategoryEntity byId = this.getById(catId);
			if (byId == null) {
				return false;
			}
			/*存在子分类*/
			List<CategoryEntity> parentCid = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", catId));
			if (parentCid.size() != 0) {
				return false;
			}
		}
		return this.removeByIds(Arrays.asList(catIds));
	}

	/**
	 * //TODO
	 *
	 * @param catelogId
	 * @return catelogPath完整路径
	 * @throws
	 * @date 2021/6/17 下午10:59
	 * @Description
	 */
	@Override
	public Long[] findCatelogPath(Long catelogId) {
		List<Long> longs = new ArrayList<>();
		List<Long> paths = findParentPath(catelogId, longs);
		Collections.reverse(paths);
		return paths.toArray(new Long[0]);
	}

	/*关联更新*/
	@Override
//	@CacheEvict(value = "category",key ="'CACHE_getOneLevelCategorys'" ) // 清楚数据 缓存失效
//	@Caching(evict = {@CacheEvict(value = "category",key ="'getOneLevelCategorys'" ),@CacheEvict(value = "category",key ="'getCatalogJson'" )}) //同时多个操作
	/*存储同一类型的数据,都可以指定同一个分区名 分区名就是默认前缀*/
//	CachePut() 双写模式
	@CacheEvict(value = "category", allEntries = true) //清楚指定缓存分区
	public boolean updateCascade(CategoryEntity category) {
		/*关联更新*/
		return this.updateById(category) && categoryBrandRelationService.updateCategory(category);
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 21:58
	 * @Description 获取前端所有一级分类
	 */
	/*  @Cacheable: Triggers cache population. 触发将数据保存到缓存
		@CacheEvict: Triggers cache eviction.  删除数据
		@CachePut: Updates the cache without interfering with the method execution. 不影响方法执行更新缓存
		@Caching: Regroups multiple cache operations to be applied on a method. 组合以上多个操作
		@CacheConfig: Shares some common cache-related settings at class-level. 共享缓存配置 类级别

		1.导入依赖
		2.properties/yml配置缓存类型
		3.配置开启缓存注解 @EnableCaching
		3.使用缓存注册

		root.method 方法全限定名

		如果缓存中有数据 则从缓存中获取数据 不执行方法 缓存中无数据 调用方法 返回值放入缓存
		value : 缓存的分区 按照业务类型

		@Cacheable
		默认过期时间 永久
		默认使用jdk序列化机制,把序列化后的数据存入redis
		# 毫秒
		spring.cache.redis.time-to-live=3600000 自定义缓存过期时间
		sync = true 加锁 本地锁 默认无锁
	*/
	@Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
	@Override
	public List<CategoryEntity> getOneLevelCategorys() {
		return this.list(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
	}

	/*sync = true 加锁 默认无锁 */
	@Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
	@Override
	public Map<String, List<Catelog2Vo>> getCatalogJson() {
		/*解决堆外内存移除
		 * 1.升级lettuce
		 * 2.切换使用jedis *
		 * */

//		/*空结果缓存 : 解决缓存穿透*/
//		/*设置过期时间 : 解决缓存雪崩*/
//		/*加锁 : 解决缓存击穿*/
//		Map<String, List<Catelog2Vo>> map;
//		/*先查询redis*/
//		String categorysOfRedis = stringRedisTemplate.opsForValue().get("categorys");
//		/*redis中不存在 categorys*/
//		if (StringUtils.isEmpty(categorysOfRedis)) {
//			/*mysql查询*/
//			map = getCatalogjsonRedisson();
//		} else {
//			/********************* 解析redis数据 json转复杂类型 : TypeReference */
//			map = JSON.parseObject(categorysOfRedis, new TypeReference<Map<String, List<Catelog2Vo>>>() {
//			});
//		}
		log.warn("categorys :查询数据库!");
		Map<String, List<Catelog2Vo>> map = null;
		List<CategoryEntity> categorys = this.list();
		if (categorys != null) {
			List<CategoryEntity> oneLevelCategorys = getParent_cid(categorys, 0L);
			//key:k.getCatId() value: catelog2VoList
			map = oneLevelCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(),
					categoryEntity1 -> {
						//获取一级分类下的二级分类列表
						List<CategoryEntity> categoryEntities = getParent_cid(categorys, categoryEntity1.getCatId());
						//获取二级下三级分类列表
						//处理二级分类
						if (categoryEntities != null) {
							List<Catelog2Vo> catelog2VoList = categoryEntities.stream().map(categoryEntity2 -> {
								List<CategoryEntity> categoryEntityList = getParent_cid(categorys, categoryEntity2.getCatId());
								//处理三级分类
								if (categoryEntityList != null) {
									List<Catelog2Vo.Catelog3Vo> catelog3VoList = categoryEntityList.stream().map(categoryEntity3 -> {
										Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(categoryEntity2.getCatId().toString(), categoryEntity3.getCatId().toString(), categoryEntity3.getName());
										return catelog3Vo;
									}).collect(Collectors.toList());
									Catelog2Vo catelog2Vo = new Catelog2Vo(categoryEntity1.getCatId().toString(), catelog3VoList, categoryEntity2.getCatId().toString(), categoryEntity2.getName());
									return catelog2Vo;
								}
								return null;
							}).collect(Collectors.toList());
							return catelog2VoList;
						}
						return null;
					}));
		}
		log.warn("数据已存入redis");
		return map;
	}


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 22:48
	 * @Description 获取前端全部分类数据 redssion  缓存数据和数据库保持一直 数据一致性 双写模式(同时修改)  失效模式(缓存失效,重新存入缓存)
	 */
	@Transactional
	@Override
	public Map<String, List<Catelog2Vo>> getCatalogjsonRedisson() {
		/*redission获取分布式锁*/
		/*锁的粒度.越细越快  */
		RLock catelogLock = redissonClient.getLock("categorys-lock");
		catelogLock.lock();
		Map<String, List<Catelog2Vo>> categorysFromDb = null;
		try {
			/*先查询redis*/
			String categorysOfRedis = stringRedisTemplate.opsForValue().get("");
			/*redis中不存在 categorys*/
			if (!StringUtils.isEmpty(categorysOfRedis)) {
				/********************* 解析redis数据 json转复杂类型 : TypeReference */
				return JSON.parseObject(categorysOfRedis, new TypeReference<Map<String, List<Catelog2Vo>>>() {
				});
			}
			categorysFromDb = getCategorysFromDb();
			return categorysFromDb;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			catelogLock.unlock();
		}
		return categorysFromDb;
	}


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 22:48
	 * @Description 获取前端全部分类数据 redsion
	 */
	@Transactional
	@Override
	public Map<String, List<Catelog2Vo>> getCatalogjsonFromDb() {
		/*
		优化1:一次查询再处理
		*/
		/*分布式锁  redis占坑 setIfAbsent:如果不存在*/
		/*设置过期时间,防止死锁*/
		String uuid = UUID.randomUUID().toString();
		Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
		/*未获得锁*/
		int l = 0;
		while (!lock) {
			log.error("获取数据失败 等待重试!");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
			/*查询缓存中没有数据*/
			String categorysOfRedis = stringRedisTemplate.opsForValue().get("categorys");
			if (!StringUtils.isEmpty(categorysOfRedis)) {
				log.warn("从redis中获取数据成功");
				/********************* 解析redis数据 json转复杂类型 : TypeReference */
				return JSON.parseObject(categorysOfRedis, new TypeReference<Map<String, List<Catelog2Vo>>>() {
				});
			}
			if (l > 20) {
				l = 0;
				/*自旋*/
				return getCatalogjsonFromDb();
			}
			l++;
		}
		log.warn("获取分布锁成功!");
		Map<String, List<Catelog2Vo>> categorysFromDb;
		try {
			/*设置过期时间,防止死锁*/
//		stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
			/*如果获得锁 查询数据库*/
			/*存入redis*/
			categorysFromDb = getCategorysFromDb();
			/*释放锁 uuid*/
//		if (uuid.equals(stringRedisTemplate.opsForValue().get("lock"))) {
//			stringRedisTemplate.delete("lock");
//		}

		} finally {
			String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
					"    return redis.call(\"del\",KEYS[1])\n" +
					"else\n" +
					"    return 0\n" +
					"end";
			/*执行redis lua脚本 原子性释放锁 防止锁过期*/
			stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
		}
		return categorysFromDb;
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/4 下午10:30
	 * @Description 从数据库获取数据
	 */
	private Map<String, List<Catelog2Vo>> getCategorysFromDb() {
		log.warn("categorys :查询数据库!");
		Map<String, List<Catelog2Vo>> map = null;
		List<CategoryEntity> categorys = this.list();
		if (categorys != null) {
			List<CategoryEntity> oneLevelCategorys = getParent_cid(categorys, 0L);
			//key:k.getCatId() value: catelog2VoList
			map = oneLevelCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(),
					categoryEntity1 -> {
						//获取一级分类下的二级分类列表
						List<CategoryEntity> categoryEntities = getParent_cid(categorys, categoryEntity1.getCatId());
						//获取二级下三级分类列表
						//处理二级分类
						if (categoryEntities != null) {
							List<Catelog2Vo> catelog2VoList = categoryEntities.stream().map(categoryEntity2 -> {
								List<CategoryEntity> categoryEntityList = getParent_cid(categorys, categoryEntity2.getCatId());
								//处理三级分类
								if (categoryEntityList != null) {
									List<Catelog2Vo.Catelog3Vo> catelog3VoList = categoryEntityList.stream().map(categoryEntity3 -> {
										Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(categoryEntity2.getCatId().toString(), categoryEntity3.getCatId().toString(), categoryEntity3.getName());
										return catelog3Vo;
									}).collect(Collectors.toList());
									Catelog2Vo catelog2Vo = new Catelog2Vo(categoryEntity1.getCatId().toString(), catelog3VoList, categoryEntity2.getCatId().toString(), categoryEntity2.getName());
									return catelog2Vo;
								}
								return null;
							}).collect(Collectors.toList());
							return catelog2VoList;
						}
						return null;
					}));
		}
		/*转为json存入redis*/
		/*设置过期时间*/
//		stringRedisTemplate.opsForValue().set("categorys", JSON.toJSONString(map), 1, TimeUnit.DAYS);
		log.warn("数据已存入redis");
		return map;
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 22:48
	 * @Description 获取前端全部分类数据
	 */
	@Transactional
	@Override
	public Map<String, List<Catelog2Vo>> getCatalogjsonFromDbWithLocalLOck() {
		/*
		优化1:一次查询再处理
		*/
		/*本地锁  只能锁住当前服务*/
		synchronized (this) {
			/*确定缓存中没有数据*/
			String categorysOfRedis = stringRedisTemplate.opsForValue().get("categorys");
			if (!StringUtils.isEmpty(categorysOfRedis)) {
				/*直接返回*/
				return JSON.parseObject(categorysOfRedis, new TypeReference<Map<String, List<Catelog2Vo>>>() {
				});
			}
			log.info("categorys :查询数据库!");
			Map<String, List<Catelog2Vo>> map = null;
			List<CategoryEntity> categorys = this.list();
			if (categorys != null) {
				List<CategoryEntity> oneLevelCategorys = getParent_cid(categorys, 0L);
				//key:k.getCatId() value: catelog2VoList
				map = oneLevelCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(),
						categoryEntity1 -> {
							//获取一级分类下的二级分类列表
							List<CategoryEntity> categoryEntities = getParent_cid(categorys, categoryEntity1.getCatId());
							//获取二级下三级分类列表
							//处理二级分类
							if (categoryEntities != null) {
								List<Catelog2Vo> catelog2VoList = categoryEntities.stream().map(categoryEntity2 -> {
									List<CategoryEntity> categoryEntityList = getParent_cid(categorys, categoryEntity2.getCatId());
									//处理三级分类
									if (categoryEntityList != null) {
										List<Catelog2Vo.Catelog3Vo> catelog3VoList = categoryEntityList.stream().map(categoryEntity3 -> {
											Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(categoryEntity2.getCatId().toString(), categoryEntity3.getCatId().toString(), categoryEntity3.getName());
											return catelog3Vo;
										}).collect(Collectors.toList());
										Catelog2Vo catelog2Vo = new Catelog2Vo(categoryEntity1.getCatId().toString(), catelog3VoList, categoryEntity2.getCatId().toString(), categoryEntity2.getName());
										return catelog2Vo;
									}
									return null;
								}).collect(Collectors.toList());
								return catelog2VoList;
							}
							return null;
						}));
			}
			/*转为json存入redis*/
			/*设置过期时间*/
			stringRedisTemplate.opsForValue().set("categorys", JSON.toJSONString(map), 1, TimeUnit.DAYS);
			return map;
		}
	}

	/*获取对应parentCid的分类信息*/
	private List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntityList, long parentCid) {
		return categoryEntityList.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(parentCid)).collect(Collectors.toList());
	}


	private List<Long> findParentPath(Long catelogId, List<Long> longs) {
		longs.add(catelogId);
		CategoryEntity categoryEntity = this.getById(catelogId);
		if (categoryEntity.getParentCid() != 0) {
			findParentPath(categoryEntity.getParentCid(), longs);
		}
		return longs;
	}

	public List<CategoryEntity> getChildren(CategoryEntity current, List<CategoryEntity> list) {

		List<CategoryEntity> collect = list.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(current.getCatId())).peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, list))).sorted(Comparator.comparingInt(CategoryEntity::getSort)).collect(Collectors.toList());

		return collect;
	}
}