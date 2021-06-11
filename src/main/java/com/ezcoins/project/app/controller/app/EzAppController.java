package com.ezcoins.project.app.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.IgnoreUserToken;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.app.AppUpdateType;
import com.ezcoins.project.app.entity.EzAppVersion;
import com.ezcoins.project.app.entity.resp.AppVersionRespDto;
import com.ezcoins.project.app.service.EzAppAnnouncementService;
import com.ezcoins.project.app.service.EzAppVersionService;
import com.ezcoins.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/10 18:28
 * @Version:1.0
 */
@Api(tags = "APP- 版本 公告模块")
@RestController
@Accessors(chain = true)
@RequestMapping("/ezApp/app")
public class EzAppController extends BaseController {

    @Autowired
    private EzAppAnnouncementService announcementService;

    @Autowired
    private EzAppVersionService ezAppVersionService;

    @ApiOperation(value = "获取 上架app版本列表 ")
    @GetMapping("getAppVersion")
    @IgnoreUserToken
    public ResponseList<EzAppVersion> getAppVersion() {
        //查询所有上架列表
        LambdaQueryWrapper<EzAppVersion> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAppVersion::getIsRacking, AppUpdateType.ONSHELF);
        return ResponseList.success(ezAppVersionService.list(queryWrapper));
    }

    @ApiOperation(value = "公告列表 ")
    @PostMapping("getAnnouncement")
    @AuthToken
    public ResponseList<AppVersionRespDto> getAnnouncement() {
        List<AppVersionRespDto> list=announcementService.getAnnouncement(getUserId());
        return ResponseList.success(list);
    }

}
