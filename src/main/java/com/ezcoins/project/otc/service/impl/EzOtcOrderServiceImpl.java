package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.CoinConstant;
import com.ezcoins.constant.RecordSonType;
import com.ezcoins.constant.SystemOrderTips;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.interf.IndexOrderNoKey;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.mq.RabbitMQConfiguration;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.*;
import io.swagger.models.auth.In;
import org.apache.catalina.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

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
    private EzPaymentMethodService paymentMethodService;

    @Autowired
    private EzPaymentInfoService paymentInfoService;

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
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseResponse releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto) {
        //查看otc信息是否有过修改
        String userId = ContextHandler.getUserId();
        if (!advertisingBusinessService.isUpdate(userId)) {
            return BaseResponse.error(MessageUtils.message("请先完善otc交易信息")).code(700);
        }
        LambdaQueryWrapper<Type> queryWrapper = new LambdaQueryWrapper<Type>();
        queryWrapper.eq(Type::getCoinName, otcOrderReqDto.getCoinName());
        Type coinType = typeService.getOne(queryWrapper);//查询到币种
        if (!typeService.statusService(coinType,CoinConstant.OTC_STATUS)){
            return BaseResponse.error(MessageUtils.message("当前币种OTC交易尚未开"));
        }
        BigDecimal amount = otcOrderReqDto.getTotalAmount();//发布数量
        EzOtcConfig otcConfig = otcConfigService.getById(1);//otc配置
        BigDecimal minimumLimit = otcOrderReqDto.getMinimumLimit();
        BigDecimal maximumLimit = otcOrderReqDto.getMaximumLimit();
        if (maximumLimit.compareTo(otcConfig.getMaxAmount()) > 0) {
            return BaseResponse.error(MessageUtils.message("超过限额"));
        }
        if (minimumLimit.compareTo(otcConfig.getMinAmount()) < 0) {
            return BaseResponse.error(MessageUtils.message("低于限额"));
        }
        Integer prompt = otcOrderReqDto.getPrompt();
        if (prompt > otcConfig.getMaxPayTime() || prompt < otcConfig.getMinPayTime()) {
            return BaseResponse.error(MessageUtils.message("付款时间错误"));
        }
        //冻结卖出的USDT
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(otcOrderReqDto.getCoinName());
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setMainType(CoinConstants.MainType.RELEASEORDER.getType());

        BigDecimal advertisingFeeRatio = coinType.getOtcFeeRatio();//比例手续费
        BigDecimal fee = advertisingFeeRatio.multiply(amount);//手续费

        String orderNo = orderIndexService.getOrderNoByCurrencyCode(otcOrderReqDto.getCurrencyCode(), IndexOrderNoKey.ORDER_INFO);
        //判断订单 买卖
        if ("0".equals(otcOrderReqDto.getType())) {//买
            b.setAvailable(fee.negate());
            b.setSonType(RecordSonType.RELEASE_BUY_ORDER);//手续费
            b.setFee(fee);
            cList.add(b);
        } else if ("1".equals(otcOrderReqDto.getType())) {//卖
            BigDecimal totalAmount = amount.add(fee);//总扣除
            b.setFrozen(amount);
            b.setAvailable(totalAmount.negate());
            b.setSonType(RecordSonType.RELEASE_SELL_ORDER);
            b.setFee(fee);
            cList.add(b);
            
            //将支付方式存入数据订单对应的支付详情表
            Integer paymentMethod1 = otcOrderReqDto.getPaymentMethod1();
            Integer paymentMethod2 = otcOrderReqDto.getPaymentMethod2();
            Integer paymentMethod3 = otcOrderReqDto.getPaymentMethod3();
            LambdaQueryWrapper<EzPaymentInfo> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(EzPaymentInfo::getUserId, userId);
            queryWrapper1.eq(EzPaymentInfo::getPaymentMethodId, paymentMethod1).or().
                    eq(EzPaymentInfo::getPaymentMethodId, paymentMethod2).or().
                    eq(EzPaymentInfo::getPaymentMethodId, paymentMethod3);
            List<EzPaymentInfo> list = paymentInfoService.list(queryWrapper1);


            List<EzOtcOrderPayment> list1 = new ArrayList<EzOtcOrderPayment>();
            list.forEach(e -> {
                EzOtcOrderPayment ezOtcOrderPayment = new EzOtcOrderPayment();
                BeanUtils.copyBeanProp(ezOtcOrderPayment, e);
                ezOtcOrderPayment.setType("0");
                ezOtcOrderPayment.setOrderNo(orderNo);
                list1.add(ezOtcOrderPayment);
            });
            paymentService.saveBatch(list1);
        }
        accountService.balanceChangeSYNC(cList);
        EzOtcOrder ezOtcOrder = new EzOtcOrder();
        ezOtcOrder.setOrderNo(orderNo);
        ezOtcOrder.setFrozeAmount(amount);
        ezOtcOrder.setUserId(userId);
        BeanUtils.copyBeanProp(ezOtcOrder, otcOrderReqDto);
        //存入新的订单
        baseMapper.insert(ezOtcOrder);
        return BaseResponse.success();
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
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response<PaymentDetails> placeAnOrder(PlaceOrderReqDto placeOrderReqDto) {
        String userId = ContextHandler.getUserId();
        //查询当前用户是否被取消订单是否超过规定数量
        //获取otc基本配置
        EzOtcConfig otcConfig = otcConfigService.getById(1);
        //获取otc配置每天能取消的次数 5
        Integer maxCancelNum = otcConfig.getMaxCancelNum();
        Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
        if (object != null && (Integer) object > maxCancelNum) {//5后面从配置数据库得到
            return Response.error(MessageUtils.message("你今天取消次数超过上线,每天再来"));
        }

        PaymentDetails details = new PaymentDetails();
        //查看用户是否有未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getUserId, userId);
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode()).or()
                .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode());
        EzOtcOrderMatch orderMatch = orderMatchService.getOne(matchLambdaQueryWrapper);//匹配订单是否有未完成

        //通过订单号查询到购买的订单
        String orderNo = placeOrderReqDto.getOrderNo();
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        if (null != orderMatch) {
            //判断订单类型
            if ("0".equals(orderMatch.getType())) {//买单
                LambdaQueryWrapper<EzOtcOrderPayment> paymentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                paymentLambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderNo, orderMatch.getOrderMatchNo());
                details.setEzOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            } else {//卖单   查询发布订单的支付详情
                LambdaQueryWrapper<EzOtcOrderPayment> paymentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                paymentLambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderNo, orderMatch.getOrderNo());
                details.setEzOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            }
            details.setAmount(orderMatch.getAmount());
            details.setCoinName(orderMatch.getCoinName());
            details.setOrderMatchNo(orderMatch.getOrderMatchNo());
            details.setDueTime(orderMatch.getDueTime());
            details.setIsAdvertising(ezOtcOrder.getIsAdvertising());
            details.setTotalPrice(orderMatch.getTotalPrice());
            details.setAdvertisingName(orderMatch.getAdvertisingName());
            details.setPrice(orderMatch.getPrice());
            details.setStatus(orderMatch.getStatus());
            details.setType(orderMatch.getType());
            return Response.success(MessageUtils.message("请先完成当前未完成的订单"), details);//将订单返回
        }

        //判断用户注册的国籍是否满足购买条件
        String currencyCode = ezOtcOrder.getCurrencyCode();
        EzUser user = userService.getById(userId);
        String countryCode = user.getCountryCode();
        //通过国家编号查询到国家
        LambdaQueryWrapper<EzCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(EzCountryConfig::getCountryCode, countryCode);
        EzCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        if (!one.getCurrencyCode().equals(currencyCode)) {
            throw new BaseException(null, "701", null, "根据您注册所在地的相关规定，您只能交易本地区的法币");
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
        //判断冻结数量/未匹配的数量  是否大于购买数量
        if (nuQuotaAmount.compareTo(amount) < 0) {
            return Response.error(MessageUtils.message("订单数量已发生改变"));
        }
        //OTC信息
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, ezOtcOrder.getUserId());
        EzAdvertisingBusiness advertisingBusiness = advertisingBusinessService.getOne(queryWrapper);
        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNoByCountryCode(countryCode, IndexOrderNoKey.ORDER_MATCH_INFO);

        //查看  订单类型(0:买  1：卖)
        BigDecimal totalPrice = ezOtcOrder.getPrice().multiply(placeOrderReqDto.getAmount());
        EzOtcOrderMatch match = new EzOtcOrderMatch();//封装订单
        match.setOrderMatchNo(orderMatchNo);
        match.setOrderNo(ezOtcOrder.getOrderNo());
        match.setUserId(userId);
        match.setOtcOrderUserId(ezOtcOrder.getUserId());
        match.setAmount(placeOrderReqDto.getAmount());
        match.setPrice(ezOtcOrder.getPrice());
        match.setOrderType("1");
        match.setType(ezOtcOrder.getType());
        match.setCurrencyCode(ezOtcOrder.getCurrencyCode());
        match.setTotalPrice(totalPrice);
        match.setCoinName(ezOtcOrder.getCoinName());
        match.setAdvertisingName(advertisingBusiness.getAdvertisingName());

        details.setAmount(placeOrderReqDto.getAmount());
        details.setIsAdvertising(ezOtcOrder.getIsAdvertising());
        details.setCoinName(ezOtcOrder.getCoinName());
        details.setOrderMatchNo(orderMatchNo);
        details.setTotalPrice(totalPrice);
        details.setAdvertisingName(advertisingBusiness.getAdvertisingName());
        details.setPrice(ezOtcOrder.getPrice());

        //根据支付方式查询
        Integer paymentMethod1 = ezOtcOrder.getPaymentMethod1();
        Integer paymentMethod2 = ezOtcOrder.getPaymentMethod2();
        Integer paymentMethod3 = ezOtcOrder.getPaymentMethod3();
        List<Integer> list1=new ArrayList<>();
        if (null!=paymentMethod1){
            list1.add(paymentMethod1);
        }
        if (null!=paymentMethod2){
            list1.add(paymentMethod2);
        }
        if (null!=paymentMethod3){
            list1.add(paymentMethod3);
        }
        if ("1".equals(ezOtcOrder.getType())) {//卖单的时候
            LambdaQueryWrapper<EzOtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderNo, ezOtcOrder.getOrderNo());
            List<EzOtcOrderPayment> list = paymentService.list(lambdaQueryWrapper);
            //根据发布订单号查询到支付详情
            details.setEzOtcOrderPayments(list);
        } else {//买的时候
            LambdaQueryWrapper<EzPaymentInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(EzPaymentInfo::getUserId, ezOtcOrder.getUserId());
            lambdaQueryWrapper.in(EzPaymentInfo::getPaymentMethodId,list1);
            List<EzPaymentInfo> ezPaymentInfos = paymentInfoService.list(lambdaQueryWrapper);
            if (ezPaymentInfos.size()==0) {//买单
                throw new BaseException(null, "801", null, "未匹配到支付方式");
            }
            //冻结 费用
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(ezOtcOrder.getCoinName());
            b.setAvailable(placeOrderReqDto.getAmount().negate());
            b.setFrozen(placeOrderReqDto.getAmount());
            b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b.setUserId(userId);
            b.setMainType(CoinConstants.MainType.FROZEN.getType());
            b.setFee(BigDecimal.ZERO);
            cList.add(b);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
            List<EzOtcOrderPayment> ezOtcOrderPayments = new ArrayList<>();
            ezPaymentInfos.forEach(e -> {
                EzOtcOrderPayment ezOtcOrderPayment = new EzOtcOrderPayment();
                ezOtcOrderPayment.setOrderMatchNo(orderMatchNo);
                ezOtcOrderPayment.setPaymentQrCode(e.getPaymentQrCode());
                ezOtcOrderPayment.setAccountNumber(e.getAccountNumber());
                ezOtcOrderPayment.setBankName(e.getBankName());
                ezOtcOrderPayment.setRealName(e.getRealName());
                ezOtcOrderPayment.setPaymentMethodId(e.getPaymentMethodId());
                ezOtcOrderPayment.setType("1");
                ezOtcOrderPayments.add(ezOtcOrderPayment);
            });
            paymentService.saveBatch(ezOtcOrderPayments);
            details.setEzOtcOrderPayments(ezOtcOrderPayments);
        }

        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        //查看订单是否为接单广告
        String isAdvertising = ezOtcOrder.getIsAdvertising();
        Integer prompt=null;

        if ("1".equals(isAdvertising) || "0".equals(ezOtcOrder.getType())) {//不为接单广告 或者为买单
            match.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            details.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
            prompt = ezOtcOrder.getPrompt();
            if("1".equals(isAdvertising) && "1".equals(ezOtcOrder.getType())){
                ezOtcChatMsg.setSendText(SystemOrderTips.PLACE_ORDER_SUCCESS);
            }
            if("1".equals(isAdvertising) && "0".equals(ezOtcOrder.getType())){
                ezOtcChatMsg.setSendText(SystemOrderTips.PLACE_ORDER_SUCCESS_SELL);
            }
            //增加商家匹配数量
            ezOtcOrder.setQuotaAmount(quotaAmount.add(placeOrderReqDto.getAmount()));
        } else {
            ezOtcChatMsg.setSendText(SystemOrderTips.ORDERS_TIPS);
            match.setStatus(MatchOrderStatus.PENDINGORDER.getCode());
            details.setStatus(MatchOrderStatus.PENDINGORDER.getCode());
            prompt = otcConfig.getOrderTime();
        }
        Date beForeTime = DateUtils.getBeForeTime(prompt);
        match.setDueTime(beForeTime);
        details.setDueTime(beForeTime);
        orderMatchService.save(match);
        baseMapper.updateById(ezOtcOrder);//修改订单
        //TODO:将订单存入rabbitmq进行死信通信  时间到了就取消订单 根据卖家用户设置而定
        convertAndSend.convert(orderMatchNo,match.getStatus(),prompt);
        //TODO:存入消息
        ezOtcChatMsg.setOrderMatchNo(match.getOrderMatchNo());
        otcChatMsgService.save(ezOtcChatMsg);
        //返回订单
        return Response.success("下单成功", details);//将订单返回
    }


    /**
     * @Description: 商户 下架广告订单
     * @Param: [orderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     * @param orderNo
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseResponse offShelfOrder(String orderNo) {
        //根据订单号查询到是否存在未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getOrderNo, orderNo);
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT).or().
                eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID).or().
                eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER);
        List<EzOtcOrderMatch> list = orderMatchService.list(matchLambdaQueryWrapper);

        if (list.size() > 0) {
            return BaseResponse.error(MessageUtils.message("下架失败!有用户订单未完成,请先处理"));
        }
        //订单状态（0：正常 1：已下架）
        //根据订单号查询到订单
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        if ("1".equals(ezOtcOrder.getType())) {
            BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
            //TODO: 将冻结数量返回商户的资产中
            //冻结卖出的USDT
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(ezOtcOrder.getCoinName());
            b.setAvailable(frozeAmount);
            b.setFrozen(frozeAmount.negate());
            b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            b.setUserId(ezOtcOrder.getUserId());
            b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
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
        return BaseResponse.success();
    }



    /**
     * @Description: 商户 接单(订单广告)
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     * @param
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseResponse merchantOrder(OrderOperateReqDto orderOperateReqDto) {
        //根据订单号查询订单
        EzOtcOrderMatch orderMatch = orderMatchService.getById(orderOperateReqDto.getMatchOrderNo());
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PENDINGORDER.getCode())) {
            throw new BaseException(MessageUtils.message("订单状态已发生变化"));
        }

        if ("1".equals(orderOperateReqDto.getOperate())) {
            //拒绝接受订单
            orderMatch.setStatus(MatchOrderStatus.REFUSE.getCode());
            orderMatchService.updateById(orderMatch);
            return BaseResponse.success();
        }
        //查询匹配到的订单
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderMatch.getOrderNo());
        BigDecimal amount = orderMatch.getAmount();//订单数量

        //查看剩余匹配数量是否满足
        BigDecimal totalAmount = ezOtcOrder.getTotalAmount();
        BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();
        //未匹配的数量
        BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);
        if (amount.compareTo(nuQuotaAmount) > 0) {
            return BaseResponse.error(MessageUtils.message("订单数量不足"));
        }
        //订单到期时间=当前时间+付款期限(分钟)   根据用户设置的订单付款期限
        Integer prompt = ezOtcOrder.getPrompt();//付款期限(分钟)
        Date beForeTime = DateUtils.getBeForeTime(prompt);
        orderMatch.setDueTime(beForeTime);
        //修改订单状态
        orderMatch.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
        orderMatchService.updateById(orderMatch);
        //增加商家匹配数量
        ezOtcOrder.setQuotaAmount(quotaAmount.add(amount));
        baseMapper.updateById(ezOtcOrder);

        //TODO:存入消息
        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        ezOtcChatMsg.setSendText(SystemOrderTips.ORDERS_TIPS);
        ezOtcChatMsg.setOrderMatchNo(orderMatch.getOrderMatchNo());
        otcChatMsgService.save(ezOtcChatMsg);

        return BaseResponse.success();
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
        otcOrderLambdaQueryWrapper.orderByDesc(EzOtcOrder::getPrice);
        otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getStatus, "0");//订单状态（0：正常 1：已下架）
        IPage<EzOtcOrder> iPage = baseMapper.selectPage(page, otcOrderLambdaQueryWrapper);

        //根据商铺id查询商铺
        List<EzOtcOrder> records = iPage.getRecords();
        //查询所有支付方式
        List<EzPaymentMethod> methods = paymentMethodService.list();

        List<OtcOrderRespDto> otcOrderRespDtos = new ArrayList<>();
        String userId = ContextHandler.getUserId();
        records.forEach(e -> {
            OtcOrderRespDto otcOrderRespDto = new OtcOrderRespDto();
            otcOrderRespDto.setCoinName(e.getCoinName());
            otcOrderRespDto.setOrderNo(e.getOrderNo());
            otcOrderRespDto.setMinimumLimit(e.getMinimumLimit());
            otcOrderRespDto.setMaximumLimit(e.getMaximumLimit());
            otcOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));

            LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
            businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, e.getUserId());
            EzAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
            otcOrderRespDto.setAdvertisingName(one.getAdvertisingName());
            List<Integer> paymentMethods = new ArrayList<>();
            if (StringUtils.isNotNull(e.getPaymentMethod1())) {
                paymentMethods.add(methods.get(e.getPaymentMethod1() - 1).getId());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod2())) {
                paymentMethods.add(methods.get(e.getPaymentMethod2() - 1).getId());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod3())) {
                paymentMethods.add(methods.get(e.getPaymentMethod3() - 1).getId());
            }
            otcOrderRespDto.setPaymentMethods(paymentMethods);
            otcOrderRespDto.setUserId(e.getUserId());
            otcOrderRespDto.setTotalCount(one.getSellCount() + one.getBuyCount());
            otcOrderRespDto.setMouthFinishRate(one.getMouthFinishRate());
            otcOrderRespDto.setPrice(e.getPrice());
            otcOrderRespDto.setIsAdvertising(e.getIsAdvertising());
            //判断是否为自己的订单
            if (StringUtils.isNotEmpty(userId) && e.getUserId().equals(userId)) {
            } else {
                //判断匹配数量是否满足
                BigDecimal totalAmount = e.getTotalAmount();
                BigDecimal quotaAmount = e.getQuotaAmount();
                //未匹配数量
                BigDecimal noQuotaAmount=quotaAmount.subtract(totalAmount);
                if (noQuotaAmount.compareTo(otcOrderRespDto.getMinimumLimit())>0){
                    otcOrderRespDtos.add(otcOrderRespDto);
                }
            }
        });
        return ResponseList.success(otcOrderRespDtos);
    }


    /**
     * @Description: 新订单
     * @Param: []
     * @return: com.ezcoins.response.ResponseList<com.ezcoins.project.otc.entity.resp.OtcOrderRespDto>
     * @Author: Wanglei
     * @Date: 2021/6/22
     */
    @Override
    public ResponseList<NewOrderRespDto> nowOrderList(PageQuery pageQuery) {
        String userId = ContextHandler.getUserId();
        ArrayList<NewOrderRespDto> newOrderRespDtos = new ArrayList<>();
        IPage<EzOtcOrder> page = new Page<EzOtcOrder>(pageQuery.getPage(), pageQuery.getLimit());
        LambdaQueryWrapper<EzOtcOrder> otcOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getStatus, "0");//订单状态（0：正常 1：已下架）
        otcOrderLambdaQueryWrapper.orderByDesc(EzOtcOrder::getCoinName);
        baseMapper.selectPage(page, otcOrderLambdaQueryWrapper).getRecords().forEach(e -> {
            if (!e.getUserId().equals(userId) || StringUtils.isEmpty(userId)) {
                NewOrderRespDto newOrderRespDto = new NewOrderRespDto();
                newOrderRespDto.setOrderNo(e.getOrderNo());
                newOrderRespDto.setCoinName(e.getCoinName());
                newOrderRespDto.setType(e.getType());
                newOrderRespDto.setPrice(e.getPrice());
                newOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));
                newOrderRespDtos.add(newOrderRespDto);
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
        EzOtcOrder ezOtcOrder = baseMapper.selectById(otcOrderNo);
        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, ezOtcOrder.getUserId());
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
        if (one.getAdvertisingName().equals(ContextHandler.getUserId()) || StringUtils.isEmpty(one.getSecurityPassword())){
            throw  new BaseException(null,"700",null,MessageUtils.message("请先完善otc交易信息"));
        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setPrice(ezOtcOrder.getPrice());
        orderInfo.setTradingTips(ezOtcOrder.getTradingTips());
        //查询所有支付方式
        LambdaQueryWrapper<EzOtcOrderPayment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderPayment::getOrderNo,otcOrderNo);
        paymentService.list(queryWrapper);
        List<Integer> paymentMethods = new ArrayList<>();
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod1())) {
            paymentMethods.add(ezOtcOrder.getPaymentMethod1());
        }
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod2())) {
            paymentMethods.add(ezOtcOrder.getPaymentMethod2());
        }
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod3())) {
            paymentMethods.add(ezOtcOrder.getPaymentMethod3());
        }
        orderInfo.setCoinName(ezOtcOrder.getCoinName());
        orderInfo.setLastAmount(ezOtcOrder.getTotalAmount().subtract(ezOtcOrder.getQuotaAmount()));
        orderInfo.setMaximumLimit(ezOtcOrder.getMaximumLimit());
        orderInfo.setMinimumLimit(ezOtcOrder.getMinimumLimit());
        orderInfo.setAdvertisingName(one.getAdvertisingName());
        orderInfo.setPaymentMethods(paymentMethods);
        return Response.success(orderInfo);
    }

}
