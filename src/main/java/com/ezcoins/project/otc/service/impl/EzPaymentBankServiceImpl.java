package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import com.ezcoins.project.otc.entity.req.UserBankCardAddrReqDto;
import com.ezcoins.project.otc.entity.resp.PaymentBankRespDto;
import com.ezcoins.project.otc.mapper.EzPaymentBankMapper;
import com.ezcoins.project.otc.service.EzPaymentBankService;
import com.ezcoins.response.Response;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EzPaymentBankServiceImpl extends ServiceImpl<EzPaymentBankMapper, EzPaymentBank> implements EzPaymentBankService {


    /***
     * @Description: 添加/修改 用户银行卡信息
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/3
     * @param bankCardReqDto
     */
    @Override
    public Response addOrUpdateUserBankCard(UserBankCardAddrReqDto bankCardReqDto) {
        String userId = ContextHandler.getUserId();
        EzPaymentBank paymentBank = new EzPaymentBank();
        BeanUtils.copyBeanProp(paymentBank,bankCardReqDto);
        paymentBank.setUserId(userId);
        if (StringUtils.isEmpty(bankCardReqDto.getId())){
//            userWalletAddr.setCreateBy(userId);
            baseMapper.insert(paymentBank);
        }else {
//            userWalletAddr.setUpdateBy(userId);
            baseMapper.updateById(paymentBank);
        }
        return Response.success();
    }


    /***
     * @Description: 修改用户 银行卡 状态
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/3
     * @param bankCardReqDto
     */
    @Override
    public Response updateUserBankCardStatus(UserBankCardAddrReqDto bankCardReqDto) {
        String userId = ContextHandler.getUserId();
        EzPaymentBank paymentBank = new EzPaymentBank();
        BeanUtils.copyBeanProp(paymentBank,bankCardReqDto);
        paymentBank.setUserId(userId);
        if (StringUtils.isNotEmpty(bankCardReqDto.getId())){
//            userWalletAddr.setCreateBy(userId);
            baseMapper.updateById(paymentBank);
        }
        return Response.success();
    }

    /**
     * 提币地址列表
     *
     * @param
     * @return
     */
    @Override
    public List<PaymentBankRespDto> userBankCardList(String userId) {
        LambdaQueryWrapper<EzPaymentBank> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(EzPaymentBank::getBankName,withdrawalConfigId);
        lambdaQueryWrapper.eq(EzPaymentBank::getUserId,ContextHandler.getUserId());
        List<EzPaymentBank> lists = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtils.copyListProperties(lists, PaymentBankRespDto::new);
    }
}

