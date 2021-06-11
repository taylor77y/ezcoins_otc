package com.ezcoins.exception;

import com.ezcoins.base.BaseException;
import com.ezcoins.utils.StringUtils;

public class CheckException {

    public static void ex(String message) {
        throw new BaseException(message);
    }


    public static void check(boolean flag, OnOperate onOperate) {
        if (flag) {
            onOperate.operate();
        }
    }

    public static void check(boolean flag, String message) {
        if (flag) {
            throw new BaseException(message);
        }
    }


    public static void checkDb(int flag, OnOperate onOperate) {
        if (flag != 1) {
            onOperate.operate();
        }
    }

    public static void checkDb(int flag, String message) {
        if (flag != 1) {
            throw new BaseException(message);
        }
    }



    public static void checkNull(Object flag, OnOperate onOperate) {
        if (flag == null) {
            onOperate.operate();
        }
    }

    public static void checkNull(Object flag, String message) {
        if (flag == null) {
            throw new BaseException(message);
        }
    }


    public static void checkNotNull(Object flag, OnOperate onOperate) {
        if (flag != null) {
            onOperate.operate();
        }
    }

    public static void checkNotNull(Object flag, String message) {
        if (flag != null) {
            throw new BaseException(message);
        }
    }



    public static void checkEmpty(CharSequence flag, OnOperate onOperate) {
        if (StringUtils.isEmpty(flag)) {
            onOperate.operate();
        }
    }

    public static void checkEmpty(CharSequence flag, String message) {
        if (StringUtils.isEmpty(flag)) {
            throw new BaseException(message);
        }
    }



    public static void checkNotEmpty(CharSequence flag, OnOperate onOperate) {
        if (StringUtils.isNotEmpty(flag)) {
            onOperate.operate();
        }
    }

    public static void checkNotEmpty(CharSequence flag, String message) {
        if (StringUtils.isNotEmpty(flag)) {
            throw new BaseException(message);
        }
    }


    public interface OnOperate {
        void operate();
    }
}
