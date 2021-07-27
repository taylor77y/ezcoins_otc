package com.ezcoins.project.consumer.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.response.Response;
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
@Api(tags = "Admin-用户封禁模块")
@RequestMapping("/admin/consumer/userLimitLog")
public class EzUserLimitLogController {
    @Autowired
    private EzUserLimitLogService limitLogService;
    @ApiOperation(value = "封号记录列表")
    @PostMapping("userLimitLogList")
    @AuthToken
    public ResponsePageList<EzUserLimitLog> userList(@RequestBody SearchModel<EzUserLimitLog> searchModel) {
        return ResponsePageList.success(limitLogService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

}

