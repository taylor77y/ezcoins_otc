package com.ezcoins.project.consumer.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.req.CheckKycReqDto;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-05-27
 */
@RestController
@Api(tags = "Admin-用户kyc认证模块")
@RequestMapping("/admin/consumer/userKyc")
public class EzUserKycController {
    @Autowired
    private EzUserKycService kycService;

    @ApiOperation(value = "实名认证列表")
    @PostMapping("kycList")
    @AuthToken
    public ResponsePageList<EzUserKyc> kycList(@RequestBody SearchModel<EzUserKyc> searchModel) {
        return  ResponsePageList.success(kycService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "根据Id查询实名认证详情")
    @GetMapping("getKycById/{id}")
    @AuthToken
    public Response<EzUserKyc> getKycById(@PathVariable String id) {
       return Response.success(kycService.getById(id));
    }


    @ApiOperation(value = "审核实名认证")
    @PutMapping("checkKyc")
    @AuthToken
    @Log(title = "审核实名认证", businessType = BusinessType.GRANT, operatorType = OperatorType.MANAGE)
    public BaseResponse checkKyc(@RequestBody @Validated CheckKycReqDto kycReqDto) {
        kycService.checkKyc(kycReqDto);
        return BaseResponse.success();
    }


}

