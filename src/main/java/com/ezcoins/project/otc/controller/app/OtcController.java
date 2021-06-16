package com.ezcoins.project.otc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.otc.PaymentMethod;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzUserAlipay;
import com.ezcoins.project.otc.entity.EzUserBank;
import com.ezcoins.project.otc.entity.EzUserWechat;
import com.ezcoins.project.otc.entity.req.AlipayWechatReqDto;
import com.ezcoins.project.otc.entity.req.BankReqDto;
import com.ezcoins.project.otc.entity.req.DpMethodReqDto;
import com.ezcoins.project.otc.entity.resp.PaymentMethodRespDto;
import com.ezcoins.project.otc.service.EzUserAlipayService;
import com.ezcoins.project.otc.service.EzUserBankService;
import com.ezcoins.project.otc.service.EzUserWechatService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/15 16:17
 * @Version:1.0
 */
@RestController
@Api(tags = "APP-OTC模块")
@RequestMapping("/otc/app")
public class OtcController {
    @Autowired
    private EzUserAlipayService alipayService;

    @Autowired
    private EzUserBankService bankService;

    @Autowired
    private EzUserWechatService wechatService;

    @AuthToken
    @ApiOperation(value = "收款方式 列表")
    @GetMapping("paymentMethodList")
    public Response paymentMethodList(){
        String userId = ContextHandler.getUserId();
        LambdaQueryWrapper<EzUserAlipay> alipayQueryWrapper=new LambdaQueryWrapper<>();
        alipayQueryWrapper.eq(EzUserAlipay::getUserId,userId);
        EzUserAlipay userAlipay = alipayService.getOne(alipayQueryWrapper);

        LambdaQueryWrapper<EzUserWechat> wechatQueryWrapper=new LambdaQueryWrapper<>();
        wechatQueryWrapper.eq(EzUserWechat::getUserId,userId);
        EzUserWechat userWechat = wechatService.getOne(wechatQueryWrapper);

        LambdaQueryWrapper<EzUserBank> bankQueryWrapper=new LambdaQueryWrapper<>();
        bankQueryWrapper.eq(EzUserBank::getUserId,userId);
        EzUserBank userBank = bankService.getOne(bankQueryWrapper);

        PaymentMethodRespDto paymentMethodRespDto = new PaymentMethodRespDto();
        paymentMethodRespDto.setEzUserAlipay(userAlipay);
        paymentMethodRespDto.setEzUserWechat(userWechat);
        paymentMethodRespDto.setEzUserBank(userBank);
        return Response.success(paymentMethodRespDto);
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "添加/修改 支付宝/微信 收款方式")
    @PostMapping("alipayWechatPaymentMethod")
    @Log(title = "添加 支付宝/微信 收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse alipayWechatPaymentMethod(@RequestBody AlipayWechatReqDto alipayWechatReqDto){
        alipayService.alipayPaymentMethod(alipayWechatReqDto);
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "添加/修改 银行收款方式")
    @PostMapping("bankPaymentMethod")
    @Log(title = "添加/修改 银行收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse bankPaymentMethod(@RequestBody BankReqDto bankReqDto){
        bankService.bankPaymentMethod(bankReqDto);
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "删除 收款方式")
    @PostMapping("deletePaymentMethod")
    @Log(title = "删除 收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public BaseResponse deletePaymentMethod(@RequestBody DpMethodReqDto dpMethodReqDto){
        if (dpMethodReqDto.getPaymentMethod().equals(PaymentMethod.ALIPAY.getCode())){
            alipayService.removeById(dpMethodReqDto.getId());
        }else if (dpMethodReqDto.getPaymentMethod().equals(PaymentMethod.WECHAT.getCode())){
            wechatService.removeById(dpMethodReqDto.getId());
        }else if (dpMethodReqDto.getPaymentMethod().equals(PaymentMethod.BANK.getCode())){
            bankService.removeById(dpMethodReqDto.getId());
        }
        return BaseResponse.success();
    }










































}
