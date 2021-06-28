package com.ezcoins.project.coin.mapper;

import com.ezcoins.project.coin.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 资产余额表 Mapper 接口
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
public interface AccountMapper extends BaseMapper<Account> {

    int balanceChange(String id, BigDecimal available, BigDecimal frozen, BigDecimal lockup, Integer version, Date d);
}
