package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.interf.IndexOrderNoKey;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.mq.RabbitMQConfiguration;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
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
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.EncoderUtil;
import com.ezcoins.utils.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
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
 *  服务实现类
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
    private AmqpTemplate rabbitTemplate;


    @Autowired
    private EzPaymentMethodService paymentMethodService;



    @Autowired
    private EzPaymentInfoService paymentInfoService;


    @Autowired
    private EzAdvertisingBusinessService advertisingBusinessService;


    @Autowired
    private AccountService accountService;



    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseResponse releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto) {
        //获得订单号  国家代码+订单日期
        String currencyCode = otcOrderReqDto.getCurrencyCode();
        LambdaQueryWrapper<EzCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(EzCountryConfig::getCurrencyCode, currencyCode);
        EzCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        if (null == one) {
           return BaseResponse.error("国家货币代码不存在");
        }

        String countryCode = one.getCountryCode();//国家编号
        String orderNo = orderIndexService.getOrderNo(countryCode, IndexOrderNoKey.ORDER_INFO);
        BigDecimal amount = otcOrderReqDto.getTotalAmount();

        String userId = ContextHandler.getUserId();

        //判断订单 买卖
        if ("0".equals(otcOrderReqDto.getType())){//买
        }else if ("1".equals(otcOrderReqDto.getType())){//卖
            //将卖出订单金额冻结
            //手续费
            EzOtcConfig otcConfig = otcConfigService.getById(1);
            BigDecimal advertisingFee = otcConfig.getAdvertisingFee();
            BigDecimal fee = advertisingFee.multiply(amount);
            BigDecimal totalAmount=amount.add(fee);
            //冻结卖出的USDT
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName(otcOrderReqDto.getCoinName());
            b.setAvailable(totalAmount.negate());
            b.setFrozen(amount);
            b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b.setUserId(userId);
            b.setMainType(CoinConstants.MainType.FROZEN.getType());
            b.setFee(fee);

            cList.add(b);
            accountService.balanceChangeSYNC(cList);
        }

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
    public Response<PaymentDetails> placeAnOrder(PlaceOrderReqDto placeOrderReqDto) {
        PaymentDetails details = new PaymentDetails();
        String userId = ContextHandler.getUserId();

        //查询当前用户是否被取消订单是否超过规定数量
        //获取otc基本配置
        EzOtcConfig otcConfig = otcConfigService.getById(1);

        //获取otc配置每天能取消的次数 5
        Integer maxCancelNum = otcConfig.getMaxCancelNum();

        Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
        if (object!=null
                && (Integer)object > maxCancelNum) {//5后面从配置数据库得到
            return Response.error("你今天取消次数超过上线,每天再来");
        }

        //查看用户是否有未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getUserId, userId);
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode()).or()
                .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode());
        EzOtcOrderMatch orderMatch = orderMatchService.getOne(matchLambdaQueryWrapper);


        //通过订单号查询到购买的订单
        String orderNo = placeOrderReqDto.getOrderNo();
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderNo);
        List<EzPaymentMethod> list=new ArrayList<>();

        if (null != orderMatch) {
            //收款方式列表
            List<EzPaymentMethod> paymentMethodList = paymentMethodService.list();
            if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod1())){
                list.add(paymentMethodList.get(ezOtcOrder.getPaymentMethod1()-1));
            }
            if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod2())){
                list.add(paymentMethodList.get(ezOtcOrder.getPaymentMethod2()-1));
            }
            if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod3())){
                list.add(paymentMethodList.get(ezOtcOrder.getPaymentMethod3()-1));
            }
            details.setPaymentMethodList(list);
            details.setAmount(orderMatch.getAmount());
            details.setCoinName(orderMatch.getCoinName());
            details.setOrderMatchNo(orderMatch.getOrderMatchNo());
            details.setDueTime(orderMatch.getDueTime());
            details.setTotalPrice(orderMatch.getTotalPrice());
            details.setAdvertisingName(orderMatch.getAdvertisingName());
            details.setPrice(orderMatch.getPrice());
            details.setStatus(orderMatch.getStatus());
            return Response.success("请先完成当前未完成的订单",details);//将订单返回
        }


        BigDecimal maximumLimit = ezOtcOrder.getMaximumLimit();//最大限额
        BigDecimal minimumLimit = ezOtcOrder.getMinimumLimit();//最小限额
        BigDecimal amount = placeOrderReqDto.getAmount();//购买数量
        //判断数量是否满足
        if (amount.compareTo(maximumLimit) > 0 || amount.compareTo(minimumLimit) < 0) {
            return Response.error("输入数量不满足条件范围");
        }

        BigDecimal totalAmount = ezOtcOrder.getTotalAmount();//广告总数量
        BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();//匹配数量
        BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);//未匹配的数量

        //判断冻结数量/未匹配的数量  是否大于购买数量
        if (nuQuotaAmount.compareTo(amount) < 0) {
            return Response.error("订单数量已发生改变");
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
            throw new BaseException("根据您注册所在地的相关规定，您只能交易本地区的法币");
        }

        //OTC信息
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId,ezOtcOrder.getUserId());
        EzAdvertisingBusiness advertisingBusiness = advertisingBusinessService.getOne(lambdaQueryWrapper);

        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNo(countryCode, IndexOrderNoKey.ORDER_MATCH_INFO);

        //查看  订单类型(0:买  1：卖)
        EzOtcOrderMatch match = new EzOtcOrderMatch();//封装订单

        match.setOrderMatchNo(orderMatchNo);
        match.setOrderNo(ezOtcOrder.getOrderNo());
        match.setUserId(userId);
        match.setOtcOrderUserId(ezOtcOrder.getUserId());
        match.setAmount(placeOrderReqDto.getAmount());
        match.setPrice(ezOtcOrder.getPrice());
        BigDecimal totalPrice = ezOtcOrder.getPrice().multiply(placeOrderReqDto.getAmount());
        match.setTotalPrice(totalPrice);
        match.setCoinName(ezOtcOrder.getCoinName());
        match.setAdvertisingName(advertisingBusiness.getAdvertisingName());

        //订单到期时间=当前时间+付款期限(分钟)   根据用户设置的订单付款期限
        Integer prompt = ezOtcOrder.getPrompt();//付款期限(分钟)
        Date beForeTime = DateUtils.getBeForeTime(prompt);
        match.setDueTime(beForeTime);


        //收款方式列表
        List<EzPaymentMethod> paymentMethodList = paymentMethodService.list();
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod1())){
            list.add(paymentMethodList.get(ezOtcOrder.getPaymentMethod1()-1));
        }
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod2())){
            list.add(paymentMethodList.get(ezOtcOrder.getPaymentMethod2()-1));
        }
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod3())){
            list.add(paymentMethodList.get(ezOtcOrder.getPaymentMethod3()-1));
        }
        details.setPaymentMethodList(list);
        details.setAmount(placeOrderReqDto.getAmount());
        details.setCoinName(ezOtcOrder.getCoinName());
        details.setOrderMatchNo(orderMatchNo);
        details.setDueTime(beForeTime);
        details.setTotalPrice(totalPrice);
        details.setAdvertisingName(advertisingBusiness.getAdvertisingName());
        details.setPrice(ezOtcOrder.getPrice());


        //查看订单是否为接单广告
        String isAdvertising = ezOtcOrder.getIsAdvertising();
        if ("1".equals(isAdvertising)) {//不为接单广告
            match.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode()); //不是接单广告直接进入待支付状态
            details.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode()); //不是接单广告直接进入待支付状态
            //增加商家匹配数量
            ezOtcOrder.setQuotaAmount(quotaAmount.add(placeOrderReqDto.getAmount()));

            //TODO:将订单存入rabbitmq进行死信通信  时间到了就取消订单 根据卖家用户设置而定
            this.rabbitTemplate.convertAndSend(
                    RabbitMQConfiguration.orderExchange, //发送至订单交换机
                    RabbitMQConfiguration.routingKeyOrder, //订单定routingKey
                    orderMatchNo+"_"+MatchOrderStatus.WAITFORPAYMENT.getCode() //订单号   这里可以传对象 比如直接传订单对象
                    , message -> {
                        // 如果配置了 params.put("x-message-ttl", 5 * 1000);
                        // 那么这一句也可以省略,具体根据业务需要是声明 Queue 的时候就指定好延迟时间还是在发送自己控制时间
                        message.getMessageProperties().setExpiration(1000 * 60 *prompt  +"");
                        return message;
                    });
        } else {
            match.setStatus(MatchOrderStatus.PENDINGORDER.getCode()); //接单广告直接进入商家待接单状态  如果此模式下 用户取消订单免除每天取消限制的
            details.setStatus(MatchOrderStatus.PENDINGORDER.getCode()); //接单广告直接进入商家待接单状态  如果此模式下 用户取消订单免除每天取消限制的
            //TODO: 将订单存入rabbitmq进行死信通信  时间到了就取消订单  接单时间根据otc设置而定
            this.rabbitTemplate.convertAndSend(
                    RabbitMQConfiguration.orderExchange, //发送至订单交换机
                    RabbitMQConfiguration.routingKeyOrder, //订单定routingKey
                    orderMatchNo+"_"+MatchOrderStatus.PENDINGORDER.getCode() //订单号   这里可以传对象 比如直接传订单对象
                    , message -> {
                        // 如果配置了 params.put("x-message-ttl", 5 * 1000);
                        // 那么这一句也可以省略,具体根据业务需要是声明 Queue 的时候就指定好延迟时间还是在发送自己控制时间
                        message.getMessageProperties().setExpiration(1000 * 10 * 60 * otcConfig.getOrderTime()+"");
                        return message;
                    });
        }
        orderMatchService.save(match);
        baseMapper.updateById(ezOtcOrder);//修改订单
        //返回订单号
        return Response.success("下单成功",details);//将订单号返回
    }




    /***
     * @Description: 商户 下架广告订单
     * @Param: [orderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     * @param orderNo
     */
    @Override
    public BaseResponse offShelfOrder(String orderNo) {
        //根据订单号查询到是否存在未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getOrderNo, orderNo);
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT).or().
                eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID).or().
                eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER);

        List<EzOtcOrderMatch> list = orderMatchService.list(matchLambdaQueryWrapper);
        if (list.size() > 0) {
            return BaseResponse.error("下架失败!有用户订单未完成,请先处理");
        }
        //订单状态（0：正常 1：已下架）
        //根据订单号查询到订单
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderNo);



        if ("1".equals(ezOtcOrder.getType())){
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

    /***
     * @Description: 商户 接单(订单广告)
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     * @param
     */
    @Override
    public BaseResponse merchantOrder(OrderOperateReqDto orderOperateReqDto) {
        //根据订单号查询订单
        EzOtcOrderMatch orderMatch = orderMatchService.getById(orderOperateReqDto.getMatchOrderNo());
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PENDINGORDER.getCode())) {
            throw new BaseException("订单状态已发生变化");
        }
        if ("1".equals(orderOperateReqDto.getOperate())) {
            //拒绝接受订单
            orderMatch.setStatus(MatchOrderStatus.REFUSE.getCode());
            orderMatchService.updateById(orderMatch);
            return BaseResponse.success();
        }
        //查询匹配到的订单
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderMatch.getOrderMatchNo());

        BigDecimal amount = orderMatch.getAmount();//订单数量

        //查看剩余匹配数量是否满足
        BigDecimal totalAmount = ezOtcOrder.getTotalAmount();
        BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();
        //未匹配的数量
        BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);

        if (amount.compareTo(nuQuotaAmount) > 0) {
            //拒绝接受订单
            orderMatch.setStatus(MatchOrderStatus.ORDERBEENCANCELLED.getCode());
            orderMatchService.updateById(orderMatch);
            return BaseResponse.error("订单数量不足 已取消接受定单");
        }
        //修改订单状态
        orderMatch.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());

        //增加商家匹配数量
        ezOtcOrder.setQuotaAmount(quotaAmount.add(amount));
        baseMapper.updateById(ezOtcOrder);
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
        IPage<EzOtcOrder> page=new Page<EzOtcOrder>(orderQueryReqDto.getPage(),orderQueryReqDto.getLimit());
        LambdaQueryWrapper<EzOtcOrder> otcOrderLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //广告类型(0:买  1：卖)
        String type = orderQueryReqDto.getType();
        CheckException.checkNotEmpty(type, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getType,type);
        });
        //币种名
        String coinName = orderQueryReqDto.getCoinName();
        CheckException.checkNotEmpty(coinName, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getCoinName,coinName);
        });
        //国际货币代码
        String currencyCode = orderQueryReqDto.getCurrencyCode();
        CheckException.checkNotEmpty(currencyCode, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getCurrencyCode,currencyCode);
        });
        //支付方式
        String payMethodId = orderQueryReqDto.getPayMethodId();
        CheckException.checkNotEmpty(payMethodId, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getPaymentMethod1,payMethodId).or()
                    .eq(EzOtcOrder::getPaymentMethod2,payMethodId).or()
                    .eq(EzOtcOrder::getPaymentMethod3,payMethodId);
        });
        //是否为接单广告
        String isAdvertising = orderQueryReqDto.getIsAdvertising();
        CheckException.checkNotEmpty(isAdvertising, () -> {
            otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getIsAdvertising,isAdvertising);
        });

        //支付方式
        String transactionAmount = orderQueryReqDto.getTransactionAmount();//交易数量
        CheckException.checkNotEmpty(transactionAmount, () -> {
            otcOrderLambdaQueryWrapper.ge(EzOtcOrder::getMinimumLimit,transactionAmount);//大于最小即可
        });
        //按照价格降序排列
        otcOrderLambdaQueryWrapper.orderByDesc(EzOtcOrder::getPrice);
        otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getStatus,"0");//订单状态（0：正常 1：已下架）
        IPage<EzOtcOrder> iPage = baseMapper.selectPage(page, otcOrderLambdaQueryWrapper);

        //根据商铺id查询商铺
        List<EzOtcOrder> records = iPage.getRecords();

        //查询所有支付方式
        List<EzPaymentMethod> list = paymentMethodService.list();

        List<OtcOrderRespDto> otcOrderRespDtos=new ArrayList<>();
        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper=new LambdaQueryWrapper<>();
        records.forEach(e->{
            OtcOrderRespDto otcOrderRespDto = new OtcOrderRespDto();
            otcOrderRespDto.setCoinName(e.getCoinName());
            otcOrderRespDto.setOrderNo(e.getOrderNo());
            otcOrderRespDto.setMinimumLimit(e.getMinimumLimit());
            otcOrderRespDto.setMaximumLimit(e.getMaximumLimit());
            otcOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));
            businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId,e.getUserId());
            EzAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
            otcOrderRespDto.setAdvertisingName(one.getAdvertisingName());
            List<String> icons=new ArrayList<>();
            if (StringUtils.isNotNull(e.getPaymentMethod1())){
                icons.add(list.get(e.getPaymentMethod1()-1).getIcon());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod2())){
                icons.add(list.get(e.getPaymentMethod2()-1).getIcon());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod3())){
                icons.add(list.get(e.getPaymentMethod3()-1).getIcon());
            }
            otcOrderRespDto.setIcons(icons);
            otcOrderRespDto.setUserId(e.getUserId());
            otcOrderRespDto.setTotalCount(one.getSellCount()+one.getBuyCount());
            otcOrderRespDto.setMouthFinishRate(one.getMouthFinishRate());
            otcOrderRespDto.setPrice(e.getPrice());
            otcOrderRespDtos.add(otcOrderRespDto);
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
        ArrayList<NewOrderRespDto> newOrderRespDtos = new ArrayList<>();
        IPage<EzOtcOrder> page=new Page<EzOtcOrder>(pageQuery.getPage(),pageQuery.getLimit());
        LambdaQueryWrapper<EzOtcOrder> otcOrderLambdaQueryWrapper=new LambdaQueryWrapper<>();
        otcOrderLambdaQueryWrapper.eq(EzOtcOrder::getStatus,"0");//订单状态（0：正常 1：已下架）
        otcOrderLambdaQueryWrapper.orderByDesc(EzOtcOrder::getCoinName);
        baseMapper.selectPage(page,otcOrderLambdaQueryWrapper).getRecords().forEach(e->{
            NewOrderRespDto newOrderRespDto = new NewOrderRespDto();
            newOrderRespDto.setOrderNo(e.getOrderNo());
            newOrderRespDto.setCoinName(e.getCoinName());
            newOrderRespDto.setType(e.getType());
            newOrderRespDto.setPrice(e.getPrice());
            newOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));
            newOrderRespDtos.add(newOrderRespDto);
        });
        return ResponseList.success(newOrderRespDtos);
    }






    /**
     * @Description: 购买查询订单详情
     * @Param: [otcOrderNo]
     * @return: com.ezcoins.response.Response<com.ezcoins.project.otc.entity.resp.NewOrderRespDto>
     * @Author: Wanglei
     * @Date: 2021/6/22
     * @param otcOrderNo
     */
    @Override
    public Response<OrderInfo> orderInfo(String otcOrderNo) {
        EzOtcOrder ezOtcOrder = baseMapper.selectById(otcOrderNo);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setPrice(ezOtcOrder.getPrice());
        orderInfo.setTradingTips(ezOtcOrder.getTradingTips());
        //查询所有支付方式
        List<EzPaymentMethod> list = paymentMethodService.list();

        List<String> icons=new ArrayList<>();
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod1())){
            icons.add(list.get(ezOtcOrder.getPaymentMethod1()-1).getIcon());
        }
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod2())){
            icons.add(list.get(ezOtcOrder.getPaymentMethod2()-1).getIcon());
        }
        if (StringUtils.isNotNull(ezOtcOrder.getPaymentMethod3())){
            icons.add(list.get(ezOtcOrder.getPaymentMethod3()-1).getIcon());
        }
        orderInfo.setCoinName(ezOtcOrder.getCoinName());
        orderInfo.setLastAmount(ezOtcOrder.getTotalAmount().subtract(ezOtcOrder.getQuotaAmount()));
        orderInfo.setMaximumLimit(ezOtcOrder.getMaximumLimit());
        orderInfo.setMinimumLimit(ezOtcOrder.getMinimumLimit());

        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper=new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId,ezOtcOrder.getUserId());
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(businessLambdaQueryWrapper);
        orderInfo.setAdvertisingName(one.getAdvertisingName());
        orderInfo.setIcons(icons);
        return Response.success(orderInfo);
    }


//    private void preJudge(String userId, String merchantId) {
//       EzUser user = userService.getById(userId);
//        if (UserKycStatus.NOTCERTIFIED.getCode().equals(user.getKycStatus())) {
//            throw new BaseException("请完成kyc认证");
//        }
//        Example example = new Example(OtcOrder.class);
//
//        example.createCriteria().andEqualTo("userId", userId);
//        var orders = mapper.selectByExample(example);
//        var count = orders.stream().filter(x -> {
//            return (x.getStatus() == null || x.getStatus().equals(Constant.UNPAY) || x.getStatus().equals(Constant.PENDING) || x.getStatus().equals(Constant.PAYED));
//        }).count();
//        if (count > 0) return OtcInfo.builder().status(201).data("存在尚未完成的订单,请忽重复下单").build();
//        return null;
//    }
//


}
