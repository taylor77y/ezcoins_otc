package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.constant.enums.coin.CoinStatus;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.Account;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.coin.mapper.AccountMapper;
import com.ezcoins.project.coin.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.coin.service.TypeService;
import com.ezcoins.redis.CacheUtils;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.SpringUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 资产账户表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private TypeService typeService;

    @Autowired
    private CacheUtils cacheUtils;

    /**
     * 创建用户 【资金账户】
     */
    private List<Account> processCoinAccount(String userId) throws AccountOperationBusyException {
        LambdaQueryWrapper<Type> typeQueryWrapper = new LambdaQueryWrapper<>();
        typeQueryWrapper.eq(Type::getStatus, CoinStatus.ENABLE.getCode());
        List<Type> coinList = typeService.list(typeQueryWrapper);//查询到所有启用的币种

        LambdaQueryWrapper<Account> accountQueryWrapper = new LambdaQueryWrapper<>();
        accountQueryWrapper.eq(Account::getUserId, userId);
        List<Account> accountList = baseMapper.selectList(accountQueryWrapper);//查询用户的账户列表


        if (StringUtils.isNotEmpty(coinList)) {
            if (StringUtils.isEmpty(accountList) || accountList.size() < coinList.size()) {//用户没有账户或者账户数量小于币种数量
                if (accountList == null) {
                    accountList = new ArrayList<>();
                }
                String lockName = "userLock:" + userId;
                boolean isLock = cacheUtils.getLock(lockName, CacheUtils.LOCK_WAITTIME_SECONDS);//获得锁
                try {
                    if (isLock) { //获取锁成功
                        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) SpringUtils
                                .getBean("transactionManager");
                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        TransactionStatus status = transactionManager.getTransaction(def);
                        try {
                            for (Type coin : coinList) {  //遍历启用的币种
                                accountQueryWrapper.eq(Account::getCoinId, coin.getId());
                                Account account = baseMapper.selectOne(accountQueryWrapper);//通过用户id和币种id查询账户

                                if (account == null && CoinStatus.ENABLE.getCode().equals(coin.getStatus())) {/** 币种状态（0启用 1禁用 ） */
                                    Date d = DateUtils.getNowDate();
                                    account = new Account();
                                    //封装新账户数据
                                    account.setUserId(userId);
                                    account.setCoinId(coin.getId());
                                    account.setCoinName(coin.getCoinName());
                                    account.setCreateTime(d);
                                    account.setUpdateTime(d);
                                    baseMapper.insert(account);//添加账户数据
                                    accountList.add(account);//将创建好的账户放入集合
                                }
                            }
                            transactionManager.commit(status); //提交事务
                            return accountList; //返回账户列表
                        } catch (Exception e) {
                            e.printStackTrace();
                            transactionManager.rollback(status); //事务回滚
                            throw e;
                        }
                    }
                    throw new AccountOperationBusyException();
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (isLock) {
                        cacheUtils.releaseLock(lockName); //释放锁
                    }
                }
            } else {
                return accountList;
            }
        }
        return new ArrayList<Account>(); //无启用币种时候
    }


    @Override
    public List<AccountRespDto> coinAccountListByUserId(String userId) {
        List<Account> accounts = processCoinAccount(userId);
        return BeanUtils.copyListProperties(accounts, AccountRespDto::new);
    }
}