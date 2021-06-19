package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.coin.entity.RechargeConfig;
import com.ezcoins.project.coin.entity.WithdrawConfig;
import com.ezcoins.project.coin.entity.req.WithdrewConfigReqDto;
import com.ezcoins.project.coin.service.RechargeConfigService;
import com.ezcoins.project.coin.service.WithdrawConfigService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.config.entity.EzSmsConfig;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.project.otc.entity.req.PaymentMethodReqDto;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 提现配置表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@RestController
@Api(tags = "Admin-资产配置模块")
@RequestMapping("/admin/coin/coinConfig")
public class CoinConfigController {
    @Autowired
    private WithdrawConfigService withdrawConfigService;

    @Autowired
    private RechargeConfigService rechargeConfigService;


    @ApiOperation(value = "提币配置列表")
    @PostMapping("/withdrawConfigList")
    @AuthToken
    public ResponsePageList<WithdrawConfig> withdrawConfigList(@RequestBody SearchModel<WithdrawConfig> searchModel){
        return ResponsePageList.success(withdrawConfigService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "冲币配置列表")
    @PostMapping("/rechargeConfigList")
    @AuthToken
    public ResponsePageList<RechargeConfig> rechargeConfigList(@RequestBody SearchModel<RechargeConfig> searchModel){
        return ResponsePageList.success(rechargeConfigService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @NoRepeatSubmit
    @ApiOperation(value = "添加/修改  提币配置")
    @PostMapping("addOrUpdate")
    @AuthToken
    @Log(title = "添加/修改  提币配置", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse addOrUpdate(@RequestBody WithdrewConfigReqDto withdrewConfigReqDto) {
        withdrawConfigService.addOrUpdate(withdrewConfigReqDto);
        return BaseResponse.success();
    }






}

