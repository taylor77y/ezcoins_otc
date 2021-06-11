package com.ezcoins.project.consumer.mapper;

import com.ezcoins.project.consumer.entity.EzUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanglei
 * @since 2021-05-26
 */
public interface EzUserMapper extends BaseMapper<EzUser> {
    int checkUserUnique(@Param("userName") String userName, @Param("phone")String phone, @Param("email")String email, @Param("inviteCode") String inviteCode);

   EzUser selectUserBy(@Param("userName") String userName, @Param("phone")String phone, @Param("email")String email, @Param("inviteCode") String inviteCode);
}
