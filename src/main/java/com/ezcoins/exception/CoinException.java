package com.ezcoins.exception;

import com.ezcoins.base.BaseException;

/**
 * 资金账户基础异常类
 * 
 * @author lv
 */
public class CoinException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public CoinException(String code,String defaultMessage, Object[] args){
        super("coin", code, defaultMessage, args);
    }
}
