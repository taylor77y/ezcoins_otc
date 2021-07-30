package com.ezcoins.project.consumer.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUserLimit;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.project.consumer.service.EzUserLimitService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 封号列表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-26
 */
@RestController
@Api(tags = "Admin-用户封禁模块")
@RequestMapping("/admin/consumer/ez-user-limit")
public class EzUserLimitController {

    @Autowired
    private EzUserLimitService limitService;
    @ApiOperation(value = "封号列表")
    @PostMapping("userLimitList")
    @AuthToken
    public ResponsePageList<EzUserLimit> userLimitList(@RequestBody SearchModel<EzUserLimit> searchModel) {
        return ResponsePageList.success(limitService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }
    @ApiOperation(value = "封号")
    @PostMapping("title")
    @AuthToken
    @Log(title = "用户中心模块", logInfo ="封号", operatorType = OperatorType.MANAGE)
    public Response title(@RequestBody UserLimitReqDto userLimitReqDto) {
        return limitService.title(userLimitReqDto);
    }

    @ApiOperation(value = "")
    @PutMapping("unblock/{userId}/{type}")
    @AuthToken
    @Log(title = "用户中心模块", logInfo ="解封", operatorType = OperatorType.MANAGE)
    public Response unblock(@PathVariable String userId,@PathVariable String type) {
        return limitService.unblock(userId,type);
    }
}

