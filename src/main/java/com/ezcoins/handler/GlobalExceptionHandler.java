package com.ezcoins.handler;


import com.ezcoins.base.BaseException;

import com.ezcoins.constant.interf.HttpStatus;
import com.ezcoins.exception.CustomException;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.security.auth.login.AccountExpiredException;
import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理器
 * 
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public BaseResponse baseException(BaseException e){
        return BaseResponse.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public BaseResponse businessException(CustomException e){
        if (StringUtils.isNull(e.getCode()))
        {
            return BaseResponse.error(e.getMessage());
        }
        return BaseResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseResponse handlerNoFoundException(Exception e){
        log.error(e.getMessage(), e);
        return BaseResponse.error(HttpStatus.NOT_FOUND, MessageUtils.message("path.not.exist"));//路径不存在，请检查路径是否正确
    }

    @ExceptionHandler(AccessDeniedException.class)
    public BaseResponse handleAuthorizationException(AccessDeniedException e){
        log.error(e.getMessage());
        return BaseResponse.error(HttpStatus.FORBIDDEN, MessageUtils.message("no.permission"));//没有权限，请联系管理员授权
    }

    @ExceptionHandler(AccountExpiredException.class)
    public BaseResponse handleAccountExpiredException(AccountExpiredException e){
        log.error(e.getMessage(), e);
        return BaseResponse.error(e.getMessage());
    }

//    @ExceptionHandler(UsernameNotFoundException.class)
//    public BaseResponse handleUsernameNotFoundException(UsernameNotFoundException e){
//        log.error(e.getMessage(), e);
//        return BaseResponse.error(e.getMessage());
//    }

    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public BaseResponse validatedBindException(BindException e)
    {
        log.error(e.getMessage());
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return BaseResponse.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e){
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return BaseResponse.error(message);
    }

}
