package com.ezcoins.project.config.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.entity.EzSmsConfig;
import com.ezcoins.project.config.entity.req.CountryReqDto;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.MessageUtils;
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
 * @since 2021-06-11
 */
@RestController
@Api(tags = "Admin-国家编号管理模块")
@RequestMapping("/admin/config/countryConfig")
public class EzCountryConfigController {
    @Autowired
    private EzCountryConfigService countryConfigService;

    @ApiOperation(value = "国家编号配置列表")
    @PostMapping("/countryConfigs")
    @AuthToken
    public ResponsePageList<EzCountryConfig> countryConfigs(@RequestBody SearchModel<EzCountryConfig> searchModel){
        return ResponsePageList.success(countryConfigService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "添加国家编号配置")
    @PostMapping("/addCountryConfigs")
    @AuthToken
    @Log(title = "配置模块", logInfo ="添加国家编号配置", operatorType = OperatorType.MANAGE)
    public Response addCountryConfigs(@RequestBody CountryReqDto countryReqDto){
        //判断配置是否存在
        LambdaQueryWrapper<EzCountryConfig> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzCountryConfig::getCountryName,countryReqDto.getCountryName())
                .eq(EzCountryConfig::getCountryNameEn,countryReqDto.getCountryNameEn())
                .eq(EzCountryConfig::getCountryCode,countryReqDto.getCountryCode())
                .eq(EzCountryConfig::getCountryTelCode,countryReqDto.getCountryTelCode());

        EzCountryConfig countryConfig = countryConfigService.getOne(queryWrapper,true);
        if (countryConfig!=null){
            return Response.error(MessageUtils.message("内容重复"));
        }
        EzCountryConfig ezCountryConfig = new EzCountryConfig();
        BeanUtils.copyBeanProp(ezCountryConfig,countryReqDto);
        countryConfigService.save(ezCountryConfig);
        return Response.success();
    }

    @ApiOperation(value = "批量删除国家编号配置")
    @DeleteMapping("/removeCountryConfigs")
    @AuthToken
    @Log(title = "配置模块", logInfo ="批量删除国家编号配置", operatorType = OperatorType.MANAGE)
    public Response removeCountryConfigs(@RequestBody List<String> idList){
        countryConfigService.removeByIds(idList);
        return Response.success();
    }


}

