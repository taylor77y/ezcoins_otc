package com.ezcoins.project.acl.mapper;

import com.ezcoins.project.acl.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    int insertList(List<RoleMenu> list);

    int deleteBatchMenus(HashMap<String, Object> map);
}
