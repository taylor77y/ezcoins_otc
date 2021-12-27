package com.ezcoins.project.otc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.OtcConfig;
import com.ezcoins.project.otc.entity.OtcFiatCurrency;
import com.ezcoins.project.otc.entity.OtcMerchant;
import com.ezcoins.project.otc.entity.OtcOrder;
import com.ezcoins.project.otc.entity.req.OtcConfigReqDto;
import com.ezcoins.project.otc.entity.req.OtcTransactionReqDto;
import com.ezcoins.project.otc.entity.req.UserInternetAccountReqDto;
import com.ezcoins.project.otc.entity.resp.InternetAccountRespDto;
import com.ezcoins.project.otc.entity.resp.OtcTransactionRespDto;
import com.ezcoins.project.otc.service.*;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "APP-OTC模块 网络账号")
@RequestMapping("/otc/app/internetaccount")
public class OtcInternetAccountController  extends BaseController {

    @Autowired
    private EzUserService ezUserService;

    @Autowired
    private EzInternetAccountService ezInternetAccountService;

    @Autowired
    private OtcTransactionOrderService otcTransactionOrderService;

    @Autowired
    private OtcOrderService otcOrderService;

    @Autowired
    private OtcConfigService otcConfigService;

    @Autowired
    private OtcFiatCurrencyService otcFiatCurrencyService;

    @Autowired
    private OtcMerchantService otcMerchantService;

    @ApiOperation(value = "网络账号列表")
    @AuthToken
    @GetMapping("internetAccountList")
    public ResponseList<InternetAccountRespDto> internetAccountList(){
        return ResponseList.success(ezInternetAccountService.internetAccountList(getUserId()));
    }


    @ApiOperation(value = "添加/修改 网络账号信息")
    @AuthToken
    @PostMapping("addOrUpdateInternetAccount")
    @Log(title = "添加/修改 用户网络账号信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response addOrUpdateInternetAccount(@RequestBody @Valid UserInternetAccountReqDto internetAccountReqDto){
        return ezInternetAccountService.addOrUpdateInternetAccount(internetAccountReqDto);
    }

    @ApiOperation(value = "修改 网络账号状态")
    @AuthToken
    @PostMapping("updateUserInternetAccountStatus")
    @Log(title = "修改 网络账号状态", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response updateUserInternetAccountStatus(@RequestBody @Valid UserInternetAccountReqDto internetAccountReqDto){
        return ezInternetAccountService.updateUserInternetAccountStatus(internetAccountReqDto);
    }

    // TODO: 2021/12/21
//    @ApiOperation(value = "放币")
//    @AuthToken
//    @GetMapping("userOrderPaymentMoney")
//    public ResponseList<OtcTransactionOrderRespDto> userOrderPaymentMoney(){
//        return ResponseList.success(otcTransactionOrderService.otcTransactionOrderList());
//    }


    @ApiOperation(value = "新增 次级菜单-OTC 配置")//sellUserId   buyUserId
    @AuthToken
    @PostMapping("addOrUpdateOtcConfig")
    @Log(title = "添加/修改 网络账号信息状态", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response<OtcConfig> addOrUpdateOtcConfig(@RequestBody @Valid OtcConfigReqDto otcConfigReqDto){
        return otcConfigService.addOrUpdateOtcConfig(otcConfigReqDto);
    }

    @ApiOperation(value = "法币币种（抓取系统参数配置的法币）")
    @AuthToken
    @GetMapping("fiatList")
    public ResponseList<OtcFiatCurrency> fiatList(){
        return ResponseList.success(otcFiatCurrencyService.fiatListFromBinance());
    }

    @ApiOperation(value = "次级菜单-OTC订单列表 页面展示")
    @AuthToken
    @GetMapping("otcTransactionOrderList")
    public ResponseList<OtcTransactionRespDto> otcTransactionOrderList(@RequestBody OtcTransactionReqDto otcTransactionReqDto){
        return otcTransactionOrderService.otcTransactionOrderList(otcTransactionReqDto);
    }


    @ApiOperation(value = "次级菜单-根据交易 id 查询 订单详情")
    @AuthToken
    @GetMapping("findOrderDetailsByTxid")
    public Response<OtcOrder> findOrderDetailsByTxid(@PathVariable String txid){
//        return otcTransactionOrderService.findOrderDetailsByTxid(txid);

        LambdaQueryWrapper<OtcOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcOrder::getTransactionId, txid);
        return Response.success(otcOrderService.getOne(lambdaQueryWrapper));
    }

    @ApiOperation(value = "用户名（可点击跳转至用户详情）")
    @AuthToken
    @GetMapping("findUserDetailsByUserName")
    public Response<EzUser> findUserDetailsByUserName(@PathVariable String userName){
        return Response.success(ezUserService.selectUserBy(userName,"","","",""));
    }


    // TODO: 2021/12/22
    //根据匹配订单id查询聊天记录
//    @ApiOperation(value = "对话内容（点击弹出双方于订单内的留言）")
//    @AuthToken
//    @GetMapping("userOrderPaymentMoney")
//    public ResponseList<OtcTransactionOrderRespDto> userOrderPaymentMoney(){
//        return ResponseList.success(otcTransactionOrderService.otcTransactionOrderList());
//    }


    @ApiOperation(value = "次级菜单-商家列表")
    @AuthToken
    @GetMapping("otcMerchantList")
    public ResponseList<OtcMerchant> otcMerchantList(){
        LambdaQueryWrapper<OtcMerchant> otcQueryWrapper = new LambdaQueryWrapper<>();
        return ResponseList.success(otcMerchantService.list(otcQueryWrapper));
    }
}
