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
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse selectAdminRoleMenu(){
        List<Menu> authMenuList =menuService.selectAdminRoleMenu(getUserId());
        return BaseResponse.success().data("list",authMenuList);
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
    @Log(title = "添加角色", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse addAclRole(@RequestBody @Validated AclRoleReqDto aclRoleReqDto) {
        roleService.addAclRole(aclRoleReqDto);
        return BaseResponse.success();
    }

    /**
     * 删除角色
     *
     * @return
     */
    @ApiOperation(value = "根据id列表删除角色")
    @DeleteMapping("/delAclRole/{id}")
    @AuthToken
    @Log(title = "删除角色", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public BaseResponse delAuthRole(@PathVariable String id) {
        //删除角色权限关联表
        LambdaQueryWrapper<RoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleMenu::getRoleId, id);
        roleMenuService.remove(lambdaQueryWrapper);
        roleService.removeById(id);
        return BaseResponse.success();
    }


    /**
     * 修改角色
     *
     * @return
     */
    @ApiOperation(value = "修改角色")
    @PostMapping("/updateAclRole")
    @AuthToken
    @Log(title = "修改角色", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse updateAclRole(@RequestBody AclRoleReqDto aclRoleReqDto) {
        roleService.updateAclRole(aclRoleReqDto);
        return BaseResponse.success();
    }


    /**
     * 添加权限
     *
     * @return
     */

    @ApiOperation(value = "添加权限")
    @PostMapping("/addAuthMenu")
    @AuthToken
    @Log(title = "添加权限", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse addAuthMenu(@RequestBody @Validated AclMenuReqDto aclMenuReqDto) {
        menuService.addAclMenu(aclMenuReqDto);
        return BaseResponse.success();
    }

    /**
     * 删除权限
     *
     * @return
     */
    @ApiOperation(value = "删除权限")
    @DeleteMapping("/delAclMenu/{id}")
    @AuthToken
    @Log(title = "删除权限", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public BaseResponse delAclMenu(@PathVariable Integer id) {
        menuService.delAclMenu(id);
        return BaseResponse.success();
    }

    /**
     * 修改权限
     *
     * @return
     */
    @ApiOperation(value = "修改权限")
    @PostMapping("/updateAclMenu")
    @AuthToken
    @Log(title = "删除权限", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public BaseResponse updateAuthMenu(@RequestBody @Validated AclMenuReqDto aclMenuReqDto) {
        menuService.updateAclMenu(aclMenuReqDto);
        return BaseResponse.success();
    }


    /**
     * 菜单树
     */
    @ApiOperation(value = "菜单树")
    @GetMapping("/findMenuTree")
    @AuthToken
    public BaseResponse findMenuTree() {
        return BaseResponse.success().data("list", menuService.findMenuTree());
    }



    @ApiOperation(value = "菜单列表")
    @PostMapping("/findAuthMenus")
    @AuthToken
    public ResponsePageList<Menu> findAuthMenus(@RequestBody SearchModel<Menu> searchModel) {
        return ResponsePageList.success(menuService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }





}

