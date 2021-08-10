package com.ezcoins.project.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.system.entity.EzSysBoom;
import com.ezcoins.project.system.entity.EzSysLog;
import com.ezcoins.project.system.entity.EzSysTips;
import com.ezcoins.project.system.entity.req.SysTipsReqDto;
import com.ezcoins.project.system.service.EzSysLogService;
import com.ezcoins.project.system.service.EzSysTipsService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统提示 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-28
 */
@RestController
@Api(tags = "Admin-系统模块")
@RequestMapping("/admin/system/ez-sys-tips")
public class EzSysTipsController {
    @Autowired
    private EzSysTipsService tipsService;

    @Autowired
    private EzUserService userService;


    @PostMapping("getSysTipList")
    @ApiOperation(value = "站内信发送列表")
    @AuthToken
    public ResponsePageList<EzSysTips> getBoomList(@RequestBody SearchModel<EzSysTips> searchModel){
        return ResponsePageList.success(tipsService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @DeleteMapping("clean")
    @ApiOperation(value = "清除系统一周之外的站内信")
    @AuthToken
    public Response clean(){
        LambdaQueryWrapper<EzSysTips> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.lt(EzSysTips::getCreateTime,DateUtils.getNdayStart(-7));
        tipsService.remove(queryWrapper);
        return Response.success();
    }


    @PostMapping("sendSysTip")
    @ApiOperation(value = "发送站内信到用户")
    @AuthToken(CODE = "39")
    @Log(title = "系统模块", logInfo ="发送站内信到用户", operatorType = OperatorType.MANAGE)
    public Response sendSysTip(@RequestBody SysTipsReqDto sysTipsReqDto){
        if (StringUtils.isNotEmpty(sysTipsReqDto.getUserId())){
            EzSysTips ezSysTips = new EzSysTips();
            BeanUtils.copyBeanProp(ezSysTips,sysTipsReqDto);
            tipsService.save(ezSysTips);
        }else {
            List<EzSysTips> ezSysTipsList = new ArrayList<>();
            userService.list().stream().map(EzUser::getUserId).collect(Collectors.toList()).forEach(e->{
                EzSysTips ezSysTip = new EzSysTips();
                BeanUtils.copyBeanProp(ezSysTip,sysTipsReqDto);
                ezSysTip.setUserId(e);
                ezSysTipsList.add(ezSysTip);
            });
            tipsService.saveBatch(ezSysTipsList);

        }
        return Response.success();
    }
}

