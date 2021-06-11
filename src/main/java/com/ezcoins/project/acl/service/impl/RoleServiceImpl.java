package com.ezcoins.project.acl.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.project.acl.entity.Menu;
import com.ezcoins.project.acl.entity.Role;
import com.ezcoins.project.acl.entity.RoleMenu;
import com.ezcoins.project.acl.entity.req.AclRoleReqDto;
import com.ezcoins.project.acl.mapper.MenuMapper;
import com.ezcoins.project.acl.mapper.RoleMapper;
import com.ezcoins.project.acl.mapper.RoleMenuMapper;
import com.ezcoins.project.acl.mapper.UserRoleMapper;
import com.ezcoins.project.acl.service.RoleService;
import com.ezcoins.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MenuMapper menuMapper;


    @Autowired
    private RoleMenuMapper roleMenuMapper;



    @Override
    public BaseResponse findRoleByUserId(String userId) {
        List<Menu> authMenuList = userRoleMapper.selectAdminRoleMenu(userId);
        //获取分类一级菜单
        List<Menu> menuList = authMenuList.stream().filter(authMenu -> authMenu.getParentId() == 0)
                .map(menu -> { //设置当前子节点
                    menu.setChildren(getChildList(menu, authMenuList));
                    return menu;
                })
                .collect(Collectors.toList());
        return BaseResponse.success().data("allRolesList",CollectionUtils.isEmpty(menuList) ? authMenuList : menuList);
    }

    //设置子节点
    private List<Menu> getChildList(Menu first, List<Menu> all) {
        List<Menu> list = all.stream().filter(menu -> {
            return menu.getParentId().equals(first.getMenuId());//
        }).map(menu -> {
            menu.setChildren(getChildList(menu, all));
            return menu;
        }).collect(Collectors.toList());
        return list;
    }



    @Override
    public void addAclRole(AclRoleReqDto aclRoleReqDto) {
        //查询名称是否存在
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Role::getName, aclRoleReqDto.getName());
        List<Role> authRoleList = baseMapper.selectList(lambdaQueryWrapper);
        CheckException.check(authRoleList.size() > 0, "权限名称已经存在");

        //查询节点
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Menu::getMenuId,aclRoleReqDto.getChildrenMenuId());
        List<Menu> authMenuList = menuMapper.selectList(queryWrapper);
        List<Integer> childrenMenuId = authMenuList.stream().filter(item -> item.getParentId() > 0).map(Menu::getMenuId).collect(Collectors.toList());

        Role aclRole = new Role();
        BeanUtils.copyBeanProp(aclRole, aclRoleReqDto);
        aclRole.setChildrenMenuId(CollUtil.join(childrenMenuId, ","));
        aclRole.setCreateBy(ContextHandler.getUserName());
        CheckException.checkDb(baseMapper.insert(aclRole), "角色添加失败");

        List<RoleMenu> list = new ArrayList<>();
        List<Integer> menuIds = aclRoleReqDto.getMenuIds();
        RoleMenu authRoleMenu = null;
        //给角色添加权限
        for (int i = 0; i < menuIds.size(); i++) {
            authRoleMenu = new RoleMenu();
            authRoleMenu.setRoleId(aclRole.getRoleId());
            authRoleMenu.setMenuId(menuIds.get(i).toString());
            list.add(authRoleMenu);
        }
        boolean flag = false;
        if (roleMenuMapper.insertList(list) > 0) {
            flag = true;
        }
        CheckException.check(!flag, "角色添加权限失败");
    }

    @Override
    public void updateAclRole(AclRoleReqDto aclRoleReqDto) {
        String roleId = aclRoleReqDto.getRoleId();
        String name = aclRoleReqDto.getName();
        CheckException.checkEmpty(roleId,"角色id不能为空");

        Role authRole = baseMapper.selectById(roleId);
        CheckException.checkNull(authRole, "角色不存在");

        CheckException.check(!authRole.getName().equals(name), () -> {
            LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Role::getName, name);
            List<Role> authRoleList = baseMapper.selectList(lambdaQueryWrapper);
            CheckException.check(authRoleList.size() > 0, "角色名称重复");
        });

        List<Integer> childrenMenuId = new ArrayList<>();
        if (aclRoleReqDto.getChildrenMenuId() != null) {
            childrenMenuId=aclRoleReqDto.getChildrenMenuId();
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuId,childrenMenuId);
            List<Menu> authMenuList = menuMapper.selectList(queryWrapper);
            childrenMenuId = authMenuList.stream().filter(item -> item.getParentId() > 0).map(item -> item.getMenuId()).collect(Collectors.toList());
            authRole.setChildrenMenuId(CollUtil.join(childrenMenuId, ","));
        }

        authRole.setName(name);
        authRole.setUpdateBy(ContextHandler.getUserName());
        CheckException.checkDb(baseMapper.updateById(authRole), "角色更新失败");


        //查询角色权限
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> list1 = roleMenuMapper.selectList(queryWrapper);
        List<String> list2 = list1.stream().map(item -> item.getMenuId()).collect(Collectors.toList());

        List<Integer> menuIds = new ArrayList<>();
        if (aclRoleReqDto.getMenuIds() != null) {
            menuIds = aclRoleReqDto.getMenuIds();
            if (!checkDifference(menuIds,list2)) {
                //删除关联关系
                HashMap<String,Object> map = new HashMap<>(2);
                map.put("roleId", roleId);
                map.put("menuIds", list2);
                roleMenuMapper.deleteBatchMenus(map);
                List<RoleMenu> list = new ArrayList<>();
                RoleMenu authRoleMenu = null;
                //给角色添加权限
                for (int i = 0; i < menuIds.size(); i++) {
                    authRoleMenu = new RoleMenu();
                    authRoleMenu.setRoleId(authRole.getRoleId());
                    authRoleMenu.setMenuId(menuIds.get(i).toString());
                    list.add(authRoleMenu);
                }
                boolean flag = false;
                if (roleMenuMapper.insertList(list) > 0) {
                    flag = true;
                }
                CheckException.check(!flag, "角色修改权限失败");
            }
        }

    }

    // 比较两个集合是否相同
    private static boolean checkDifference(List<Integer> list, List<String> list1) {
        return list.stream().sorted().map(item -> item.toString()).collect(Collectors.joining())
                .equals(list1.stream().sorted().collect(Collectors.joining()));
    }


}