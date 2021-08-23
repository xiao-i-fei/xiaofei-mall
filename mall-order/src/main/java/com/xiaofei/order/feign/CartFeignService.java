package com.xiaofei.order.feign;

import com.ruoyi.common.core.constant.CacheConstants;
import com.xiaofei.common.cart.entity.CartEntity;
import com.xiaofei.common.utils.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * User: 李飞
 * Date: 2021/8/21
 * Time: 15:10
 */
@FeignClient("mall-cart")
public interface CartFeignService {

    @PostMapping("/cart/auth/querybyids")
    ResponseResult<List<CartEntity>> queryCartByUserIds(@RequestBody String ids);

    @PostMapping("/cart/auth/querycheckcart")
    ResponseResult<List<CartEntity>> queryCheckCart(@RequestHeader(name = CacheConstants.DETAILS_USER_ID) Long userId);
}