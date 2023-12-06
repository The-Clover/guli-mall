package com.lsh.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lsh.cart.feign.ProductFeignService;
import com.lsh.cart.interceptor.CartInterceptor;
import com.lsh.cart.service.CartService;
import com.lsh.cart.vo.Cart;
import com.lsh.cart.vo.CartItem;
import com.lsh.cart.vo.UserInfoTo;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.vo.SkuInfoVo;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private final String CART_PREFIX = "gulimall:cart:";

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * //TODO
     *
     * @param
     * @return
     * @throws
     * @date 2021/8/23 23:50
     * @Description 添加到购物车
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> boundHashOps = getCart();
        String item = (String) boundHashOps.get(skuId.toString());
        if (!StringUtils.isEmpty(item)) {
            /*商品已存在*/
            CartItem cartItem = JSON.parseObject(item, CartItem.class);
            /*更改数量*/
            if (num > 0) {
                cartItem.setCount(cartItem.getCount() + num);
                log.info("添加到购物车!");
            }
            System.out.println(cartItem);

            boundHashOps.put(skuId.toString(), JSON.toJSONString(cartItem));

            return cartItem;
        }
        /*要添加的新商品*/
        CartItem cartItem = new CartItem();

        /*远程查询当前要添加的商品的信息*/
        CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
            R r = productFeignService.info(skuId);
            if (r.getCode() != 0) {
                throw new FeignException.NotFound("找不到商品信息", null, null);
            }
            SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
            cartItem.setCheck(true);
            cartItem.setCount(1);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setTitle(skuInfo.getSkuTitle());
            cartItem.setSkuId(skuId);
            cartItem.setPrice(skuInfo.getPrice());
        }, threadPoolExecutor);


        /*3 远程查询sku的组合信息 attrs*/
        CompletableFuture<Void> getSkuSaleAttrValueStringList = CompletableFuture.runAsync(() -> {

            List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
            if (skuSaleAttrValues != null) {
                cartItem.setSkuAttr(skuSaleAttrValues);
            }
        }, threadPoolExecutor);


        /*两个线程的异步任务完成以后再放入redis*/
        try {
            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValueStringList).get();
            /*转为json 放入购物车redis*/
            String s = JSON.toJSONString(cartItem);
            boundHashOps.put(skuId.toString(), s);
            log.info("添加购物车成功" + cartItem);
            return cartItem;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return null;
    }

    /*获取购物车中的购物项*/
    @Override
    public CartItem getCartItem(Long skuId) {

        BoundHashOperations<String, Object, Object> cart = getCart();
        String o = (String) cart.get(skuId.toString());

        return JSON.parseObject(o, CartItem.class);
    }

    /**
     * //TODO
     *
     * @param
     * @return
     * @throws
     * @date 2021/8/24 23:45
     * @Description 查询整个购物车
     */
    @Override
    public Cart getCartList() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //		2 判断是否登录
        Cart cart = new Cart();
        String cartKey;
        log.info("user-key:" + userInfoTo.getUserKey());
        if (userInfoTo.getUserId() != null) {
            /*获取临时购物车*/
            if (!StringUtils.isEmpty(userInfoTo.getUserKey()) && !Objects.equals(userInfoTo.getUserKey(), userInfoTo.getUserId().toString())) {
                /*合并购物车*/
                log.info("合并购物车!");
                List<CartItem> cartItem2 = getCartItem(CART_PREFIX + userInfoTo.getUserKey());
                if (cartItem2 != null) {
                    for (CartItem cartItem : cartItem2) {
                        addToCart(cartItem.getSkuId(), cartItem.getCount());
                    }
                    /*清除临时购物车*/
                    if (!Objects.equals(userInfoTo.getUserKey(), userInfoTo.getUserId().toString())) {
                        clearCart(CART_PREFIX + userInfoTo.getUserKey());
                    }
                }
            }
            /*已登录 获取登录购物车和临时购物车合并*/
            cartKey = CART_PREFIX + userInfoTo.getUserId();
            /*获取登录购物车 包含所有购物车*/
            List<CartItem> cartItem1 = getCartItem(cartKey);
            cart.setItems(cartItem1);
        } else {
            /*未登录*/
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItem = getCartItem(cartKey);
            cart.setItems(cartItem);
        }

        log.info(cartKey);

        return cart;
    }


    /**
     * //TODO
     *
     * @param
     * @return
     * @throws
     * @date 2021/8/23 23:59
     * @Description 获取要操作的购物车
     */
    private BoundHashOperations<String, Object, Object> getCart() {
        // 1.通过拦截器的threadLocal获取用户信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

//		2 判断是否登录
        String cartKey;
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        log.info(cartKey);

        /*操作 redis 购物车 判断购物车是否存在商品*/
        System.out.println(cartKey);
        BoundHashOperations<String, Object, Object> boundHashOps = stringRedisTemplate.boundHashOps(cartKey);
        return boundHashOps;
    }


    public List<CartItem> getCartItem(String cartKey) {

        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map(value ->
                    JSON.parseObject((String) value, CartItem.class)
            ).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    /**
     * @param skuId
     * @param checked 购物项选中状态
     */
    @Override
    public void checkItem(Long skuId, Integer checked) {

        CartItem cartItem = getCartItem(skuId);
        /*改变状态*/
        cartItem.setCheck(checked == 1);
        String str = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cart = getCart();
        cart.put(skuId.toString(), str);
    }

    @Override
    public void updateNumItem(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        if (num > 0) {
            cartItem.setCount(num);
            BoundHashOperations<String, Object, Object> cart = getCart();
            cart.put(skuId.toString(), JSON.toJSONString(cartItem));
        }
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cart = getCart();
        cart.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getOrderItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            System.out.println("无用户信息");
            return null;
        }
        System.out.println(userInfoTo);
        List<CartItem> cartItem = getCartItem(CART_PREFIX + userInfoTo.getUserId());
        if (cartItem != null && cartItem.size() > 0) {
            List<CartItem> collect = cartItem.stream().peek(item -> {
                /*查询最新的价格*/
                BigDecimal price = productFeignService.getPrice(item.getSkuId());
                if (price != null) {
                    item.setPrice(price);
                    System.out.println("购物车查询完成..............");
                }
            }).filter(CartItem::isCheck).collect(Collectors.toList());
            return collect;
        }
        System.out.println("未查询到购物车..................");
        return null;
    }

}
