package com.ezcoins.project.consumer.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserLimit;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
import com.ezcoins.project.consumer.service.EzUserLimitService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-05-26
 */
@RestController
@Api(tags = "Admin-用户中心模块")
@RequestMapping("/admin/consumer/user")
public class EzUserController {

    @Autowired
    private EzUserService ezUserService;

    @Autowired
    private EzUserLimitService limitService;


    @ApiOperation(value = "用户列表")
    @PostMapping("userList")
    @AuthToken
    public ResponsePageList<EzUser> userList(@RequestBody SearchModel<EzUser> searchModel) {
        List<EzUser> records = ezUserService.page(searchModel.getPage(), searchModel.getQueryModel()).getRecords();
        List<String> idList = records.stream().map(EzUser::getUserId).collect(Collectors.toList());
        LambdaQueryWrapper<EzUserLimit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(EzUserLimit::getUserId, idList);
        List<EzUserLimit> list = limitService.list(queryWrapper);
        Map<String, EzUserLimit> collect = list.stream().collect(Collectors.toMap(EzUserLimit::getUserId, Function.identity()));
        records.forEach(e -> {
            EzUserLimit limit = collect.get(e.getUserId());
            e.setLogin(limit == null ? "0" : limit.getLogin());
            e.setOrders(limit == null ? "0" : limit.getOrders());
            e.setWithdraw(limit == null ? "0" : limit.getWithdraw());
            e.setBusiness(limit == null ? "0" : limit.getBusiness());
        });
        return ResponsePageList.success(ezUserService.page(searchModel.getPage(), searchModel.getQueryModel()).setRecords(records));
    }


    @ApiOperation(value = "添加用户")
    @PostMapping("addUser")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "用户中心模块", logInfo = "添加用户", operatorType = OperatorType.MANAGE)
    public Response addUser(@RequestBody EzUserReqDto ezUserDto) {
        ezUserService.addUser(ezUserDto, true);
        return Response.success();
    }


    @ApiOperation(value = "修改用户信息")
    @PutMapping("updateUser")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "用户中心模块", logInfo = "修改用户信息", operatorType = OperatorType.MANAGE)
    public Response updateUser(@RequestBody EzUserReqDto ezUserDto) {
        ezUserService.updateUser(ezUserDto);
        return Response.success();
    }

    @ApiOperation(value = "逻辑删除用户")
    @DeleteMapping("deleteUser/{userId}")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "用户中心模块", logInfo = "逻辑删除用户", operatorType = OperatorType.MANAGE)
    public Response deleteUser(@PathVariable String userId) {
        ezUserService.getById(userId);
        return Response.success();
    }


}

