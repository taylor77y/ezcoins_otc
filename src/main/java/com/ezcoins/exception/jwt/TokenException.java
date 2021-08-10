package com.ezcoins.exception.jwt;

import com.ezcoins.base.BaseException;

public class TokenException extends BaseException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException() {
        super(null,"401120","登录已失效，请重新登录",null);
    }
}
