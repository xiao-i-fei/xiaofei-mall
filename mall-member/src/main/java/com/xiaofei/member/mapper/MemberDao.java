package com.xiaofei.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaofei.common.member.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 *
 * @author xiaofei
 * @email 1903078434@qq.com
 * @date 2021-06-27 13:32:34
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

}