package com.ezcoins.project.system.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.system.entity.EzSysLog;
import com.ezcoins.project.system.entity.EzSysTips;
import com.ezcoins.project.system.entity.req.SysTipsReqDto;
import com.ezcoins.project.system.service.EzSysLogService;
import com.ezcoins.project.system.service.EzSysTipsService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("sendSysTip")
    @ApiOperation(value = "发送站内信到用户")
    @AuthToken
    @Log(title = "系统模块", logInfo ="发送站内信到用户", operatorType = OperatorType.MANAGE)
    public Response sendSysTip(@RequestBody SysTipsReqDto sysTipsReqDto){
        if (StringUtils.isEmpty(sysTipsReqDto.getUserId())){
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

