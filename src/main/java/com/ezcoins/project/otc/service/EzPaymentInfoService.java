package com.ezcoins.project.otc.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.entity.req.PaymentQrcodeTypeReqDto;
import com.ezcoins.response.Response;

/**
 * <p>
 * 用户 支付宝信息 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
public interface EzPaymentInfoService extends IService<EzPaymentInfo> {




    /**
    * @Description: 添加/修改 支付宝收款方式
    * @Param: [alipayReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/15
    */
    Response alipayPaymentMethod(PaymentQrcodeTypeReqDto qrcodeTypeReqDto);
}
