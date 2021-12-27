package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzInternetAccount;
import com.ezcoins.project.otc.entity.req.UserInternetAccountReqDto;
import com.ezcoins.project.otc.entity.resp.InternetAccountRespDto;
import com.ezcoins.project.otc.mapper.EzInternetAccountMapper;
import com.ezcoins.project.otc.service.EzInternetAccountService;
import com.ezcoins.response.Response;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EzInternetAccountServiceImpl extends ServiceImpl<EzInternetAccountMapper, EzInternetAccount> implements EzInternetAccountService {

    /***
     * @Description: 添加/修改 网络账号信息
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/3
     * @param internetAccountReqDto
     */
    @Override
    public Response addOrUpdateInternetAccount(UserInternetAccountReqDto internetAccountReqDto) {
        String userId = ContextHandler.getUserId();
        EzInternetAccount internetAccount = new EzInternetAccount();
        BeanUtils.copyBeanProp(internetAccount,internetAccountReqDto);
        internetAccount.setUserId(userId);
        if (StringUtils.isEmpty(internetAccountReqDto.getId())){
//            userWalletAddr.setCreateBy(userId);
            baseMapper.insert(internetAccount);
        }else {
//            userWalletAddr.setUpdateBy(userId);
            baseMapper.updateById(internetAccount);
        }
        return Response.success();
    }


    /***
     * @Description: 修改 网络账号状态
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/3
     * @param internetAccountReqDto
     */
    @Override
    public Response updateUserInternetAccountStatus(UserInternetAccountReqDto internetAccountReqDto) {
        String userId = ContextHandler.getUserId();
        EzInternetAccount internetAccount = new EzInternetAccount();
        BeanUtils.copyBeanProp(internetAccount,internetAccountReqDto);
//        internetAccount.setUserId(userId);
        if (StringUtils.isNotEmpty(internetAccountReqDto.getId())){
//            userWalletAddr.setUpdateBy(userId);
            baseMapper.updateById(internetAccount);
        }
        return Response.success();
    }


    /**
     * 用户 网络账号列表
     *
     * @param
     * @return
     */
    @Override
    public List<InternetAccountRespDto> internetAccountList(String userId) {
        LambdaQueryWrapper<EzInternetAccount> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(EzPaymentBank::getBankName,withdrawalConfigId);
        lambdaQueryWrapper.eq(EzInternetAccount::getUserId, ContextHandler.getUserId());
        List<EzInternetAccount> lists = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtils.copyListProperties(lists, InternetAccountRespDto::new);
    }
}
