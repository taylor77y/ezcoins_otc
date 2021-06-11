package com.ezcoins.exception.user;


import com.ezcoins.base.BaseException;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户信息异常类
 * 
 * 
 */
@Slf4j
public class UserException extends BaseException{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args){
        super("user", code, args, null);
        log.info(code,args);
    }
}
