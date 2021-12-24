package com.ezcoins.project.app.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.app.AppUpdateType;
import com.ezcoins.project.app.entity.EzAppVersion;
import com.ezcoins.project.app.entity.req.VersionReqDto;
import com.ezcoins.project.app.entity.resp.AppAnnouncementRespDto;
import com.ezcoins.project.app.entity.resp.AppVersionRespDto;
import com.ezcoins.project.app.service.EzAppAnnouncementService;
import com.ezcoins.project.app.service.EzAppVersionService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.CompareVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("getAppVersion")
    public Response<AppVersionRespDto> getAppVersion(@RequestBody VersionReqDto versionReqDto) {
        String locale = LocaleContextHolder.getLocale().toString();

        AppVersionRespDto appVersionRespDto = new AppVersionRespDto();
        //查询所有上架列表
        LambdaQueryWrapper<EzAppVersion> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAppVersion::getIsRacking, AppUpdateType.ONSHELF.getCode());
        queryWrapper.eq(EzAppVersion::getPlatform, versionReqDto.getPlatform());
        queryWrapper.eq(EzAppVersion::getThisVersion,versionReqDto.getThisVersion());
//        Optional<EzAppVersion>
        EzAppVersion one = ezAppVersionService.getOne(queryWrapper);
        //如果当前版本已下架
        if (one==null || CompareVersion.compareVersion(one.getThisVersion(),one.getMinVersion())<0){
            //查询稳定
            LambdaQueryWrapper<EzAppVersion> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(EzAppVersion::getIsRacking, AppUpdateType.ONSHELF.getCode());
            queryWrapper1.eq(EzAppVersion::getPlatform, versionReqDto.getPlatform());
            queryWrapper1.eq(EzAppVersion::getIsDefault,0);
            EzAppVersion defaultVersion = ezAppVersionService.getOne(queryWrapper1);
            appVersionRespDto.setThisVersion(defaultVersion.getThisVersion());
            appVersionRespDto.setAddr(defaultVersion.getAddr());
            if ("zh_CN".equals(locale)) {
                appVersionRespDto.setContent(defaultVersion.getContent());
                appVersionRespDto.setTitle(defaultVersion.getTitle());
            } else if ("en_US".equals(locale)) {
                appVersionRespDto.setContent(defaultVersion.getContentEn());
                appVersionRespDto.setTitle(defaultVersion.getTitleEn());
            }
        }else {
            return Response.error("无需升级版本");
        }
        return Response.success(appVersionRespDto);
    }


    @ApiOperation(value = "公告列表")
    @GetMapping("getAnnouncement")
    @AuthToken
    public ResponseList<AppAnnouncementRespDto> getAnnouncement() {
        List<AppAnnouncementRespDto> list=announcementService.getAnnouncement(getUserId());
        return ResponseList.success(list);
    }

}
