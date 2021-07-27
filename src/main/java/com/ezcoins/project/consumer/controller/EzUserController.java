package com.ezcoins.project.consumer.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-05-26
 */
@RestController
@Api(tags = "Admin-用户模块")
@RequestMapping("/admin/consumer/user")
public class EzUserController {

    @Autowired
    private EzUserService ezUserService;


    @ApiOperation(value = "用户列表")
    @PostMapping("userList")
    @AuthToken
    public ResponsePageList<EzUser> userList(@RequestBody SearchModel<EzUser> searchModel) {
        return ResponsePageList.success(ezUserService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


    @ApiOperation(value = "添加用户")
    @PostMapping("addUser")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "添加用户", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public Response addUser(@RequestBody EzUserReqDto ezUserDto) {
        ezUserService.addUser(ezUserDto,true);
        return Response.success();
    }


    @ApiOperation(value = "修改用户信息")
    @PutMapping("updateUser")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "修改用户信息", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public Response updateUser(@RequestBody EzUserReqDto ezUserDto) {
        ezUserService.updateUser(ezUserDto);
        return Response.success();
    }

    @ApiOperation(value = "逻辑删除用户")
    @DeleteMapping("deleteUser/{userId}")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "逻辑删除用户", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public Response deleteUser(@PathVariable String userId) {
        ezUserService.getById(userId);
        return Response.success();
    }





}

