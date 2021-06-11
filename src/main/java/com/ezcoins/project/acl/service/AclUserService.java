package com.ezcoins.project.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.acl.entity.AclUser;
import com.ezcoins.project.acl.entity.req.AclUserReqDto;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
public interface AclUserService extends IService<AclUser> {

    // 从数据库中取出用户信息
    AclUser selectByUsername(String username);


    void register(AclUserReqDto user);
}
