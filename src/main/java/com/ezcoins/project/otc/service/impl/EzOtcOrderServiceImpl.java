package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.interf.IndexOrderNoKey;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.entity.req.OrderOperateReqDto;
import com.ezcoins.project.otc.entity.req.OtcOrderReqDto;
import com.ezcoins.project.otc.entity.req.PlaceOrderReqDto;
import com.ezcoins.project.otc.mapper.EzOtcOrderMapper;
import com.ezcoins.project.otc.service.EzOtcOrderIndexService;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.otc.service.EzOtcOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
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


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto) {
        //获得订单号  国家代码+订单日期
        String currencyCode = otcOrderReqDto.getCurrencyCode();
        LambdaQueryWrapper<EzCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(EzCountryConfig::getCurrencyCode, currencyCode);
        EzCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        if (null == one) {
            throw new BaseException("国家货币代码不存在");
        }
        String countryCode = one.getCountryCode();//国家编号
        String orderNo = orderIndexService.getOrderNo(countryCode, IndexOrderNoKey.ORDER_INFO);

        EzOtcOrder ezOtcOrder = new EzOtcOrder();
        ezOtcOrder.setOrderNo(orderNo);
        BeanUtils.copyBeanProp(ezOtcOrder, otcOrderReqDto);

        //存入新的广告
        baseMapper.insert(ezOtcOrder);

        //将卖出订单金额冻结
        BigDecimal totalAmount = otcOrderReqDto.getTotalAmount();


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
    public BaseResponse placeAnOrder(PlaceOrderReqDto placeOrderReqDto) {
        String userId = ContextHandler.getUserId();
        //查询当前用户是否被取消订单是否超过规定数量
        int count = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);

        //获取otc配置每天能取消的次数 5
        if (count > 5) {//5后面从配置数据库得到
            throw new BaseException("你今天取消次数超过上线,每天再来");
        }

        //查看用户是否有未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getUserId, userId);
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER)
                .or()
                .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT);

        EzOtcOrderMatch orderMatch = orderMatchService.getOne(matchLambdaQueryWrapper);
        if (null != orderMatch) {
            return BaseResponse.error("请先完成当前未完成的订单").data("matchOrderNo", orderMatch.getOrderMatchNo());//将订单号返回
        }
        //通过订单号查询到购买的订单
        String orderNo = placeOrderReqDto.getOrderNo();
        EzOtcOrder ezOtcOrder = baseMapper.selectById(orderNo);


        BigDecimal amount = placeOrderReqDto.getAmount();
        BigDecimal maximumLimit = ezOtcOrder.getMaximumLimit();
        BigDecimal minimumLimit = ezOtcOrder.getMinimumLimit();

        BigDecimal totalAmount = ezOtcOrder.getTotalAmount();
        BigDecimal quotaAmount = ezOtcOrder.getQuotaAmount();
        //未匹配的数量
        BigDecimal nuQuotaAmount = totalAmount.subtract(quotaAmount);

        BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
        //判断数量是否满足
        if (amount.compareTo(maximumLimit) > 0 || amount.compareTo(minimumLimit) < 0) {
            throw new BaseException("输入数量不满足条件范围");
        }
        //判断冻结数量/未匹配的数量  是否大于购买数量
        if (frozeAmount.compareTo(amount) < 0 || nuQuotaAmount.compareTo(amount) < 0) {
            throw new BaseException("订单已发生改变");
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

        //查看  订单类型(0:买  1：卖)
        EzOtcOrderMatch match = new EzOtcOrderMatch();//封装订单
        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNo(countryCode, IndexOrderNoKey.ORDER_MATCH_INFO);
        match.setOrderMatchNo(orderMatchNo);
        match.setOrderNo(ezOtcOrder.getOrderNo());
        match.setUserId(userId);
        match.setOtcOrderUserId(ezOtcOrder.getUserId());
        match.setAmount(placeOrderReqDto.getAmount());
        match.setPrice(ezOtcOrder.getPrice());
        match.setTotalPrice(ezOtcOrder.getPrice().multiply(placeOrderReqDto.getAmount()));
        match.setCoinName(ezOtcOrder.getCoinName());

        //订单到期时间=当前时间+付款期限(分钟)   根据用户设置的订单付款期限
        Integer prompt = ezOtcOrder.getPrompt();//付款期限(分钟)
        match.setDueTime(DateUtils.getBeForeTime(prompt));

        //查看订单是否为接单广告
        String isAdvertising = ezOtcOrder.getIsAdvertising();
        if ("1".equals(isAdvertising)) {//不为接单广告
            match.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode()); //不是接单广告直接进入待支付状态
            //TODO:将订单存入rabbitmq进行死信通信  时间到了就取消订单 根据卖家用户设置而定
            //增加商家匹配数量
            ezOtcOrder.setQuotaAmount(quotaAmount.add(placeOrderReqDto.getAmount()));
            baseMapper.updateById(ezOtcOrder);
        } else {
            match.setStatus(MatchOrderStatus.PENDINGORDER.getCode()); //接单广告直接进入商家待接单状态  如果此模式下 用户取消订单免除每天取消限制的
            //TODO: 将订单存入rabbitmq进行死信通信  时间到了就取消订单  接单时间根据otc设置而定

        }
        orderMatchService.save(match);

        //增加订单匹配数量


        //返回订单号
        return BaseResponse.success().data("matchOrderNo", match.getOrderMatchNo());//将订单号返回
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

        BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
        //TODO: 将冻结数量返回商户的资产中

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
            orderMatch.setStatus(MatchOrderStatus.ORDERBEENCANCELLED.getCode());
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

























//    private void preJudge(String userId, String merchantId) {
////        EzUser user = userService.getById(userId);
////        if (UserKycStatus.NOTCERTIFIED.getCode().equals(user.getKycStatus())) {
////            throw new BaseException("请完成kyc认证");
////        }
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
