package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.otc.PaymentMethod;
import com.ezcoins.constant.enums.user.KycStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.otc.entity.EzUserAlipay;
import com.ezcoins.project.otc.entity.EzUserWechat;
import com.ezcoins.project.otc.entity.req.AlipayWechatReqDto;
import com.ezcoins.project.otc.mapper.EzUserAlipayMapper;
import com.ezcoins.project.otc.service.EzUserAlipayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.service.EzUserWechatService;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 支付宝信息 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@Service
public class EzUserAlipayServiceImpl extends ServiceImpl<EzUserAlipayMapper, EzUserAlipay> implements EzUserAlipayService {

    @Autowired
    private EzUserKycService kycService;

    @Autowired
    private EzUserWechatService wechatService;


    /**
     * @param alipayWechatReqDto
     * @Description: 添加/修改 支付宝收款方式
     * @Param: [alipayReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/15
     */
    @Override
    public void alipayPaymentMethod(AlipayWechatReqDto alipayWechatReqDto) {
        String realName = alipayWechatReqDto.getRealName();

        EzUserKyc one = kycService.getOneApprove(ContextHandler.getUserId());
        if (null == one) {
            throw new BaseException(MessageUtils.message("用户实名认证尚未通过"));
        }
        if (!one.getRealName().equals(realName)) {
            throw new BaseException(MessageUtils.message("请输入认证的真实姓名"));
        }
        String id = alipayWechatReqDto.getId();
        String type = alipayWechatReqDto.getType();
        if (type.equals(PaymentMethod.ALIPAY.getCode())){
            EzUserAlipay ezUserAlipay = new EzUserAlipay();
            BeanUtils.copyBeanProp(ezUserAlipay, alipayWechatReqDto);
            if (StringUtils.isEmpty(id)){//添加
                baseMapper.insert(ezUserAlipay);
            }else {//修改
                baseMapper.updateById(ezUserAlipay);
            }
        }else if (type.equals(PaymentMethod.WECHAT.getCode())){
            EzUserWechat ezUserWechat = new EzUserWechat();
            BeanUtils.copyBeanProp(ezUserWechat, alipayWechatReqDto);
            if (StringUtils.isEmpty(id)){//添加
                wechatService.save(ezUserWechat);
            }else {//修改
                wechatService.updateById(ezUserWechat);
            }
        }
    }
}