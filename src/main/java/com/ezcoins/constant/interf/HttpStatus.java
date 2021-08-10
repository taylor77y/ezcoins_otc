package com.ezcoins.constant.interf;


public interface HttpStatus{
    
    public static final int HTTP_RES_CODE_200 = 200;

    public static final int HTTP_RES_CODE_500 = 500;
    // 响应请求成功
    String HTTP_RES_CODE_200_VALUE = "success";
    // 系统错误
    String HTTP_RES_CODE_500_VALUE = "fail";
    
    public static final int CREATED = 201;

    
    public static final int ACCEPTED = 202;

    
    public static final int NO_CONTENT = 204;

    
    public static final int MOVED_PERM = 301;

    
    public static final int SEE_OTHER = 303;

    
    public static final int NOT_MODIFIED = 304;

    
    public static final int BAD_REQUEST = 400;

    
    public static final int UNAUTHORIZED = 401;

    
    public static final int FORBIDDEN = 403;

    
    public static final int NOT_FOUND = 404;



    public static final int BAD_METHOD = 405;

    
    public static final int CONFLICT = 409;

    
    public static final int UNSUPPORTED_TYPE = 415;

    public static final int NOT_IMPLEMENTED = 501;
}
