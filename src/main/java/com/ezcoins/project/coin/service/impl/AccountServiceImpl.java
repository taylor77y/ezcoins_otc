package com.ezcoins.project.coin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.constant.enums.coin.CoinStatus;
import com.ezcoins.exception.coin.AccountBalanceNotEnoughException;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.Account;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.mapper.AccountMapper;
import com.ezcoins.project.coin.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.coin.service.TypeService;
import com.ezcoins.redis.CacheUtils;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.SpringUtils;
import com.ezcoins.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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



    @Override
    public Account getAccountByUserIdAndCoinId(String userId, String coinId) throws AccountOperationBusyException {
        LambdaQueryWrapper<Account> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Account::getCoinId,coinId);
        lambdaQueryWrapper.eq(Account::getUserId,userId);
        Account account = baseMapper.selectOne(lambdaQueryWrapper);

        if (account == null) {
            //没有查到就去创建
            List<Account> accountList = processCoinAccount(userId);
            if (StringUtils.isNotEmpty(accountList)) {
                for (Account a : accountList) {
                    if (a.getCoinId().equals(coinId)) {
                        return a;
                    }
                }
            }
            return null;
        }
        return account;
    }
//
//
//    @Override
//    public boolean balanceChangeSYNC(List<BalanceChange> cList) throws AccountBalanceNotEnoughException, AccountOperationBusyException {
//        if (StringUtils.isEmpty(cList)) {
//            return false;// 无操作
//        }
//        for (BalanceChange c : cList) {
//            // 判断参数
//            if (StringUtils.isNull(c.getCoinId()) || StringUtils.isNull(c.getUserId())
//                    || StringUtils.isNull(c.getMainType())) {
//                log.error("参数异常{}", JSON.toJSON(c));
//                return false;
//            }
//            if (StringUtils.isNull(c.getAvailable()) && StringUtils.isNull(c.getFrozen())
//                    && StringUtils.isNull(c.getLockup())) {
//                log.error("参数异常{}", JSON.toJSON(c));
//                return false;
//            }
//
//            Account acc = getAccountByUserIdAndCoinId(c.getUserId(), c.getCoinId()); //通过用户id和币种id查询账户
//
//            if (StringUtils.isNull(acc)) {
//                log.error("参数异常-{},找到不到账户", JSON.toJSON(c));
//                return false;
//            }
//            c.setCoinName(acc.getCoinName());
//            log.info("操作用户ID[{}]币种ID[{}-{}]余额数量[{}]冻结数量[{}]锁仓数量[{}],操作主类型[{}],操作子类型[{}]", c.getUserId(), c.getCoinId(), c.getCoinName(),
//                    c.getAvailable(), c.getFrozen(), c.getLockup(), c.getMainType().getRemark(), c.getSonType());
//
//            if (StringUtils.isNotNull(c.getAvailable()) && c.getAvailable().compareTo(BigDecimal.ZERO) != 0) {//判断 操作剩余金额
//                if (acc.getAvailable().add(c.getAvailable()).compareTo(BigDecimal.ZERO) == -1) {
//                    throw new AccountBalanceNotEnoughException();
//                } else {
//                    acc.setAvailable(acc.getAvailable().add(c.getAvailable()));
//                }
//            }
//
//            if (StringUtils.isNotNull(c.getFrozen()) && c.getFrozen().compareTo(BigDecimal.ZERO) != 0) {//判断 操作冻结金额
//                if (acc.getFrozen().add(c.getFrozen()).compareTo(BigDecimal.ZERO) == -1) {
//                    throw new AccountBalanceNotEnoughException();
//                } else {
//                    acc.setFrozen(acc.getFrozen().add(c.getFrozen()));
//                }
//            }
//
//            if (StringUtils.isNotNull(c.getLockup()) && c.getLockup().compareTo(BigDecimal.ZERO) != 0) { //判断 操作锁仓金额
//                if (acc.getLockup().add(c.getLockup()).compareTo(BigDecimal.ZERO) == -1) {
//                    throw new AccountBalanceNotEnoughException();
//                } else {
//                    acc.setLockup(acc.getLockup().add(c.getLockup()));
//                }
//            }
//            c.setCoinName(acc.getCoinName());
//            boolean isLock = cacheUtils.getAccountLock(acc.getId(), CacheUtils.LOCK_WAITTIME_SECONDS);//获得锁
//            try {
//                Date d = DateUtils.getNowDate();
//                if (!isLock || coinAccountMapper.balanceChange(acc.getId(), acc.getAvailable(), acc.getFrozen(), //没有获取到锁或者没有更新成功
//                        acc.getLockup(), acc.getVersion(), d) <= 0) {
//                    throw new AccountOperationBusyException();
//                } else {
//                    if (MainType.FROZEN.equals(c.getMainType()) || MainType.UNFREEZE.equals(c.getMainType())
//                            || MainType.LOCKUP.equals(c.getMainType()) || MainType.UNLOCK.equals(c.getMainType())) {
//                        // 冻结/解冻/锁仓/解锁 不生成资产流水
//                    }else if (MainType.RECHARGE.equals(c.getMainType()) || MainType.WITHDRAWAL.equals(c.getMainType())) {
//                        // 充值/提现 单独生成资产流水
//                    }else {
//                        CoinRecord rec = new CoinRecord();
//                        rec.setUserId(c.getUserId());
//                        rec.setCoinId(c.getCoinId());
//                        rec.setCoinName(c.getCoinName());
//                        rec.setCreateTime(d);
//                        rec.setFee(BigDecimal.ZERO);
//                        rec.setMemo(c.getMemo());
//                        rec.setIncomeType(c.getIncomeType().getType());
//                        rec.setMainType(c.getMainType().getType());
//                        rec.setSonType(c.getSonType());
//                        rec.setStatus(RecordStatus.OK.getStatus());
//                        rec.setAmount(c.getAvailable());
//
//                        coinRecordService.insertCoinRecord(rec);
//                    }
//                }
//            } catch (Exception e) {
//                throw e;
//            } finally {
//                if (isLock) {
//                    cacheUtils.releaseAccountLock(acc.getId());
//                }
//            }
//        }
//        return true;
//    }


}