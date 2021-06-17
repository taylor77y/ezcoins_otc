package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.coin.WithdrawOrderStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.WithdrawOrder;
import com.ezcoins.project.coin.entity.req.CheckWithdrewOrderReqDto;
import com.ezcoins.project.coin.mapper.WithdrawOrderMapper;
import com.ezcoins.project.coin.service.WithdrawOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.beans.beancontext.BeanContext;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Service
@Slf4j
public class WithdrawOrderServiceImpl extends ServiceImpl<WithdrawOrderMapper, WithdrawOrder> implements WithdrawOrderService {

    /***
     * @Description: 审核提币 订单
     * @Param: [checkWithdrewOrderReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/17
     * @param checkWithdrewOrderReqDto
     */
    @Override
    public void reviewWithdrawOrder(CheckWithdrewOrderReqDto checkWithdrewOrderReqDto) {
        String userName = ContextHandler.getUserName();
        if (!checkWithdrewOrderReqDto.getOperate().equals(WithdrawOrderStatus.PENDINGREVIEW.getCode())){
            throw new BaseException("该提币订单审核失败");
        } else  if (checkWithdrewOrderReqDto.getOperate().equals(WithdrawOrderStatus.BY.getCode())){
                //TODO: 去优盾处理
            log.info("后台管理员 {}： 审核通过",userName);

        }else if (checkWithdrewOrderReqDto.getOperate().equals(WithdrawOrderStatus.REFUSE.getCode())){
            log.info("后台管理员 {}： 审核失败,原因：{}",userName,checkWithdrewOrderReqDto.getReason());
        }
        WithdrawOrder withdrawOrder = baseMapper.selectById(checkWithdrewOrderReqDto.getId());
        //修改订单状态
        withdrawOrder.setStatus(checkWithdrewOrderReqDto.getOperate());
        withdrawOrder.setReason(checkWithdrewOrderReqDto.getReason());
        withdrawOrder.setUpdateBy(userName);
        baseMapper.updateById(withdrawOrder);
    }
}
