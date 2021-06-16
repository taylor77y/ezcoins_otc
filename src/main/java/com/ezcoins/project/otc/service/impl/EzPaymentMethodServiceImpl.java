package com.ezcoins.project.otc.service.impl;

import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.project.otc.entity.req.PaymentMethodReqDto;
import com.ezcoins.project.otc.mapper.EzPaymentMethodMapper;
import com.ezcoins.project.otc.service.EzPaymentMethodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Service
public class EzPaymentMethodServiceImpl extends ServiceImpl<EzPaymentMethodMapper, EzPaymentMethod> implements EzPaymentMethodService {

    /***
     * @Description: 添加/修改  收款方式
     * @Param: [paymentMethodReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/16
     * @param paymentMethodReqDto
     */
    @Override
    public void addOrUpdatePaymentMethod(PaymentMethodReqDto paymentMethodReqDto) {
        Integer id = paymentMethodReqDto.getId();
        EzPaymentMethod ezPaymentMethod = new EzPaymentMethod();
        BeanUtils.copyBeanProp(ezPaymentMethod,paymentMethodReqDto);

        if (null==id){//添加
            baseMapper.insert(ezPaymentMethod);
        }else {
            baseMapper.updateById(ezPaymentMethod);
        }
    }
}
