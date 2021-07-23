package com.ezcoins.project.system.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.system.entity.EzSysLogininfor;
import com.ezcoins.project.system.service.EzSysLogininforService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 登录日志 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-05
 */
@RestController
@Api(tags = "Admin-日志模块")
@RequestMapping("/admin/system/SysLogininfor")
public class EzSysLogininforController {

    @Autowired
    private EzSysLogininforService ezSysLogininforService;

    @PostMapping("getSysLogininfor")
    @ApiOperation(value = "条件查询系统日志")
    @AuthToken
    public ResponsePageList<EzSysLogininfor> getSysLogininfor(@RequestBody SearchModel<EzSysLogininfor> searchModel){
        return ResponsePageList.success(ezSysLogininforService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @DeleteMapping("removeSysLogininfor")
    @ApiOperation(value = "根据id批量删除登录日志")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "批量删除登录日志",businessType = BusinessType.CLEAN,operatorType= OperatorType.MANAGE)
    public Response removeSysLogininfor(@RequestBody List<String> idList){
        ezSysLogininforService.removeByIds(idList);
        return Response.success();
    }
}

