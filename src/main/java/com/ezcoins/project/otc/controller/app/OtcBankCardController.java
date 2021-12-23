package com.ezcoins.project.otc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzOtcCoinExchange;
import com.ezcoins.project.otc.entity.EzOtcCoinInfo;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.entity.req.UserBankCardAddrReqDto;
import com.ezcoins.project.otc.entity.resp.CoinConfigRespDto;
import com.ezcoins.project.otc.entity.resp.MerchantsBussinessStatsRespDto;
import com.ezcoins.project.otc.entity.resp.PaymentBankRespDto;
import com.ezcoins.project.otc.service.*;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;

@Slf4j
@RestController
@Api(tags = "APP-OTC 用户银行卡模块")
@RequestMapping("/otc/app/bankcard")
public class OtcBankCardController extends BaseController {

    @Autowired
    private EzPaymentBankService ezPaymentBankService;

    @Autowired
    private EzOtcCoinConfigService ezOtcCoinConfigService;


    @Autowired
    private EzOtcCoinInfoService ezOtcCoinInfoService;

    @Autowired
    private EzOtcCoinExchangeService ezOtcCoinExchangeService;

    @Autowired
    private EzUserService userService;

    @Autowired
    private EzOtcOrderMatchService orderMatchService;

    @Autowired
    private EzUserKycService kycService;

    @Autowired
    private EzAdvertisingBusinessService advertisingBusinessService;


    @ApiOperation(value = "用户银行卡列表")
    @AuthToken
    @GetMapping("userBankCardList")
    public ResponseList<PaymentBankRespDto> userBankCardList(){
        return ResponseList.success(ezPaymentBankService.userBankCardList(getUserId()));
    }


    @ApiOperation(value = "添加/修改 用户银行卡信息")
    @AuthToken
    @PostMapping("addOrUpdateUserBankCard")
    @Log(title = "添加/修改 用户银行卡信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response addOrUpdateUserBankCard(@RequestBody UserBankCardAddrReqDto bankCardReqDto){
        return ezPaymentBankService.addOrUpdateUserBankCard(bankCardReqDto);
    }


    @ApiOperation(value = "修改用户 银行卡 状态")
    @AuthToken
    @PostMapping("updateUserBankCardStatus")
    @Log(title = "修改用户 银行卡 状态", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response updateUserBankCardStatus(@RequestBody UserBankCardAddrReqDto bankCardReqDto){
        return ezPaymentBankService.updateUserBankCardStatus(bankCardReqDto);
    }

    @ApiOperation(value = "查询所有可用 coin 信息")
    @AuthToken
    @GetMapping("queryAllCoins")// 交易类型：在线购买、卖出
    public ResponseList<EzOtcCoinInfo> queryAllCoins(){

        return ResponseList.success(ezOtcCoinInfoService.queryAllCoins());
    }

    /*
        *一般而言，我们把火币、云币等交易所称为场内交易平台，此类平台不仅帮助存放我们的人民币和数字货币，还提供买卖交易的场所。
        *而在场外交易平台上，卖家只将自己的数字资产(一般是比特币)暂存在平台上，
        *通过挂单撮合的方式进行交易，最大的不同便是比特币的买卖方式和过程由双方自行商定并进行，无需通过平台。
     */
    @ApiOperation(value = "查询所有 coin 挂单配置信息")
    @AuthToken
    @GetMapping("getAllOTCTransctionConfig/{transactionType}")// 交易类型：在线购买、卖出
    public ResponseList<CoinConfigRespDto> getAllOTCTransctionConfig(@PathVariable String transactionType,@PathVariable(value = "userId", required = false) String userId){
//        LambdaQueryWrapper<EzOtcCoinConfig> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(EzOtcCoinConfig::getTransactionType,transactionType);
        LambdaQueryWrapper<EzUserKyc> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUserKyc::getUserId,ContextHandler.getUserId());
        EzUserKyc one = kycService.getOne(lambdaQueryWrapper);
        if (one==null){
            return ResponseList.error(MessageUtils.message("请先完成实名认证"), Collections.EMPTY_LIST);
        }
        return ResponseList.success(ezOtcCoinConfigService.getAllOTCTransctionConfig(transactionType));
    }

