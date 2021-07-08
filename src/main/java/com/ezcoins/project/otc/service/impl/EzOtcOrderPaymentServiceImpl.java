package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.project.otc.entity.EzOtcOrderPayment;
import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.mapper.EzOtcOrderPaymentMapper;
import com.ezcoins.project.otc.service.EzOtcOrderPaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.service.EzPaymentInfoService;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-02
 */
@Service
public class EzOtcOrderPaymentServiceImpl extends ServiceImpl<EzOtcOrderPaymentMapper, EzOtcOrderPayment> implements EzOtcOrderPaymentService {


    @Autowired
    private EzPaymentInfoService paymentInfoService;

    /**
     * 存入支付信息
     *
     * @param paymentMethod1
     * @param paymentMethod2
     * @param paymentMethod3
     */
    @Override
    public List<EzOtcOrderPayment> depositPayment(Integer paymentMethod1, Integer paymentMethod2, Integer paymentMethod3,String userId,String orderNo,String orderMatchNo) {
        LambdaQueryWrapper<EzPaymentInfo> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(EzPaymentInfo::getUserId, userId);
        queryWrapper1.eq(EzPaymentInfo::getPaymentMethodId, paymentMethod1).or().
                eq(EzPaymentInfo::getPaymentMethodId, paymentMethod2).or().
                eq(EzPaymentInfo::getPaymentMethodId, paymentMethod3);
        List<EzPaymentInfo> list = paymentInfoService.list(queryWrapper1);
        if (list.size()==0){
            throw new BaseException(null, "801", null, "未匹配到支付方式");
        }
        List<EzOtcOrderPayment> list1 = new ArrayList<EzOtcOrderPayment>();
        list.forEach(e -> {
            EzOtcOrderPayment ezOtcOrderPayment = new EzOtcOrderPayment();
            BeanUtils.copyBeanProp(ezOtcOrderPayment, e);
            if (StringUtils.isNotEmpty(orderNo)){
                ezOtcOrderPayment.setType("0");
                ezOtcOrderPayment.setOrderNo(orderNo);
            }else {
                ezOtcOrderPayment.setType("1");
                ezOtcOrderPayment.setOrderMatchNo(orderMatchNo);
            }
            list1.add(ezOtcOrderPayment);
        });

        this.saveBatch(list1);
        if (StringUtils.isNotEmpty(orderMatchNo)){
            return list1;
        }else {
            return null;
        }
    }
}
