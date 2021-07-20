package com.ezcoins.exception.jwt;

public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException() {
        super("登录已失效，请重新登录");
    }
}
