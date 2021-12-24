package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.CoinConstant;
import com.ezcoins.constant.RecordSonType;
import com.ezcoins.constant.SysOrderConstants;
import com.ezcoins.constant.SystemOrderTips;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.interf.IndexOrderNoKey;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.manager.AsyncManager;
import com.ezcoins.manager.factory.AsyncFactory;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.coin.service.TypeService;
import com.ezcoins.project.common.mq.service.ConvertAndSendService;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.entity.req.*;
import com.ezcoins.project.otc.entity.resp.NewOrderRespDto;
import com.ezcoins.project.otc.entity.resp.OrderInfo;
import com.ezcoins.project.otc.entity.resp.OtcOrderRespDto;
import com.ezcoins.project.otc.entity.resp.PaymentDetails;
import com.ezcoins.project.otc.mapper.EzOtcOrderMapper;
import com.ezcoins.project.otc.service.*;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import com.ezcoins.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Service
public class EzOtcOrderServiceImpl extends ServiceImpl<EzOtcOrderMapper, EzOtcOrder> implements EzOtcOrderService {

    @Autowired
    private EzUserService userService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private EzCountryConfigService countryConfigService;

    @Autowired
    private EzOtcOrderIndexService orderIndexService;

    @Autowired
    private EzOtcOrderMatchService orderMatchService;

    @Autowired
    private EzOtcConfigService otcConfigService;

    @Autowired
    private ConvertAndSendService convertAndSend;

    @Autowired
    private EzOtcOrderPaymentService paymentService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private EzOtcChatMsgService otcChatMsgService;

    @Autowired
    private EzAdvertisingBusinessService advertisingBusinessService;

    @Autowired
    private AccountService accountService;

    @Override
    @Transactional(value="transactionManager2", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto) {
        String coinName = otcOrderReqDto.getCoinName();
        //查看otc信息是否有过修改
        String userId = otcOrderReqDto.getUserId();
        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId);
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
        if (StringUtils.isEmpty(one.getSecurityPassword())) {
            return Response.error(MessageUtils.message("请先完善otc交易信息")).code(700);
        }
        LambdaQueryWrapper<Type> queryWrapper = new LambdaQueryWrapper<Type>();
        queryWrapper.eq(Type::getCoinName, coinName);
        Type coinType = typeService.getOne(queryWrapper);//查询到币种
        if (!typeService.statusService(coinType, CoinConstant.OTC_STATUS)) {
            throw new BaseException("此币种尚未开放交易");
        }
        BigDecimal amount = otcOrderReqDto.getTotalAmount();//发布数量
        BigDecimal minimumLimit = otcOrderReqDto.getMinimumLimit();
        BigDecimal maximumLimit = otcOrderReqDto.getMaximumLimit();
        if (maximumLimit.compareTo(coinType.getMaxAmount()) > 0 || minimumLimit.compareTo(coinType.getMinAmount()) < 0) {
            throw new BaseException("发布数量不在限额");
        }
        EzOtcConfig otcConfig = otcConfigService.getById(1);//otc配置
        Integer prompt = otcOrderReqDto.getPrompt();
        if (prompt > otcConfig.getMaxPayTime() || prompt < otcConfig.getMinPayTime()) {
            throw new BaseException("付款时间不在限定时间内");
        }

        BigDecimal advertisingFeeRatio = coinType.getOtcFeeRatio();
        BigDecimal fee = advertisingFeeRatio.multiply(amount);//比例手续费

        //扣除手续费
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(coinName);
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setMainType(CoinConstants.MainType.FREE.getType());
        b.setSonType(RecordSonType.HANDLING_FEE);
        b.setAvailable(fee.negate());
        cList.add(b);

