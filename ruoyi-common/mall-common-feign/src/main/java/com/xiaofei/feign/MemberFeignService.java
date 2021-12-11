package com.xiaofei.feign;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.xiaofei.common.member.entity.MemberReceiveAddressEntity;
import com.xiaofei.common.utils.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * User: 李飞
 * Date: 2021/11/23
 * Time: 15:45
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("/member/receiveaddress/auth/receiveaddressinfo")
    ResponseResult<List<MemberReceiveAddressEntity>> getReceiveAddress(@RequestHeader(name = SecurityConstants.DETAILS_USER_ID) Long userId);
}