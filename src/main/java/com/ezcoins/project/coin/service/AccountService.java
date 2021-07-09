package com.ezcoins.project.coin.service;

import com.ezcoins.exception.coin.AccountBalanceNotEnoughException;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.coin.entity.req.ReviseAccountReqDto;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.response.BaseResponse;

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

    Account getAccountByUserIdAndCoinId(String userId, String coinName) throws AccountOperationBusyException;

    List<Account> processCoinAccount(String userId,String userName);

    // 同步操作资产[余额/冻结/锁仓]
    boolean balanceChangeSYNC(List<BalanceChange> cList)
            throws AccountBalanceNotEnoughException, AccountOperationBusyException;

    BaseResponse revise(ReviseAccountReqDto reviseAccountReqDto);
}
