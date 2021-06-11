package com.ezcoins.project.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.acl.entity.Role;
import com.ezcoins.project.acl.entity.req.AclRoleReqDto;
import com.ezcoins.response.BaseResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
public interface RoleService extends IService<Role> {

    BaseResponse findRoleByUserId(String userId);

    void addAclRole(AclRoleReqDto aclRoleReqDto);


    void updateAclRole(AclRoleReqDto aclRoleReqDto);
}
