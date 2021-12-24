package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzInternetAccount;
import com.ezcoins.project.otc.entity.req.UserInternetAccountReqDto;
import com.ezcoins.project.otc.entity.resp.InternetAccountRespDto;
import com.ezcoins.response.Response;

import java.util.List;

public interface EzInternetAccountService extends IService<EzInternetAccount> {

    /***
     * @Description: 增加用户 网络账号
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: Wanglei
     * @Date: 2021/7/8
     */
    Response addOrUpdateInternetAccount(UserInternetAccountReqDto internetAccountReqDto);


    /***
     * @Description: 增加用户 网络账号
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: Wanglei
     * @Date: 2021/7/8
     */
    Response updateUserInternetAccountStatus(UserInternetAccountReqDto internetAccountReqDto);

    /**
     * 用户 网络账号列表
     * @param userId
     * @return
     */
    List<InternetAccountRespDto> internetAccountList(String userId);
}
