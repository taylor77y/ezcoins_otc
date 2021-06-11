package com.ezcoins.utils;
import java.math.BigDecimal;

/**
* 重写BigDecimal的求和方法，避免BigDecimal对象为null的时，报空指针的情况
*/
public class BigDecimalUtils {
	public static BigDecimal ifNullSetZero(BigDecimal value) {
		if (value != null) {
			return value;
		} else {
			return BigDecimal.ZERO;
		}
	}
	public static BigDecimal sum(BigDecimal ...value){
		BigDecimal result = BigDecimal.ZERO;
		for (int i = 0; i < value.length; i++){
			result = result.add(ifNullSetZero(value[i]));
		}
		return result;
	}
}
