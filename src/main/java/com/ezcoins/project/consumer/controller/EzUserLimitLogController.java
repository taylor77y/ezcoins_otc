package com.ezcoins.project.consumer.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 封号记录表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-10
 */
@RestController
@Api(tags = "Admin-封号记录")
@RequestMapping("/admin/consumer/userLimitLog")
public class EzUserLimitLogController {

    @Autowired
    private EzUserLimitLogService limitLogService;

    @ApiOperation(value = "封号/解号 记录列表")
    @PostMapping("userLimitLogList")
    @AuthToken
    public ResponsePageList<EzUserLimitLog> userList(@RequestBody SearchModel<EzUserLimitLog> searchModel) {
        return ResponsePageList.success(limitLogService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @DeleteMapping("removeUserLimitLogs")
    @ApiOperation(value = "根据id批量删除 封号/解号 记录")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "批量删除封号记录",businessType = BusinessType.DELETE,operatorType= OperatorType.MANAGE)
    public BaseResponse removeSysLogs(@RequestBody List<String> idList){
        limitLogService.removeByIds(idList);
        return BaseResponse.success();
    }
    //



}