        //判断订单 买卖
        EzOtcOrder ezOtcOrder = new EzOtcOrder();
        String orderNo = orderIndexService.getOrderNoByCurrencyCode(otcOrderReqDto.getCurrencyCode(), IndexOrderNoKey.ORDER_INFO);
        ezOtcOrder.setOrderNo(orderNo);
        ezOtcOrder.setUserId(userId);
        if ("0".equals(otcOrderReqDto.getType())) {//买
            ezOtcOrder.setFrozeAmount(BigDecimal.ZERO);
        } else if ("1".equals(otcOrderReqDto.getType())) {//卖
            ezOtcOrder.setFrozeAmount(amount);
            BalanceChange b2 = new BalanceChange();
            b2.setCoinName(coinName);
            b2.setUserId(userId);
            b2.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b2.setMainType(CoinConstants.MainType.FROZEN.getType());
            b2.setSonType(RecordSonType.TRANSACTION_FREEZE);
            b2.setFrozen(amount);
            b2.setAvailable(amount.negate());
            cList.add(b2);
        }
        //将支付方式存入数据订单对应的支付详情表
        paymentService.depositPayment(otcOrderReqDto.getPaymentMethod1(), otcOrderReqDto.getPaymentMethod2(), otcOrderReqDto.getPaymentMethod3(), userId, orderNo, null);

        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        BeanUtils.copyBeanProp(ezOtcOrder, otcOrderReqDto);
        ezOtcOrder.setAdvertisingName(one.getAdvertisingName());
        //存入新的订单
        baseMapper.insert(ezOtcOrder);
        //给用户一个新订单信号
        WebSocketHandle.nowOrder();
        return Response.success();
    }

    @Override
    public Response cancelAdvertisingOrder(String adId) {
        String userId = ContextHandler.getUserId();
        /*ArrayList<NewOrderRespDto> newOrderRespDtos = new ArrayList<>();
        IPage<EzOtcOrder> page = new Page<EzOtcOrder>(pageQuery.getPage(), pageQuery.getLimit());
        LambdaQueryWrapper<EzOtcOrder> otcOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getStatus, "0");//订单状态（0：正常 1：已下架）
        otcOrderLambdaQueryWrapper.orderByDesc(EzOtcOrder::getCreateTime);
        baseMapper.selectPage(page, otcOrderLambdaQueryWrapper).getRecords().forEach(e -> {
            if ( StringUtils.isEmpty(userId) || !e.getUserId().equals(userId) ) {
                NewOrderRespDto newOrderRespDto = new NewOrderRespDto();
                BeanUtils.copyBeanProp(newOrderRespDto, e);
                newOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));
                if (newOrderRespDto.getLastAmount().compareTo(e.getMinimumLimit()) >= 0) {
                    newOrderRespDtos.add(newOrderRespDto);
                }
            }
        });*/
        return Response.success();
    }

    /**
     * @param placeOrderReqDto
     * @Description: 下单
     * @Param: [placeOrderReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/18
     */
    @Override
    @Transactional(value="transactionManager1", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response<PaymentDetails> placeAnOrder(PlaceOrderReqDto placeOrderReqDto) {
        String userId = ContextHandler.getUserId();
        //通过订单号查询到购买的订单
        String orderNo = placeOrderReqDto.getOrderNo();
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        PaymentDetails details = new PaymentDetails();

        //查看用户是否有未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = Wrappers.<EzOtcOrderMatch>lambdaQuery().
                eq(EzOtcOrderMatch::getUserId, userId).eq(EzOtcOrderMatch::getType, ezOtcOrder.getType())
                .and(wq -> wq.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode())
                        .or().eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode())
                        .or().eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode())
                        .or().eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.APPEALING.getCode()));
        EzOtcOrderMatch orderMatch = orderMatchService.getOne(matchLambdaQueryWrapper);//匹配订单是否有未完成
        if (null != orderMatch) {
            BeanUtils.copyBeanProp(details, orderMatch);
            details.setNowTime(DateUtils.getNowDate());
            LambdaQueryWrapper<EzOtcOrderPayment> paymentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //判断订单类型
            if ("0".equals(orderMatch.getType())) {//买单
                paymentLambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderMatchNo, orderMatch.getOrderMatchNo());
                details.setEzOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            } else {//卖单
                paymentLambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderNo, orderMatch.getOrderNo());
                details.setEzOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            }
            return Response.success(MessageUtils.message("请先完成当前未完成的订单"), details);//将订单返回
        }
        if ("1".equals(ezOtcOrder.getStatus())) {
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        LambdaQueryWrapper<Type> typeLambdaQueryWrapper = new LambdaQueryWrapper<Type>();
        typeLambdaQueryWrapper.eq(Type::getCoinName, ezOtcOrder.getCoinName());
        Type coinType = typeService.getOne(typeLambdaQueryWrapper);//查询到币种
        if (!typeService.statusService(coinType, CoinConstant.OTC_STATUS)) {
            return Response.error(MessageUtils.message("当前币种OTC交易尚未开"));
        }
        //获取otc基本配置
        EzOtcConfig otcConfig = otcConfigService.getById(1);
        otcConfigService.checkOtcStatus(userId,otcConfig.getMaxCancelNum());

        //判断用户注册的国籍是否满足购买条件
        String currencyCode = ezOtcOrder.getCurrencyCode();
        EzUser user = userService.getById(userId);
        String countryCode = user.getCountryCode();
        //通过国家编号查询到国家
        LambdaQueryWrapper<EzCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(EzCountryConfig::getCountryCode, countryCode);
        EzCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        if (!one.getCurrencyCode().equals(currencyCode)) {
            return Response.error(MessageUtils.message("根据您注册所在地的相关规定，您只能交易本地区的法币"), 701);//将订单返回
        }
        BigDecimal maximumLimit = ezOtcOrder.getMaximumLimit();//最大限额
        BigDecimal minimumLimit = ezOtcOrder.getMinimumLimit();//最小限额
        BigDecimal amount = placeOrderReqDto.getAmount();//购买数量
        //判断数量是否满足
        if (amount.compareTo(maximumLimit) > 0 || amount.compareTo(minimumLimit) < 0) {
            return Response.error(MessageUtils.message("输入数量不满足条件范围"));
        }
        BigDecimal totalAmount = ezOtcOrder.getTotalAmount();//广告总数量
        BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();//匹配数量
        BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);//未匹配的数量
        //未匹配的数量  是否大于购买数量
        if (nuQuotaAmount.compareTo(amount) < 0) {
            return Response.error(MessageUtils.message("订单数量已发生改变"));
        }
        //OTC信息
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, ezOtcOrder.getUserId()).or().eq(EzAdvertisingBusiness::getUserId, userId);
        List<EzAdvertisingBusiness> list = advertisingBusinessService.list(queryWrapper);
        Map<String, EzAdvertisingBusiness> map = list.stream().collect(Collectors.toMap(EzAdvertisingBusiness::getUserId, Function.identity()));
        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNoByCountryCode(countryCode, IndexOrderNoKey.ORDER_MATCH_INFO);
        //查看  订单类型(0:买  1：卖)
        BigDecimal totalPrice = ezOtcOrder.getPrice().multiply(placeOrderReqDto.getAmount());//总价格

        EzOtcOrderMatch match = new EzOtcOrderMatch();//封装订单
        BeanUtils.copyBeanProp(match, ezOtcOrder);
        details.setNowTime(DateUtils.getNowDate());
        match.setUserId(userId);
        match.setOtcOrderUserId(ezOtcOrder.getUserId());
        match.setOrderMatchNo(orderMatchNo);
        match.setAmount(placeOrderReqDto.getAmount());
        match.setTotalPrice(totalPrice);
        match.setAdvertisingName(map.get(ezOtcOrder.getUserId()).getAdvertisingName());
        match.setMatchAdvertisingName(map.get(userId).getAdvertisingName());

        BeanUtils.copyBeanProp(details, ezOtcOrder);
        details.setAmount(placeOrderReqDto.getAmount());
        details.setOrderMatchNo(orderMatchNo);
        details.setOrderType("1");
        details.setTotalPrice(totalPrice);
        details.setAdvertisingName(map.get(ezOtcOrder.getUserId()).getAdvertisingName());

        //根据支付方式查询
        Integer paymentMethod1 = ezOtcOrder.getPaymentMethod1();
        Integer paymentMethod2 = ezOtcOrder.getPaymentMethod2();
        Integer paymentMethod3 = ezOtcOrder.getPaymentMethod3();
        if ("1".equals(ezOtcOrder.getType())) {//卖单的时候
            LambdaQueryWrapper<EzOtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderNo, ezOtcOrder.getOrderNo());
            //根据发布订单号查询到支付详情
            details.setEzOtcOrderPayments(paymentService.list(lambdaQueryWrapper));
        } else {//买的时候
            details.setEzOtcOrderPayments(paymentService.depositPayment(paymentMethod1, paymentMethod2, paymentMethod3, userId, null, orderMatchNo));
            //冻结 费用
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(ezOtcOrder.getCoinName());
            b.setAvailable(placeOrderReqDto.getAmount().negate());
            b.setFrozen(placeOrderReqDto.getAmount());
            b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b.setMainType(CoinConstants.MainType.FROZEN.getType());
            b.setUserId(userId);
            b.setSonType(RecordSonType.TRANSACTION_FREEZE);
            cList.add(b);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
        }
        //查看订单是否为接单广告
        String isAdvertising = ezOtcOrder.getIsAdvertising();
        Integer prompt = null;
        String sellUserId = null;
        String buyUserId = null;
        int flag = 0;
        if ("1".equals(isAdvertising)) {//不为接单广告 或者为买单
            match.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            details.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            prompt = ezOtcOrder.getPrompt();
            if ("1".equals(ezOtcOrder.getType())) {//卖单
                flag = 1;
                sellUserId = userId;
                buyUserId = match.getOtcOrderUserId();
            }
            if ("0".equals(ezOtcOrder.getType())) {//买单
                flag = 2;
                sellUserId = userId;
                buyUserId = match.getOtcOrderUserId();
            }
        } else {
            flag = 3;
            sellUserId = userId;
            buyUserId = match.getOtcOrderUserId();
            match.setStatus(MatchOrderStatus.PENDINGORDER.getCode());
            details.setStatus(MatchOrderStatus.PENDINGORDER.getCode());
            prompt = otcConfig.getOrderTime();
        }
        //增加商家匹配数量
        ezOtcOrder.setQuotaAmount(quotaAmount.add(placeOrderReqDto.getAmount()));
        baseMapper.updateById(ezOtcOrder);//修改订单

        Date beForeTime = DateUtils.getBeForeTime(prompt);
        details.setDueTime(beForeTime);
        match.setDueTime(beForeTime);
        orderMatchService.save(match);
        //TODO:将订单存入rabbitmq进行死信通信  时间到了就取消订单 根据卖家用户设置而定
        convertAndSend.convert(orderMatchNo, match.getStatus(), prompt);
        //TODO:存入消息
        if (flag == 1) {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatchNo,
                    SysOrderConstants.SysChatMsg.BUY_PLACE_ORDER, MatchOrderStatus.WAITFORPAYMENT));
        } else if (flag == 2) {
            AsyncManager.me().execute(AsyncFactory.sendSysChat(sellUserId, buyUserId, orderMatchNo,
                    SysOrderConstants.SysChatMsg.SELL_PLACE_ORDER, MatchOrderStatus.WAITFORPAYMENT));
        }
        //给用户一个信号
        WebSocketHandle.otherAuthentication(ezOtcOrder.getUserId(),ezOtcOrder.getType(),
                placeOrderReqDto.getAmount()+ezOtcOrder.getCoinName());

        //返回订单
        return Response.success(MessageUtils.message("下单成功"), details);//将订单返回
    }


    /**
     * @param orderNo
     * @Description: 商户 下架广告订单
     * @Param: [orderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     */
    @Override
    @Transactional(value="transactionManager1", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response offShelfOrder(String orderNo) {
        //根据订单号查询到是否存在未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderMatch::getOrderNo, orderNo).and(we -> we.eq(
                EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT).or()
                .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID).or()
                .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER)
                .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.APPEALING));
        List<EzOtcOrderMatch> list = orderMatchService.list(queryWrapper);
        if (list.size() > 0) {
            return Response.error(MessageUtils.message("下架失败!有用户订单未完成,请先处理"));
        }
        //根据订单号查询到订单
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        //订单状态（0：正常 1：已下架）
        if ("1".equals(ezOtcOrder.getStatus())) {
            return Response.error("订单状态已发生变化");
        }
        if ("1".equals(ezOtcOrder.getType())) {// 1：卖)
            BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
            //TODO: 将冻结数量返回商户的资产中
            //解冻卖出的USDT
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(ezOtcOrder.getCoinName());
            b.setAvailable(frozeAmount);//返回冻结的数量
            b.setFrozen(frozeAmount.negate());//解冻冻结的数量
            b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            b.setUserId(ezOtcOrder.getUserId());
            b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
            b.setFee(BigDecimal.ZERO);
            cList.add(b);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
        }
        //改变订单状态
        ezOtcOrder.setStatus("1");
        ezOtcOrder.setEndTime(DateUtils.getNowDate());
        baseMapper.updateById(ezOtcOrder);
        return Response.success();
    }


    /**
     * app订单列表
     *
     * @param orderQueryReqDto
     * @return
     */
    @Override
    public ResponseList<OtcOrderRespDto> otcOrderList(OtcOrderQueryReqDto orderQueryReqDto) {
        IPage<EzOtcOrder> page = new Page<EzOtcOrder>(orderQueryReqDto.getPage(), orderQueryReqDto.getLimit());
        LambdaQueryWrapper<EzOtcOrder> otcOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //广告类型(0:买  1：卖)
        String type = orderQueryReqDto.getType();
        CheckException.checkNotEmpty(type, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getType, type);
        });
        //币种名
        String coinName = orderQueryReqDto.getCoinName();
        CheckException.checkNotEmpty(coinName, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getCoinName, coinName);
        });
        //国际货币代码
        String currencyCode = orderQueryReqDto.getCurrencyCode();
        CheckException.checkNotEmpty(currencyCode, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getCurrencyCode, currencyCode);
        });
        //支付方式
        String payMethodId = orderQueryReqDto.getPayMethodId();
        CheckException.checkNotEmpty(payMethodId, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getPaymentMethod1, payMethodId).or()
                    .eq(EzOtcOrder::getPaymentMethod2, payMethodId).or()
                    .eq(EzOtcOrder::getPaymentMethod3, payMethodId);
        });

        //是否为接单广告
        String isAdvertising = orderQueryReqDto.getIsAdvertising();
        CheckException.checkNotEmpty(isAdvertising, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getIsAdvertising, isAdvertising);
        });
        //支付方式
        String transactionAmount = orderQueryReqDto.getTransactionAmount();//交易数量
        CheckException.checkNotEmpty(transactionAmount, () -> {
            otcOrderLambdaQueryWrapper.ge(EzOtcOrder::getMinimumLimit, transactionAmount);//大于最小即可
        });
        //按照价格降序排列
        otcOrderLambdaQueryWrapper.orderByAsc(EzOtcOrder::getPrice);
        otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getStatus, "0");//订单状态（0：正常 1：已下架）
        otcOrderLambdaQueryWrapper.orderByDesc(EzOtcOrder::getCreateTime);
        IPage<EzOtcOrder> iPage = baseMapper.selectPage(page, otcOrderLambdaQueryWrapper);
        //根据商铺id查询商铺
        List<EzOtcOrder> records = iPage.getRecords();
        //查询所有支付方式
        List<OtcOrderRespDto> otcOrderRespDtos = new ArrayList<>();
        String userId = ContextHandler.getUserId();
        records.forEach(e -> {
            OtcOrderRespDto otcOrderRespDto = new OtcOrderRespDto();
            BeanUtils.copyBeanProp(otcOrderRespDto, e);
            otcOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));
            LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
            businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, e.getUserId());
            EzAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
            otcOrderRespDto.setTotalCount(one.getSellCount() + one.getBuyCount());
            otcOrderRespDto.setFinishRate(one.getFinishRate());
            List<Integer> paymentMethods = new ArrayList<>();
            paymentMethods.add(e.getPaymentMethod1());
            paymentMethods.add(e.getPaymentMethod2());
            paymentMethods.add(e.getPaymentMethod3());
            otcOrderRespDto.setPaymentMethods(paymentMethods);
            //判断是否为自己的订单
            if (StringUtils.isEmpty(userId) || !e.getUserId().equals(userId)) {
                //判断匹配数量是否满足
                BigDecimal totalAmount = e.getTotalAmount();
                BigDecimal quotaAmount = e.getQuotaAmount();
                //未匹配数量
                BigDecimal noQuotaAmount = totalAmount.subtract(quotaAmount);
                if (noQuotaAmount.compareTo(otcOrderRespDto.getMinimumLimit()) >= 0) {
                    otcOrderRespDtos.add(otcOrderRespDto);
                }
            }
        });
        return ResponseList.success(otcOrderRespDtos);
    }


    @Override
    public ResponseList<NewOrderRespDto> nowOrderList(PageQuery pageQuery) {
        String userId = ContextHandler.getUserId();
        ArrayList<NewOrderRespDto> newOrderRespDtos = new ArrayList<>();
        IPage<EzOtcOrder> page = new Page<EzOtcOrder>(pageQuery.getPage(), pageQuery.getLimit());
        LambdaQueryWrapper<EzOtcOrder> otcOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getStatus, "0");//订单状态（0：正常 1：已下架）
        otcOrderLambdaQueryWrapper.orderByDesc(EzOtcOrder::getCreateTime);
        baseMapper.selectPage(page, otcOrderLambdaQueryWrapper).getRecords().forEach(e -> {
            if ( StringUtils.isEmpty(userId) || !e.getUserId().equals(userId) ) {
                NewOrderRespDto newOrderRespDto = new NewOrderRespDto();
                BeanUtils.copyBeanProp(newOrderRespDto, e);
                newOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));
                if (newOrderRespDto.getLastAmount().compareTo(e.getMinimumLimit()) >= 0) {
                    newOrderRespDtos.add(newOrderRespDto);
                }
            }
        });
        return ResponseList.success(newOrderRespDtos);
    }


    /**
     * @param otcOrderNo
     * @Description: 购买查询订单详情
     * @Param: [otcOrderNo]
     * @return: com.ezcoins.response.Response<com.ezcoins.project.otc.entity.resp.NewOrderRespDto>
     * @Author: Wanglei
     * @Date: 2021/6/22
     */
    @Override
    public Response<OrderInfo> orderInfo(String otcOrderNo) {
        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper2.eq(EzAdvertisingBusiness::getUserId, ContextHandler.getUserId());
        EzAdvertisingBusiness one1 = advertisingBusinessService.getOne(businessLambdaQueryWrapper2);
        if (StringUtils.isEmpty(one1.getSecurityPassword())) {
            throw new BaseException(null, "700", MessageUtils.message("请先完善otc交易信息"), null);
        }
        EzOtcOrder ezOtcOrder = baseMapper.selectById(otcOrderNo);
        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, ezOtcOrder.getUserId());
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyBeanProp(orderInfo, ezOtcOrder);
        //查询所有支付方式
        LambdaQueryWrapper<EzOtcOrderPayment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderPayment::getOrderNo, otcOrderNo);
        paymentService.list(queryWrapper); // 有问题
