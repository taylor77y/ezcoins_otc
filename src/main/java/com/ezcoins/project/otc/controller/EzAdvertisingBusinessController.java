package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.req.BusinessReqDto;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.EncoderUtil;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@RestController
@Api(tags = "Admin-OTC广告商户模块")
@RequestMapping("/admin/otc/ezAdvertisingBusiness")
public class EzAdvertisingBusinessController {
    @Autowired
    private EzAdvertisingBusinessService advertisingBusinessService;

    @ApiOperation(value = "OTC广告商户  列表")
    @PostMapping("advertisingBusinessList")
    @AuthToken
    public ResponsePageList<EzAdvertisingBusiness> advertisingBusinessList(@RequestBody SearchModel<EzAdvertisingBusiness> searchModel) {
        return ResponsePageList.success(advertisingBusinessService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "修改商户信息")
    @PostMapping("updateAdvertisingBusiness")
    @AuthToken
    @Log(title = "OTC模块", logInfo ="修改商户信息", operatorType = OperatorType.MANAGE)
    public Response updateAdvertisingBusiness(@RequestBody BusinessReqDto businessReqDto){
        EzAdvertisingBusiness advertisingBusiness = new EzAdvertisingBusiness();
        BeanUtils.copyBeanProp(advertisingBusiness, businessReqDto);
        if (StringUtils.isNotEmpty(businessReqDto.getSecurityPassword())){
            advertisingBusiness.setSecurityPassword(EncoderUtil.encode(businessReqDto.getSecurityPassword()));
        }
        advertisingBusiness.setUpdateBy(ContextHandler.getUserName());
        advertisingBusinessService.updateById(advertisingBusiness);
        return Response.success();

    }
}

