package com.ezcoins.project.coin.service.impl;

import com.ezcoins.project.coin.entity.Wallet;
import com.ezcoins.project.coin.mapper.WalletMapper;
import com.ezcoins.project.coin.service.WalletService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 钱包地址表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

}
