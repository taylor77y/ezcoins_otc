package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.project.coin.entity.RechargeConfig;
import com.ezcoins.project.coin.entity.Wallet;
import com.ezcoins.project.coin.mapper.WalletMapper;
import com.ezcoins.project.coin.service.RechargeConfigService;
import com.ezcoins.project.coin.service.WalletService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.coin.udun.Address;
import com.ezcoins.project.coin.udun.BiPayService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 钱包地址表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-25
 */
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    @Autowired
    private RechargeConfigService configService;

    @Autowired
    private BiPayService biPayService;

    @Override
    public BaseResponse rechargeAddress(String userId, String id) {
        String rechargeAddr = null;
        //根据id查询充值地址main_type  coin_type
        RechargeConfig rechargeConfig = configService.getById(id);
        String mainCoinType = rechargeConfig.getMainCoinType();
        //通过用户id 查询钱包列表
        LambdaQueryWrapper<Wallet> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Wallet::getUserId, userId);
        List<Wallet> wallets = baseMapper.selectList(lambdaQueryWrapper);

        List<Wallet> collect = wallets.stream().filter(e -> e.getMainCoinType().equals(mainCoinType)).collect(Collectors.toList());
        if (collect.size()!=0){
            rechargeAddr=collect.get(0).getAddress();
        }else {
            Address coinAddress = biPayService.createCoinAddress(mainCoinType, userId.toString(), "");
            if (StringUtils.isNotNull(coinAddress)){
                Wallet wallet = new Wallet();
                wallet.setAddress(coinAddress.getAddress());
                wallet.setMainCoinType(mainCoinType);
                wallet.setUserId(userId);
                wallet.setWalletType("thirdparty");
                baseMapper.insert(wallet);
            }
        }
        return BaseResponse.success().data("rechargeAddr",rechargeAddr);
    }
}
