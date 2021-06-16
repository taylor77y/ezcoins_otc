package com.ezcoins.project.otc.service.impl;

import com.ezcoins.base.BaseException;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.service.EzUserKycService;

import com.ezcoins.project.otc.entity.EzPaymentQrcode;
import com.ezcoins.project.otc.entity.req.PaymentQrcodeTypeReqDto;
import com.ezcoins.project.otc.mapper.EzPaymentQrcodeMapper;
import com.ezcoins.project.otc.service.EzPaymentQrcodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@Service
public class EzPaymentQrcodeServiceImpl extends ServiceImpl<EzPaymentQrcodeMapper, EzPaymentQrcode> implements EzPaymentQrcodeService {

    @Autowired
    private EzUserKycService kycService;

    /**
     * @param qrcodeTypeReqDto
     * @Description: 添加/修改 二维码类型  收款方式
     * @Param: [alipayReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/15
     */
    @Override
    public void alipayPaymentMethod(PaymentQrcodeTypeReqDto qrcodeTypeReqDto) {
        String realName = qrcodeTypeReqDto.getRealName();
        EzUserKyc one = kycService.getOneApprove(ContextHandler.getUserId());
        if (null == one) {
            throw new BaseException(MessageUtils.message("用户实名认证尚未通过"));
        }
        if (!one.getRealName().equals(realName)) {
            throw new BaseException(MessageUtils.message("请输入认证的真实姓名"));
        }

        String id = qrcodeTypeReqDto.getId();
        EzPaymentQrcode paymentQrcode = new EzPaymentQrcode();
        BeanUtils.copyBeanProp(paymentQrcode, qrcodeTypeReqDto);
        if (StringUtils.isEmpty(id)) {//添加
            baseMapper.insert(paymentQrcode);
        } else {//修改
            baseMapper.updateById(paymentQrcode);
        }

    }
}