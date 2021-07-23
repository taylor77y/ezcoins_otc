package com.ezcoins.exception.file;


import com.ezcoins.base.BaseException;

/**
 * 文件信息异常类
 * 
 * 
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String defaultMessage, Object[] args)
    {
        super("file", null, defaultMessage, args);
    }

}
