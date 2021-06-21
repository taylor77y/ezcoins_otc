package com.ezcoins.project.system.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.system.entity.EzSysLog;
import com.ezcoins.project.system.entity.EzSysLog;
import com.ezcoins.project.system.service.EzSysLogService;
import com.ezcoins.project.system.service.EzSysLogService;
import com.ezcoins.response.BaseResponse;
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
 * @since 2021-06-05
 */
@RestController
@Api(tags = "Admin-日志模块")
@RequestMapping("/admin/system/SysLog")
public class EzSysLogController {

    @Autowired
    private EzSysLogService ezSysLogService;

    @PostMapping("getSysLog")
    @ApiOperation(value = "条件查询系统日志")
    @AuthToken
    public ResponsePageList<EzSysLog> getSysLog(@RequestBody SearchModel<EzSysLog> searchModel){
        return ResponsePageList.success(ezSysLogService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @DeleteMapping("removeSysLogs")
    @ApiOperation(value = "根据id批量删除系统日志")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "批量删除系统日志",businessType = BusinessType.DELETE,operatorType= OperatorType.MANAGE)
    public BaseResponse removeSysLogs(@RequestBody List<String> idList){
        ezSysLogService.removeByIds(idList);
        return BaseResponse.success();
    }





}

