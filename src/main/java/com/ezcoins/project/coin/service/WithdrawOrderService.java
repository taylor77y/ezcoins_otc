package com.ezcoins.project.coin.service;

import com.ezcoins.project.coin.entity.WithdrawOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.coin.entity.req.CheckWithdrewOrderReqDto;
import com.ezcoins.project.coin.entity.req.WithdrawReqDto;
import com.ezcoins.response.Response;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
public interface WithdrawOrderService extends IService<WithdrawOrder> {

    /*** 
    * @Description: 审核提币 订单
    * @Param: [checkWithdrewOrderReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/17
    */
    void reviewWithdrawOrder(CheckWithdrewOrderReqDto checkWithdrewOrderReqDto);


    /**
     * 发起提现
     * @param withdrawReqDto
     * @return
     */
    Response withdraw(WithdrawReqDto withdrawReqDto);
}
