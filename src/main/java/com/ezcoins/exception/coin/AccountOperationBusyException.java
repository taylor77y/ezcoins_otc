package com.ezcoins.exception.coin;


import com.ezcoins.exception.CoinException;

/**
 * 资金账户繁忙 异常类
 * 
 */
public class AccountOperationBusyException extends CoinException
{
    private static final long serialVersionUID = 1L;

    /**
     * 资金账户繁忙
     */
    public AccountOperationBusyException()
    {
    	super("coin.busy.error", null);
    }
}