//        List<EzOtcOrderPayment> lists = paymentService.list(queryWrapper);
        List<Integer> paymentMethods = new ArrayList<>();
        paymentMethods.add(ezOtcOrder.getPaymentMethod1());
        paymentMethods.add(ezOtcOrder.getPaymentMethod2());
        paymentMethods.add(ezOtcOrder.getPaymentMethod3());
        orderInfo.setLastAmount(ezOtcOrder.getTotalAmount().subtract(ezOtcOrder.getQuotaAmount()));
        orderInfo.setPaymentMethods(paymentMethods);
        orderInfo.setAdvertisingName(one.getAdvertisingName());
        return Response.success(orderInfo);
    }


    /**
     * @param
     * @Description: 商户 接单(订单广告)
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     */
    @Override
    @Transactional(value = "transactionManager1", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response merchantOrder(OrderOperateReqDto orderOperateReqDto) {
        //根据订单号查询订单
        EzOtcOrderMatch orderMatch = orderMatchService.getById(orderOperateReqDto.getMatchOrderNo());
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PENDINGORDER.getCode())) {
            throw new BaseException("订单状态已发生变化");
        }
        List<EzOtcChatMsg> list = new ArrayList<>();
        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        ezOtcChatMsg.setOrderMatchNo(orderMatch.getOrderMatchNo());
        ezOtcChatMsg.setReceiveUserId(orderMatch.getUserId());
        String status = null;
        if ("1".equals(orderOperateReqDto.getOperate())) {
            status = MatchOrderStatus.REFUSE.getCode();
            //拒绝接受订单
            orderMatch.setStatus(status);
            ezOtcChatMsg.setSendText(SystemOrderTips.REFUSE_ORDER);
            otcChatMsgService.sendSysChat(list, MatchOrderStatus.REFUSE.getCode());
            return Response.success();
        } else {
            //查询匹配到的订单
            EzOtcOrder ezOtcOrder = baseMapper.selectById(orderMatch.getOrderNo());
            BigDecimal amount = orderMatch.getAmount();//订单数量
            //查看剩余匹配数量是否满足
            BigDecimal totalAmount = ezOtcOrder.getTotalAmount();
            BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();
            //未匹配的数量
            BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);
            if (amount.compareTo(nuQuotaAmount) > 0) {
                return Response.error(MessageUtils.message("订单数量不足"));
            }
            //订单到期时间=当前时间+付款期限(分钟)   根据用户设置的订单付款期限
            Integer prompt = ezOtcOrder.getPrompt();//付款期限(分钟)
            Date beForeTime = DateUtils.getBeForeTime(prompt);
            orderMatch.setDueTime(beForeTime);
            //修改订单状态
            status = MatchOrderStatus.WAITFORPAYMENT.getCode();
            orderMatch.setStatus(status);
            //增加商家匹配数量
            ezOtcOrder.setQuotaAmount(quotaAmount.add(amount));
            baseMapper.updateById(ezOtcOrder);
            ezOtcChatMsg.setSendText(SystemOrderTips.ORDERS);
        }
        list.add(ezOtcChatMsg);
        //TODO:存入消息
        orderMatchService.updateById(orderMatch);
        otcChatMsgService.sendSysChat(list, status);
        return Response.success();
    }


}
