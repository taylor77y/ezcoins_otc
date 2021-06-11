package com.ezcoins.exception.user;

/**
 * 参数异常类
 * 
 */
public class ParameterException extends UserException
{
    private static final long serialVersionUID = 1L;

    public ParameterException()
    {
        super("system.parameter.exception", null);
    }
}
