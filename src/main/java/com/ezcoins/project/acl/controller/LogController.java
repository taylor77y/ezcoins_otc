package com.ezcoins.project.acl.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.acl.entity.Log;
import com.ezcoins.project.acl.service.LogService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.system.entity.EzSysLog;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-28
 */
@RestController
@RequestMapping("/acl/log")
@Api(tags = "Admin-管理员日志")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("getAdminLog")
    @ApiOperation(value = "条件查询管理员操作日志")
    @AuthToken
    public ResponsePageList<Log> getAdminLog(@RequestBody SearchModel<Log> searchModel){
        return ResponsePageList.success(logService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @DeleteMapping("removeAdminLog")
    @ApiOperation(value = "批量删除管理员操作日志")
    @AuthToken
    @NoRepeatSubmit
    @com.ezcoins.aspectj.lang.annotation.Log(title = "管理员模块", logInfo ="批量删除管理员操作日志", operatorType = OperatorType.MANAGE)
    public Response removeSysLogs(@RequestBody List<String> idList){
        logService.removeByIds(idList);
        return Response.success();
    }

}

