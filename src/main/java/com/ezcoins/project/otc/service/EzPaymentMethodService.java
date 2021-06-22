package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.PaymentMethodReqDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
public interface EzPaymentMethodService extends IService<EzPaymentMethod> {

    /***
    * @Description: 添加/修改  收款方式
    * @Param: [paymentMethodReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/16
    */
    void addOrUpdatePaymentMethod(PaymentMethodReqDto paymentMethodReqDto);
}
