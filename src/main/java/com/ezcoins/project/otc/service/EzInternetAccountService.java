package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzInternetAccount;
import com.ezcoins.project.otc.entity.req.UserInternetAccountReqDto;
import com.ezcoins.project.otc.entity.resp.InternetAccountRespDto;
import com.ezcoins.response.Response;

import java.util.List;

public interface EzInternetAccountService extends IService<EzInternetAccount> {

    /***
     * @Description: 添加/修改 网络账号信息
     * @Param: [internetAccountReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/8
     */
    Response addOrUpdateInternetAccount(UserInternetAccountReqDto internetAccountReqDto);


    /***
     * @Description: 修改 网络账号状态
     * @Param: [internetAccountReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/8
     */
    Response updateUserInternetAccountStatus(UserInternetAccountReqDto internetAccountReqDto);

    /**
     * 用户 网络账号列表
     * @param userId
     * @return List<InternetAccountRespDto>
     */
    List<InternetAccountRespDto> internetAccountList(String userId);
}
