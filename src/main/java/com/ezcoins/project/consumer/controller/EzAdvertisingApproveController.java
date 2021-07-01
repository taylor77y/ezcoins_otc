package com.ezcoins.project.consumer.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzAdvertisingApprove;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.req.CheckAdvertisingReqDto;
import com.ezcoins.project.consumer.entity.req.CheckKycReqDto;
import com.ezcoins.project.consumer.service.EzAdvertisingApproveService;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 高级认证表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-30
 */
@RestController
@Api(tags = "Admin-高级认证")
@RequestMapping("/admin/consumer/ezAdvertisingApprove")
public class EzAdvertisingApproveController {
    @Autowired
    private EzAdvertisingApproveService advertisingApproveService;


    @ApiOperation(value = "高级认证列表")
    @PostMapping("approveList")
    @AuthToken
    public ResponsePageList<EzAdvertisingApprove> approveList(@RequestBody SearchModel<EzAdvertisingApprove> searchModel) {
        return  ResponsePageList.success(advertisingApproveService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "审核高级认证")
    @PutMapping("checkAdvertising")
    @AuthToken
    @Log(title = "审核实名认证", businessType = BusinessType.GRANT, operatorType = OperatorType.MANAGE)
    public BaseResponse checkAdvertising(@RequestBody @Validated CheckAdvertisingReqDto checkAdvertisingReqDto) {
        advertisingApproveService.checkAdvertising(checkAdvertisingReqDto);
        return BaseResponse.success();
    }


}

