package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import com.ezcoins.project.otc.entity.req.UserBankCardAddrReqDto;
import com.ezcoins.project.otc.entity.resp.PaymentBankRespDto;
import com.ezcoins.response.Response;

import java.util.List;

public interface EzPaymentBankService extends IService<EzPaymentBank> {

    /***
     * @Description: 增加提币地址
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: Wanglei
     * @Date: 2021/7/8
     */
    Response addOrUpdateUserBankCard(UserBankCardAddrReqDto bankCardReqDto);

    /**
     * 提币地址列表
     * @param userId
     * @return
     */
    List<PaymentBankRespDto> userBankCardList(String userId);
}
