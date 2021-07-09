package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.UserWalletAddr;
import com.ezcoins.project.coin.entity.req.UserAddrReqDto;
import com.ezcoins.project.coin.mapper.UserWalletAddrMapper;
import com.ezcoins.project.coin.service.UserWalletAddrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
@Service
public class UserWalletAddrServiceImpl extends ServiceImpl<UserWalletAddrMapper, UserWalletAddr> implements UserWalletAddrService {

    /***
     * @Description: 增加提币地址
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/7/8
     * @param addrReqDto
     */
    @Override
    public BaseResponse addOrUpdateWithdrawalAddr(UserAddrReqDto addrReqDto) {
        String userId = ContextHandler.getUserId();
        UserWalletAddr userWalletAddr = new UserWalletAddr();
        BeanUtils.copyBeanProp(userWalletAddr,addrReqDto);
        userWalletAddr.setUserId(userId);
        if (StringUtils.isEmpty(addrReqDto.getId())){
            userWalletAddr.setCreateBy(userId);
            baseMapper.insert(userWalletAddr);
        }else {
            userWalletAddr.setUpdateBy(userId);
            baseMapper.updateById(userWalletAddr);
        }
        return BaseResponse.success();
    }

    /**
     * 提币地址列表
     *
     * @param rechargeConfigId
     * @return
     */
    @Override
    public ResponseList<UserWalletAddr> withdrawalAddrList(String rechargeConfigId) {
        LambdaQueryWrapper<UserWalletAddr> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserWalletAddr::getWithdrawalConfigId,rechargeConfigId);
        lambdaQueryWrapper.eq(UserWalletAddr::getUserId,ContextHandler.getUserId());
        return ResponseList.success(baseMapper.selectList(lambdaQueryWrapper));
    }
}
