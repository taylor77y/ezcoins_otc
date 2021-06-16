package com.ezcoins.project.coin.service.impl;

import com.ezcoins.project.coin.entity.Account;
import com.ezcoins.project.coin.mapper.AccountMapper;
import com.ezcoins.project.coin.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 资产余额表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

}
