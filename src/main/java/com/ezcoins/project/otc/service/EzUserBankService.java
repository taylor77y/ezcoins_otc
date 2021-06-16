package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzUserBank;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.BankReqDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
public interface EzUserBankService extends IService<EzUserBank> {

    /***
    * @Description: 添加/修改 银行收款方式
    * @Param: [bankReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/15
    */
    void bankPaymentMethod(BankReqDto bankReqDto);
}
