package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.project.otc.entity.req.BusinessReqDto;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.project.otc.service.EzPaymentMethodService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
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
    @Log(title = "修改商户信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse updateAdvertisingBusiness(@RequestBody BusinessReqDto businessReqDto){
        if (StringUtils.isNotEmpty(businessReqDto.getAdvertisingName()) && !advertisingBusinessService.isUpdateBy(businessReqDto.getId())) {
            return BaseResponse.error("商户名不能再进行修改了");
        }
        EzAdvertisingBusiness advertisingBusiness = new EzAdvertisingBusiness();
        BeanUtils.copyProperties(advertisingBusiness,businessReqDto);
        advertisingBusiness.setUpdateBy(ContextHandler.getUserName());
        advertisingBusinessService.updateById(advertisingBusiness);
        return BaseResponse.success();

    }
}

