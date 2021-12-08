package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.constant.SysOrderConstants;
import com.ezcoins.constant.SysTipsConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.manager.AsyncManager;
import com.ezcoins.manager.factory.AsyncFactory;
import com.ezcoins.project.otc.entity.EzOtcOrderAppeal;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.entity.req.AppealReqDto;
import com.ezcoins.project.otc.entity.req.DoAppealReqDto;
import com.ezcoins.project.otc.entity.req.DoOrderReqDto;
import com.ezcoins.project.otc.mapper.EzOtcOrderAppealMapper;
import com.ezcoins.project.otc.service.EzOtcOrderAppealService;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.response.Response;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 订单申诉 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
@Service
public class EzOtcOrderAppealServiceImpl extends ServiceImpl<EzOtcOrderAppealMapper, EzOtcOrderAppeal> implements EzOtcOrderAppealService {
    @Autowired
    private EzOtcOrderMatchService matchService;

    /**
     * 订单申诉
     *
     * @param appealReqDto
     * @return
     */
    @Override
    @Transactional(value="transactionManager1", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response appeal(AppealReqDto appealReqDto) {
        //通过订单号查询到订单
        EzOtcOrderMatch orderMatch = matchService.getById(appealReqDto.getOrderMatchNo());
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PAID.getCode())) {
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        String userId = ContextHandler.getUserId();
        //查询用户是否已申诉
        LambdaQueryWrapper<EzOtcOrderAppeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzOtcOrderAppeal::getOrderMatchNo, appealReqDto.getOrderMatchNo());
        lambdaQueryWrapper.eq(EzOtcOrderAppeal::getStatus, "1");
        lambdaQueryWrapper.eq(EzOtcOrderAppeal::getUserId, userId);
        if (baseMapper.selectCount(lambdaQueryWrapper) > 0) {
            return Response.error(MessageUtils.message("用户已申诉，等待处理"));
        }
        EzOtcOrderAppeal ezOtcOrderAppeal = new EzOtcOrderAppeal();
        BeanUtils.copyBeanProp(ezOtcOrderAppeal, appealReqDto);
        ezOtcOrderAppeal.setCreateBy(ContextHandler.getUserName());
        ezOtcOrderAppeal.setUserId(userId);
        baseMapper.insert(ezOtcOrderAppeal);
        //修改订单申诉状态
        if ("1".equals(orderMatch.getIsAppeal())) {
            orderMatch.setIsAppeal("0");
            orderMatch.setStatus(MatchOrderStatus.APPEALING.getCode());
            matchService.updateById(orderMatch);
        }
        String toUserId = userId.equals(orderMatch.getUserId()) ? orderMatch.getOtcOrderUserId() : orderMatch.getUserId();
        String toName = userId.equals(orderMatch.getUserId()) ? orderMatch.getAdvertisingName() : orderMatch.getMatchAdvertisingName();
        AsyncManager.me().execute(AsyncFactory.StationLetter(toUserId,
                SysTipsConstants.TipsType.APPEALING, orderMatch.getOrderNo(), toName));
        //给于对方和自己信号
        AsyncManager.me().execute(AsyncFactory.sendSysChat(userId, toUserId, orderMatch.getOrderMatchNo(),
                SysOrderConstants.SysChatMsg.APPEALING, MatchOrderStatus.APPEALING));
        return Response.success();
    }

    /**
     * 取消申诉
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response cancelAppeal(String id) {
        String userId=ContextHandler.getUserId();
        //通过id查询到申诉信息
        EzOtcOrderAppeal ezOtcOrderAppeal = baseMapper.selectById(id);
        if (!"1".equals(ezOtcOrderAppeal.getStatus())) {
            return Response.error(MessageUtils.message("申诉状态已改变"));
        }
        String orderMatchNo = ezOtcOrderAppeal.getOrderMatchNo();
        //查看订单的申诉
        EzOtcOrderMatch orderMatch = matchService.getById(orderMatchNo);
        if (!orderMatch.getStatus().equals(MatchOrderStatus.APPEALING.getCode())){
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        LambdaQueryWrapper<EzOtcOrderAppeal> queryWrapper = Wrappers.<EzOtcOrderAppeal>lambdaQuery()
                .eq(EzOtcOrderAppeal::getOrderMatchNo, orderMatchNo)
                .and(wq -> wq.eq(EzOtcOrderAppeal::getStatus, "1")
                        .or().eq(EzOtcOrderAppeal::getStatus, "3")
                        .or().eq(EzOtcOrderAppeal::getStatus, "4"));
        Integer integer = baseMapper.selectCount(queryWrapper);
        if (integer == 1) {
            orderMatch.setStatus(MatchOrderStatus.PAID.getCode());
            matchService.updateById(orderMatch);
            String toUserId = userId.equals(orderMatch.getUserId()) ? orderMatch.getOtcOrderUserId() : orderMatch.getUserId();
            String toName = userId.equals(orderMatch.getUserId()) ? orderMatch.getAdvertisingName() : orderMatch.getMatchAdvertisingName();

            AsyncManager.me().execute(AsyncFactory.StationLetter(toUserId,
                    SysTipsConstants.TipsType.APPEAL_OFF, orderMatch.getOrderNo(), toName));

            AsyncManager.me().execute(AsyncFactory.sendSysChat(orderMatch.getUserId(), orderMatch.getOtcOrderUserId(), orderMatchNo,
                    SysOrderConstants.SysChatMsg.APPEAL_OFF, MatchOrderStatus.PAID));
        }
        ezOtcOrderAppeal.setStatus("2");
        baseMapper.updateById(ezOtcOrderAppeal);
        return Response.success();
    }

    /**
     * 处理投诉
     *
     * @param doAppealReqDto
     * @return
     */
    @Override
    public Response doAppeal(DoAppealReqDto doAppealReqDto) {
        //判断申诉状态
        EzOtcOrderAppeal ezOtcOrderAppeal = baseMapper.selectById(doAppealReqDto.getId());
        if (!"1".equals(ezOtcOrderAppeal.getStatus())) {
            return Response.error("申诉状态已发生变化");
        }
        ezOtcOrderAppeal.setStatus(doAppealReqDto.getStatus());
        ezOtcOrderAppeal.setExamineBy(ContextHandler.getUserName());
        ezOtcOrderAppeal.setMemo(doAppealReqDto.getMemo());
        baseMapper.updateById(ezOtcOrderAppeal);
        return Response.success();
    }

    /**
     * 处理投诉订单
     *
     * @param orderReqDto
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response doOrder(DoOrderReqDto orderReqDto) {
        //根据订单号查询是否还有未处理完的投诉
        String orderMatchNo = orderReqDto.getOrderMatchNo();
        LambdaQueryWrapper<EzOtcOrderAppeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderAppeal::getOrderMatchNo, orderMatchNo);
        queryWrapper.eq(EzOtcOrderAppeal::getStatus, "1");
        Integer integer = baseMapper.selectCount(queryWrapper);
        if (integer > 0) {
            return Response.error("请先处理完当前订单的申诉");
        }
        if ("0".equals(orderReqDto.getStatus())) {//放行
            matchService.sellerPut(orderMatchNo, true);
        } else {//订单取消
            matchService.paymentFail(orderMatchNo);
        }
        return Response.success();
    }
}
