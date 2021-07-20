package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.otc.PaymentMethod;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.service.EzUserKycService;

import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.entity.req.PaymentQrcodeTypeReqDto;
import com.ezcoins.project.otc.mapper.EzPaymentInfoMapper;
import com.ezcoins.project.otc.service.EzPaymentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
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
public class EzPaymentInfoServiceImpl extends ServiceImpl<EzPaymentInfoMapper, EzPaymentInfo> implements EzPaymentInfoService {

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
    public BaseResponse alipayPaymentMethod(PaymentQrcodeTypeReqDto qrcodeTypeReqDto) {
        if (qrcodeTypeReqDto.getPaymentMethodId() == PaymentMethod.BANK.getCode()) {
            if (StringUtils.isEmpty(qrcodeTypeReqDto.getBankName())) {
                return BaseResponse.error(MessageUtils.message("银行名称不能为空"));
            }
        } else {
            if (StringUtils.isEmpty(qrcodeTypeReqDto.getPaymentQrCode())) {
                return BaseResponse.error(MessageUtils.message("请先上传支付二维码"));
            }
        }
        String userId = ContextHandler.getUserId();
        String realName = qrcodeTypeReqDto.getRealName();
        EzUserKyc one = kycService.getOneApprove(userId);
        if (null == one) {
            throw new BaseException(MessageUtils.message("用户实名认证尚未通过"));
        }
//        if (!one.getFirstName().equals(firstName) ||!one.getLastName().equals(lastName)) {
//            throw new BaseException(MessageUtils.message("请输入认证的真实姓名"));
//        }
        String id = qrcodeTypeReqDto.getId();
        EzPaymentInfo paymentInfo = new EzPaymentInfo();
        BeanUtils.copyBeanProp(paymentInfo, qrcodeTypeReqDto);
        paymentInfo.setUserId(userId);
        if (StringUtils.isEmpty(id)) {//添加
            //查看是否纯在此支付方式
            LambdaQueryWrapper<EzPaymentInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(EzPaymentInfo::getUserId, userId);
            lambdaQueryWrapper.eq(EzPaymentInfo::getPaymentMethodId, qrcodeTypeReqDto.getPaymentMethodId());
            Integer integer = baseMapper.selectCount(lambdaQueryWrapper);
            if (integer != 0) {
                return BaseResponse.error(MessageUtils.message("每种支付方式只能上传一种"));
            }
            baseMapper.insert(paymentInfo);
        } else {//修改
            baseMapper.updateById(paymentInfo);
        }
        return BaseResponse.success();
    }
}