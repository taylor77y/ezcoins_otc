package com.ezcoins.exception;


import com.ezcoins.utils.MessageUtils;

public class CommonException extends RuntimeException {
    public CommonException(String message) {
        super(MessageUtils.message(message));
    }
    public CommonException() {
        super(MessageUtils.message("非法操作"));
    }
}
