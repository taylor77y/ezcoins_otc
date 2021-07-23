package com.ezcoins.project.coin.service;

import com.ezcoins.project.coin.entity.UserWalletAddr;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.coin.entity.req.UserAddrReqDto;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
public interface UserWalletAddrService extends IService<UserWalletAddr> {

    /***
    * @Description: 增加提币地址
    * @Param: [addrReqDto]
    * @return: com.ezcoins.response.Response
    * @Author: Wanglei
    * @Date: 2021/7/8
    */
    Response addOrUpdateWithdrawalAddr(UserAddrReqDto addrReqDto);

    /**
     * 提币地址列表
     * @param rechargeConfigId
     * @return
     */
    ResponseList<UserWalletAddr> withdrawalAddrList(String withdrawalConfigId);
}
