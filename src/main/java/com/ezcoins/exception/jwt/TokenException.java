package com.ezcoins.exception.jwt;

public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException() {
        super("token失效，请重新登录");
    }
}
