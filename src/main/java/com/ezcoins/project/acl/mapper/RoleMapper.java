package com.ezcoins.project.acl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezcoins.project.acl.entity.Menu;
import com.ezcoins.project.acl.entity.Role;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
public interface RoleMapper extends BaseMapper<Role> {


    List<Menu> selectAdminRoleMenu(String userId);

    List<Menu> selectAllMenu();
}
