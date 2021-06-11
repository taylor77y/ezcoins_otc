package com.ezcoins.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 获取随机数
 * 
 * @author qianyi
 *
 */
public class RandomUtil {

	private static final Random random = new Random();

	private static final DecimalFormat fourdf = new DecimalFormat("0000");

	private static final DecimalFormat sixdf = new DecimalFormat("000000");

	public static String getFourBitRandom() {
		return fourdf.format(random.nextInt(10000));
	}

	public static String getSixBitRandom() {
		return sixdf.format(random.nextInt(1000000));
	}

	/**
	 * 给定数组，抽取n个数据
	 * @param list
	 * @param n
	 * @return
	 */
	public static ArrayList getRandom(List list, int n) {

		Random random = new Random();

		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

		// 生成随机数字并存入HashMap
		for (int i = 0; i < list.size(); i++) {

			int number = random.nextInt(100) + 1;

			hashMap.put(number, i);
		}

		// 从HashMap导入数组
		Object[] robjs = hashMap.values().toArray();

		ArrayList r = new ArrayList();

		// 遍历数组并打印数据
		for (int i = 0; i < n; i++) {
			r.add(list.get((int) robjs[i]));
			System.out.print(list.get((int) robjs[i]) + "\t");
		}
		System.out.print("\n");
		return r;
	}

	/**
	 * 生成数字和字母组合，字母区分大小写
	 *
	 * @param length 随机字符串的长度
	 * @return
	 */
	public static String generateByRandom(final int length) {
		StringBuilder randomSb = new StringBuilder(length);
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			// 输出字母还是数字
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 字符串
			if ("char".equals(charOrNum)) {
				// 判断字母大小写
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				randomSb = randomSb.append((char) (choice + random.nextInt(26)));
			} else {
				randomSb = randomSb.append(random.nextInt(10));
			}
		}
		return randomSb.toString();
	}






}
