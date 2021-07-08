package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcOrderPayment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-02
 */
public interface EzOtcOrderPaymentService extends IService<EzOtcOrderPayment> {

    /**
     * 存入支付信息
     */
    List<EzOtcOrderPayment> depositPayment(Integer paymentMethod1, Integer paymentMethod2, Integer paymentMethod3, String userId, String orderNo, String orderMatchNo);


}
