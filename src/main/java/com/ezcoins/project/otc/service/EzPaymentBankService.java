package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import com.ezcoins.project.otc.entity.req.UserBankCardAddrReqDto;
import com.ezcoins.project.otc.entity.resp.PaymentBankRespDto;
import com.ezcoins.response.Response;

import java.util.List;

public interface EzPaymentBankService extends IService<EzPaymentBank> {

    /***
     * @Description: 添加/修改 用户银行卡信息
     * @Param: [bankCardReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/8
     */
    Response addOrUpdateUserBankCard(UserBankCardAddrReqDto bankCardReqDto);

    /***
     * @Description: 修改用户 银行卡 状态
     * @Param: [bankCardReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/8
     */
    Response updateUserBankCardStatus(UserBankCardAddrReqDto bankCardReqDto);

    /**
     * 用户银行卡列表
     * @param userId
     * @return
     */
    List<PaymentBankRespDto> userBankCardList(String userId);

}