    @ApiOperation(value = "通过币种id和法币名称获取法币兑换值")
    @AuthToken
    @GetMapping("queryByCoinIdAndAbbreviation/{coinId}/{abbreviation}")
    public Response<EzOtcCoinExchange> queryByCoinIdAndAbbreviation(@PathVariable String abbreviation, @PathVariable String coinId){
        return Response.success(ezOtcCoinExchangeService.queryByCoinIdAndAbbreviation(abbreviation, coinId));
    }

    @ApiOperation(value = "OTC商家交易汇总信息")
    @PostMapping({"advertisingBusiness/{userId}", "advertisingBusiness"})
    @AuthToken
    public Response<MerchantsBussinessStatsRespDto> getMonthlyMerchantsStatistics(@PathVariable(value = "userId", required = false) String userId) {
        String userId2 = ContextHandler.getUserId();
        String userId1 = StringUtils.isNotEmpty(userId) ? userId : userId2;
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId1);
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(lambdaQueryWrapper);
        if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(one.getSecurityPassword())) {
            return Response.error(MessageUtils.message("请先完善otc交易信息"),700);
        }
        EzUser user = userService.getById(userId1);
        String kycStatus = user.getKycStatus();

        String level = user.getLevel();
        MerchantsBussinessStatsRespDto merchantsBussinessStatsRespDto = new MerchantsBussinessStatsRespDto();
        BeanUtils.copyBeanProp(merchantsBussinessStatsRespDto, one);
        merchantsBussinessStatsRespDto.setKycStatus(kycStatus);
        merchantsBussinessStatsRespDto.setAdvertisingStatus(level);
        merchantsBussinessStatsRespDto.setRegistrationTime(one.getCreateTime());
        merchantsBussinessStatsRespDto.setMargin(one.getMargin());
        //月成功成交笔数（以三十日为基准计算）
        LambdaQueryWrapper<EzOtcOrderMatch> q = new LambdaQueryWrapper<EzOtcOrderMatch>();
        q.eq(EzOtcOrderMatch::getOtcOrderUserId, userId1);
        q.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED);
        Date ndayStart = DateUtils.getNdayStart(-30);
        q.gt(EzOtcOrderMatch::getFinishTime, ndayStart);
        merchantsBussinessStatsRespDto.setMonthlySuccessCount(orderMatchService.count(q));


        //月发起笔数（以三十日为基准计算）
        q = new LambdaQueryWrapper<EzOtcOrderMatch>();
        q.eq(EzOtcOrderMatch::getOtcOrderUserId, userId1);
        ndayStart = DateUtils.getNdayStart(-30);
        q.gt(EzOtcOrderMatch::getFinishTime, ndayStart);
        merchantsBussinessStatsRespDto.setMonthlySuccessPercent(merchantsBussinessStatsRespDto.getMonthlySuccessCount()/orderMatchService.count(q));


        //总成交笔数
        q = new LambdaQueryWrapper<EzOtcOrderMatch>();
        q.eq(EzOtcOrderMatch::getOtcOrderUserId, userId1);
        q.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED);
        merchantsBussinessStatsRespDto.setGrandTotal(orderMatchService.count(q));

        //总买入资产（计算买入的数字货币）
        //由于 LambdaQueryWrapper 并不支持使用sum等求和的聚合函数
        EzOtcOrderMatch ezOtcOrderMatch = new EzOtcOrderMatch();
        QueryWrapper<EzOtcOrderMatch> qw = new QueryWrapper<EzOtcOrderMatch>();
        qw.select("IFNULL(sum(price),0) as totalPrice").eq("type", "0")
                .eq("otc_order_user_id", userId1).eq("status", MatchOrderStatus.COMPLETED);
        ezOtcOrderMatch = orderMatchService.getOne(qw);
        merchantsBussinessStatsRespDto.setTotalpurchasedAssets(ezOtcOrderMatch.getTotalPrice());


        //总卖出资产（计算卖出的数字货币）
        ezOtcOrderMatch = new EzOtcOrderMatch();
        qw = new QueryWrapper<EzOtcOrderMatch>();
        qw.select("IFNULL(sum(price),0) as totalPrice").eq("type", "1")
                .eq("otc_order_user_id", userId1).eq("status", MatchOrderStatus.COMPLETED);
        ezOtcOrderMatch = orderMatchService.getOne(qw);
        merchantsBussinessStatsRespDto.setTotalSoldAssets(ezOtcOrderMatch.getTotalPrice());


        return Response.success(merchantsBussinessStatsRespDto);
    }
}
