package com.ezcoins.project.acl.mapper;

import com.ezcoins.project.acl.entity.Menu;
import com.ezcoins.project.acl.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {


    List<Menu> selectAdminRoleMenu(String userId);
}
