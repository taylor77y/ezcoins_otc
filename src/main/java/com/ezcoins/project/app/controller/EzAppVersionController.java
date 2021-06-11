package com.ezcoins.project.app.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.app.AppUpdateType;
import com.ezcoins.project.app.entity.EzAppVersion;
import com.ezcoins.project.app.entity.req.AppStatusReqDto;
import com.ezcoins.project.app.entity.req.AppVersionReqDto;
import com.ezcoins.project.app.service.EzAppVersionService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-05
 */
@RestController
@Api(tags = "Admin-app版本控制模块")
@RequestMapping("/admin/app/ezAppVersion")
public class EzAppVersionController {

    @Autowired
    private EzAppVersionService ezAppVersionService;

    @PostMapping("getAppVersion")
    @ApiOperation(value = "条件查询app版本")
    @AuthToken
    public ResponsePageList<EzAppVersion> getAppVersion(@RequestBody SearchModel<EzAppVersion> searchModel) {
        return ResponsePageList.success(ezAppVersionService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


    @ApiOperation(value = "上传app最新版本")
    @PostMapping("addAppVersion")
    @AuthToken
    @Log(title = "上传app最新版本", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse addAppVersion(
            @ApiParam(name = "ezcoinsAppVersion", value = "上传对象", required = true)
            @RequestBody @Validated AppVersionReqDto appVersionReqDto) {
        EzAppVersion ezAppVersion = new EzAppVersion();
        BeanUtils.copyBeanProp(ezAppVersion,appVersionReqDto);
        ezAppVersionService.addAppVersion(ezAppVersion);
        return BaseResponse.success();
    }


    @ApiOperation(value = "上传App安装包")
    @PostMapping({"uploadAppInstallPackage/{platform}/{id}","uploadAppInstallPackage/{platform}"})
    @AuthToken
    public BaseResponse uploadAppInstallPackage(
            @ApiParam(name = "file", value = "上传文件", required = true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(name = "platform", value = "app平台", required = true)
            @PathVariable("platform") String platform,
            @ApiParam(name = "id", value = "id null:直接添加  NotNull：修改版本包地址", required = true)
            @PathVariable(value = "id",required = false) String id) {
        String url = ezAppVersionService.uploadAppInstallPackage(file, platform, id);
        if (null==url){
            return BaseResponse.success();
        }
        return BaseResponse.success().data("url",url);
    }


    @ApiOperation(value = "修改app上下架状态")
    @PostMapping("updateAppStatus")
    @AuthToken
    @Log(title = "修改app状态", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public BaseResponse updateAppStatus(@RequestBody @Validated AppStatusReqDto statusReqDto) {
        ezAppVersionService.updateAppStatus(statusReqDto);
        return BaseResponse.success();
    }



    @ApiOperation(value = "修改稳定版app")
    @PostMapping("updateStableApp/{platform}/{id}")
    @AuthToken
    @NoRepeatSubmit
    @Log(title = "updateStableApp", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse updateStableApp(
            @ApiParam(name = "platform", value = "app平台", required = true)
            @PathVariable("platform") String platform,
            @ApiParam(name = "id", value = "id ", required = true)
            @PathVariable(value = "id") String id) {
        ezAppVersionService.updateStableApp(platform,id);
        return BaseResponse.success();
    }


    @ApiOperation(value = "修改app版本信息")
    @PutMapping("updateById")
    @AuthToken
    @Log(title = "修改app版本信息", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public BaseResponse updateById(@RequestBody @Validated AppVersionReqDto appVersionReqDto) {
        if (StringUtils.isEmpty(appVersionReqDto.getId())){
            return BaseResponse.error("版本编号不能为空");
        }
        EzAppVersion ezAppVersion = new EzAppVersion();
        BeanUtils.copyBeanProp(ezAppVersion,appVersionReqDto);
        ezAppVersionService.updateById(ezAppVersion);
        return BaseResponse.success();
    }



    @ApiOperation(value = "删除app版本信息")
    @DeleteMapping("deleteById/{id}")
    @AuthToken
    @Log(title = "删除app版本信息", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public BaseResponse deleteById(@PathVariable String id) {
        EzAppVersion ezcoinsAppVersion = ezAppVersionService.getById(id);
        if (ezcoinsAppVersion.getIsRacking().equals(AppUpdateType.ONSHELF.getCode()) || "0".equals(ezcoinsAppVersion.getIsDefault())){
            return BaseResponse.error("请先修改该App版本状态");
        }
        ezAppVersionService.removeById(id);
        return BaseResponse.success();
    }

}