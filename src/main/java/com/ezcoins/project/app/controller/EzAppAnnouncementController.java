package com.ezcoins.project.app.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.project.app.entity.EzAppAnnouncement;
import com.ezcoins.project.app.service.EzAppAnnouncementService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.app.entity.req.AnnouncementReqDto;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 平台公告表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-10
 */
@RestController
@Api(tags = "Admin-公告模块")
@RequestMapping("/admin/app/eAppAnnouncement")
public class EzAppAnnouncementController {

    @Autowired
    private EzAppAnnouncementService announcementService;

    @ApiOperation(value = "公告分页列表 ")
    @PostMapping("announcementList")
    @AuthToken
    public ResponsePageList<EzAppAnnouncement> announcementList(@RequestBody SearchModel<EzAppAnnouncement> searchModel) {
        return ResponsePageList.success(announcementService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @PostMapping("announce")
    @ApiOperation(value = "发布公告")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "公告模块", logInfo ="发布公告", operatorType = OperatorType.MANAGE)
    public Response announce(@RequestBody @Validated AnnouncementReqDto announcementReqDto){
        announcementService.announce(announcementReqDto);
        return Response.success();
    }


    @PutMapping("cancelAnnouncement/{id}/{status}")
    @ApiOperation(value = "禁用/打开公告")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "公告模块", logInfo ="禁用/打开公告", operatorType = OperatorType.MANAGE)
    public Response cancelAnnouncement(@PathVariable String id,@PathVariable String status){
        LambdaUpdateWrapper<EzAppAnnouncement> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(EzAppAnnouncement::getId,id).set(EzAppAnnouncement::getIfCancel,status)
                .set(EzAppAnnouncement::getCancelTime, DateUtils.getNowDate());
        announcementService.update(updateWrapper);
        return Response.success();
    }


    @DeleteMapping("removeAnnouncement")
    @ApiOperation(value = "根据id批量删除公告")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "公告模块", logInfo ="批量删除封号记录", operatorType = OperatorType.MANAGE)
    public Response removeAnnouncement(@RequestBody List<String> idList){
        announcementService.removeByIds(idList);
        return Response.success();
    }

}

