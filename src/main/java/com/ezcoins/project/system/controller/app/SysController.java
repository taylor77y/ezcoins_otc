package com.ezcoins.project.system.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.req.PageQuery;
import com.ezcoins.project.otc.entity.resp.Info;
import com.ezcoins.project.system.controller.EzSysBoomController;
import com.ezcoins.project.system.entity.EzSysLog;
import com.ezcoins.project.system.entity.EzSysTips;
import com.ezcoins.project.system.service.EzSysBoomService;
import com.ezcoins.project.system.service.EzSysTipsService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/28 18:23
 * @Version:1.0
 */
@RestController
@Api(tags = "App-系统模块")
@RequestMapping("/system/app")
public class SysController {

    @Autowired
    private EzSysTipsService tipsService;


    @Autowired
    private EzSysBoomService boomService;


    @PostMapping("sysTips")
    @ApiOperation(value = "站内信息")
    @AuthToken
    public ResponseList<EzSysTips> sysTips(){
        LambdaQueryWrapper<EzSysTips> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzSysTips::getUserId,ContextHandler.getUserId());
        queryWrapper.orderByDesc(EzSysTips::getCreateTime);
        return ResponseList.success(tipsService.list(queryWrapper));
    }

    @GetMapping("readTips")
    @ApiOperation(value = "读取站内信息")
    @AuthToken
    public Response readTips(){
        LambdaUpdateWrapper<EzSysTips> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(EzSysTips::getUserId,ContextHandler.getUserId());
        updateWrapper.eq(EzSysTips::getIsRead,"1");
        updateWrapper.set(EzSysTips::getIsRead,"0");
        tipsService.update(updateWrapper);
        return Response.success();
    }

    @ApiOperation(value = "首页信息")
    @PostMapping("info")
    @AuthToken
    public Response<Info> info(@RequestBody PageQuery pageQuery) {
        return boomService.info(pageQuery);
    }




}
