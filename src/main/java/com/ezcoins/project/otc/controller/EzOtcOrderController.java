package com.ezcoins.project.otc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.Account;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.coin.service.TypeService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.entity.req.OtcOrderReqDto;
import com.ezcoins.project.otc.entity.req.PaymentQrcodeTypeReqDto;
import com.ezcoins.project.otc.entity.resp.AdOrderRespDto;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.otc.service.EzOtcOrderService;
import com.ezcoins.project.otc.service.EzPaymentInfoService;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@RestController
@Api(tags = "Admin-OTC订单模块")
@RequestMapping("/admin/otc/ezOtcOrder")
public class EzOtcOrderController {

    @Autowired
    private EzOtcOrderService otcOrderService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private EzCountryConfigService countryConfigService;

    @Autowired
    private EzPaymentInfoService paymentInfoService;

    @Autowired
    private AccountService accountService;

    @AuthToken
    @ApiOperation(value = "OTC 订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<EzOtcOrder> otcOrderList(@RequestBody SearchModel<EzOtcOrder> searchModel){
        return ResponsePageList.success(otcOrderService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @AuthToken
    @ApiOperation(value = "后台根据用户id发布订单")
    @PostMapping("releaseOrder")
    public Response releaseOrder(@RequestBody OtcOrderReqDto otcOrderReqDto){
        if (StringUtils.isEmpty(otcOrderReqDto.getUserId())){
            return Response.error("用户id不能为空");
        }
        return otcOrderService.releaseAdvertisingOrder(otcOrderReqDto);
    }


    @AuthToken
    @ApiOperation(value = "发布订单详情")
    @GetMapping("releaseOrderInfo/{userId}")
    public Response<AdOrderRespDto> releaseOrder(@PathVariable String userId){
        AdOrderRespDto adOrderRespDto=new AdOrderRespDto();
        LambdaQueryWrapper<Type> typeLambdaQueryWrapper=new LambdaQueryWrapper<>();
        typeLambdaQueryWrapper.select(Type::getCoinName);
        adOrderRespDto.setCoinNameList(typeService.listObjs(typeLambdaQueryWrapper).stream().map(o ->  (String) o).collect(Collectors.toList()));

        LambdaQueryWrapper<EzCountryConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(EzCountryConfig::getCurrencyCode);
        lambdaQueryWrapper.isNotNull(EzCountryConfig::getCurrencyCode);
        adOrderRespDto.setCurrencyCodeList(countryConfigService.listObjs(lambdaQueryWrapper).stream().map(o ->  (String) o).collect(Collectors.toList()));

        LambdaQueryWrapper<EzPaymentInfo> alipayQueryWrapper = new LambdaQueryWrapper<>();
        alipayQueryWrapper.eq(EzPaymentInfo::getUserId, userId);
        adOrderRespDto.setPaymentInfos(paymentInfoService.list(alipayQueryWrapper));

        LambdaQueryWrapper<Account> accountLambdaQueryWrapper=new LambdaQueryWrapper<>();
        accountLambdaQueryWrapper.eq(Account::getUserId, userId);
        adOrderRespDto.setAccounts(accountService.list(accountLambdaQueryWrapper));
        return Response.success(adOrderRespDto);
    }

}

