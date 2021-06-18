package com.ezcoins.project.coin.service;

import com.ezcoins.project.coin.entity.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;

import java.util.List;

/**
 * <p>
 * 资产余额表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
public interface AccountService extends IService<Account> {


    List<AccountRespDto> coinAccountListByUserId(String userId);
}
