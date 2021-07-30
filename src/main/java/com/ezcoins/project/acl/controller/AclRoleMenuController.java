package com.ezcoins.project.acl.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.acl.entity.Menu;
import com.ezcoins.project.acl.entity.Role;
import com.ezcoins.project.acl.entity.RoleMenu;
import com.ezcoins.project.acl.entity.req.AclMenuReqDto;
import com.ezcoins.project.acl.entity.req.AclRoleReqDto;
import com.ezcoins.project.acl.service.MenuService;
import com.ezcoins.project.acl.service.RoleMenuService;
import com.ezcoins.project.acl.service.RoleService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
@RestController
@Api(tags = "Admin-权限 角色 模块")
@RequestMapping("/admin/acl/roleMenu")
public class AclRoleMenuController extends BaseController {

    @Autowired
    private MenuService menuService;


    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     *
     * @return
     */
    @ApiOperation(value = "当前用户路由")
    @GetMapping("selectAdminRoleMenu")
    @AuthToken
    public Response selectAdminRoleMenu(){
        List<Menu> authMenuList =menuService.selectAdminRoleMenu(getUserId());
        Map<String, Object> result = new HashMap<>(2);
        result.put("list",authMenuList);
        return Response.success(result);
    }

    /**
     * 查询角色列表
     *
     * @param
     * @return
     */
    @ApiOperation(value = "查询角色列表")
    @PostMapping("/findAclRoleList")
    @AuthToken
    public ResponsePageList<Role> findAuthRoleList(@RequestBody SearchModel<Role> searchModel) {
        Page<Role> pageParam=roleService.page(searchModel.getPage(), searchModel.getQueryModel());
        List<Role> list = pageParam.getRecords();
        list.forEach(role -> {
            String childrenMenuId = role.getChildrenMenuId();
            role.setChildrenMenuIds(childrenMenuId.split(","));
        });
        return ResponsePageList.success(list.size(),list);
    }

    /**
     * 添加角色
     *
     * @return
     */
    @ApiOperation(value = "添加角色")
    @PostMapping("/addAclRole")
    @AuthToken
    @Log(title = "管理员模块", logInfo ="添加角色", operatorType = OperatorType.MANAGE)
    public Response addAclRole(@RequestBody @Validated AclRoleReqDto aclRoleReqDto) {
        roleService.addAclRole(aclRoleReqDto);
        return Response.success();
    }
    /**
     * 删除角色
     *
     * @return
     */
    @ApiOperation(value = "根据id列表删除角色")
    @DeleteMapping("/delAclRole/{id}")
    @AuthToken
    @Log(title = "管理员模块", logInfo ="删除角色", operatorType = OperatorType.MANAGE)
    public Response delAuthRole(@PathVariable String id) {
        //删除角色权限关联表
        LambdaQueryWrapper<RoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleMenu::getRoleId, id);
        roleMenuService.remove(lambdaQueryWrapper);
        roleService.removeById(id);
        return Response.success();
    }
    /**
     * 修改角色
     *
     * @return
     */
    @ApiOperation(value = "修改角色")
    @PostMapping("/updateAclRole")
    @AuthToken
    @Log(title = "管理员模块", logInfo ="修改角色", operatorType = OperatorType.MANAGE)
    public Response updateAclRole(@RequestBody @Validated AclRoleReqDto aclRoleReqDto) {
        roleService.updateAclRole(aclRoleReqDto);
        return Response.success();
    }


    /**
     * 添加权限
     *
     * @return
     */

    @ApiOperation(value = "添加权限")
    @PostMapping("/addAuthMenu")
    @AuthToken
    @Log(title = "管理员模块", logInfo ="添加权限", operatorType = OperatorType.MANAGE)
    public Response addAuthMenu(@RequestBody @Validated AclMenuReqDto aclMenuReqDto) {
        menuService.addAclMenu(aclMenuReqDto);
        return Response.success();
    }

    /**
     * 删除权限
     *
     * @return
     */
    @ApiOperation(value = "删除权限")
    @DeleteMapping("/delAclMenu/{id}")
    @AuthToken
    @Log(title = "管理员模块", logInfo ="删除权限", operatorType = OperatorType.MANAGE)
    public Response delAclMenu(@PathVariable Integer id) {
        menuService.delAclMenu(id);
        return Response.success();
    }

    /**
     * 修改权限
     *
     * @return
     */
    @ApiOperation(value = "修改权限")
    @PostMapping("/updateAclMenu")
    @AuthToken
    @Log(title = "管理员模块", logInfo ="修改权限", operatorType = OperatorType.MANAGE)
    public Response updateAuthMenu(@RequestBody @Validated AclMenuReqDto aclMenuReqDto) {
        menuService.updateAclMenu(aclMenuReqDto);
        return Response.success();
    }


    /**
     * 菜单树
     */
    @ApiOperation(value = "菜单树")
    @GetMapping("/findMenuTree")
    @AuthToken
    public Response findMenuTree() {
        Map<String, Object> result = new HashMap<>(2);
        result.put("list",menuService.findMenuTree());
        return Response.success(result);
    }



    @ApiOperation(value = "菜单列表")
    @PostMapping("/findAuthMenus")
    @AuthToken
    public ResponsePageList<Menu> findAuthMenus(@RequestBody SearchModel<Menu> searchModel) {
        return ResponsePageList.success(menuService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }





}

