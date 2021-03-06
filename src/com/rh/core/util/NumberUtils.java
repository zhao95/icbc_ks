package com.rh.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字处理类
 * @author ruaho_hdy
 *
 */
public class NumberUtils {

	/**
     * 格式化指定精度double数值，根据指定取值模式对象
     * @param value 格式化之前数值
     * @param scale 小数位数
     * @param roundingMode 取值模式
     * @return 指定精度double数值
     */
    public static double formatDouble(double value, int scale, RoundingMode roundingMode) {
    	BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }
    
	/**
     * 格式化指定精度double数值，根据指定取值模式
     * @param value 格式化之前数值
     * @param scale 小数位数
     * @param roundingMode 取值模式
     * @return 指定精度double数值
     */
    public static BigDecimal formatDouble(double value, int scale, int roundingMode) {
    	BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        return bd;
    }
    
    /**
     * 格式化指定精度double数值，取值模式为默认，四舍五入
     * @param value 格式化之前数值
     * @param scale 小数位数
     * @return 指定精度double数值
     */
    public static BigDecimal formatDouble(double value, int scale) {
    	BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, BigDecimal.ROUND_UP);
        return bd;
    }
    
    /**
     * 人民币数字向大写转换
     * @param numberVal 数字型
     * @return 大写人民币字符串
     */
    public static String RMBCapital(BigDecimal numberOfMoney) {
		/**
		 * 汉语中数字大写
		 */
		final String[] CN_UPPER_NUMBER = { "零", "壹", "贰", "叁", "肆", "伍", "陆",
				"柒", "捌", "玖" };
		/**
		 * 汉语中货币单位大写，这样的设计类似于占位符
		 */
		final String[] CN_UPPER_MONETRAY_UNIT = { "分", "角", "元", "拾", "佰", "仟",
				"万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟" };
		/**
		 * 特殊字符：整
		 */
		final String CN_FULL = "整";
		/**
		 * 特殊字符：负
		 */
		final String CN_NEGATIVE = "负";
		/**
		 * 金额的精度，默认值为2
		 */
		final int MONEY_PRECISION = 2;
		/**
		 * 特殊字符：零元整
		 */
		final String CN_ZEOR_FULL = "零元" + CN_FULL;
		StringBuffer sb = new StringBuffer();
		// -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
		// positive.
		int signum = numberOfMoney.signum();
		// 零元整的情况
		if (signum == 0) {
			return CN_ZEOR_FULL;
		}
		// 这里会进行金额的四舍五入
		long number = numberOfMoney.movePointRight(MONEY_PRECISION)
				.setScale(0, 4).abs().longValue();
		// 得到小数点后两位值
		long scale = number % 100;
		int numUnit = 0;
		int numIndex = 0;
		boolean getZero = false;
		// 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
		if (!(scale > 0)) {
			numIndex = 2;
			number = number / 100;
			getZero = true;
		}
		if ((scale > 0) && (!(scale % 10 > 0))) {
			numIndex = 1;
			number = number / 10;
			getZero = true;
		}
		int zeroSize = 0;
		while (true) {
			if (number <= 0) {
				break;
			}
			// 每次获取到最后一个数
			numUnit = (int) (number % 10);
			if (numUnit > 0) {
				if ((numIndex == 9) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
				}
				if ((numIndex == 13) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
				}
				sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				getZero = false;
				zeroSize = 0;
			} else {
				++zeroSize;
				if (!(getZero)) {
					sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				}
				if (numIndex == 2) {
					if (number > 0) {
						sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
					}
				} else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				}
				getZero = true;
			}
			// 让number每次都去掉最后一个数
			number = number / 10;
			++numIndex;
		}
		// 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
		if (signum == -1) {
			sb.insert(0, CN_NEGATIVE);
		}
		// 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
		if (!(scale > 0)) {
			sb.append(CN_FULL);
		}
		return sb.toString();
    }
}
