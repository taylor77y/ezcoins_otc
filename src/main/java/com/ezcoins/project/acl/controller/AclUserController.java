package com.ezcoins.project.acl.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.acl.entity.AclUser;
import com.ezcoins.project.acl.entity.req.AclUserReqDto;
import com.ezcoins.project.acl.service.AclUserService;
import com.ezcoins.project.acl.service.RoleService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.EncoderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@RestController
@Api(tags = "Admin-管理员管理")
@RequestMapping("/admin/acl/user")
public class AclUserController {

    @Autowired
    private AclUserService userService;

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "获取管理用户分页列表")
    @PostMapping("aclUserList")
    @AuthToken
    public ResponsePageList<AclUser> aclUserList(@RequestBody SearchModel<AclUser> searchModel) {
        return ResponsePageList.success(userService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "新增管理用户")
    @PostMapping("register")
    @AuthToken
    @Log(title = "管理员模块", logInfo = "新增管理用户", operatorType = OperatorType.MANAGE)
    public Response save(@RequestBody @Validated AclUserReqDto user) {
        userService.register(user);
        return Response.success();
    }

    @ApiOperation(value = "根据id删除管理用户")
    @DeleteMapping("remove/{id}")
    @AuthToken
    @Log(title = "管理员模块", logInfo = "删除管理用户", operatorType = OperatorType.MANAGE)
    public Response remove(@PathVariable String id) {
        userService.removeById(id);
        return Response.success();
    }

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("toAssign/{userId}")
    @AuthToken
    public Response toAssign(@PathVariable String userId) {
        return roleService.findRoleByUserId(userId);
    }

    @ApiOperation(value = "修改自身密码")
    @Log(title = "管理员模块", logInfo = "修改自身密码", operatorType = OperatorType.MANAGE)
    @PutMapping("updatePwd")
    @AuthToken
    public Response updatePwd(@RequestBody AclUserReqDto user) {
        LambdaUpdateWrapper<AclUser> lambdaUpdateWrapper = Wrappers.<AclUser>lambdaUpdate();
        lambdaUpdateWrapper.eq(AclUser::getId,ContextHandler.getUserId());
        lambdaUpdateWrapper.set(AclUser::getPassword, EncoderUtil.encode(user.getPassword()));
        userService.update(lambdaUpdateWrapper);
        return Response.success();
    }
}
