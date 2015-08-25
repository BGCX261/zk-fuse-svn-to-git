package net.thinkbase.jasperreports.utils;

import java.math.BigDecimal;

/**
 * From 
 * @author thinkbase.net
 *
 */
public class MoneyUtil {

	/** 大写数字 */
	private static final String[] NUMBERS = {
		"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	/** 整数部分的单位 */
	private static final String[] IUNIT_QTY = {
		"点", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟" };
	private static final String[] IUNIT_AMT = {
		"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟" };
	/** 小数部分的单位 */
	private static final String[] DUNIT_QTY = { "", "", "" };
	private static final String[] DUNIT_AMT = { "角", "分", "厘" };

	private String[] IUNIT = null;
	private String[] DUNIT = null;
	
	/**
	 * 得到大写金额
	 * @param num
	 * @return
	 */
	public static String amountToChinese(BigDecimal num) {
		//FIXME: 这里固化了数据的精度
		num = num.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		MoneyUtil mu = new MoneyUtil();
		mu.IUNIT = IUNIT_AMT;
		mu.DUNIT = DUNIT_AMT;
		return mu.toChinese(num.toPlainString());
	}
	/**
	 * 得到大写数量
	 * @param num
	 * @return
	 */
	public static String qtyToChinese(BigDecimal num) {
		//FIXME: 这里固化了数据的精度
		num = num.setScale(2, BigDecimal.ROUND_HALF_UP);

		MoneyUtil mu = new MoneyUtil();
		mu.IUNIT = IUNIT_QTY;
		mu.DUNIT = DUNIT_QTY;
		String s = mu.toChinese(num.toPlainString());
		String prefix ="";
		
		if (num.multiply(new BigDecimal(1000)).intValue() < 1){
			//0.000x, 超出处理能力, 之间返回
			return num.toPlainString();
		}else if (num.multiply(new BigDecimal(100)).intValue() < 1){
			//0.00x
			prefix = "零点零零";
		}else if (num.multiply(new BigDecimal(10)).intValue() < 1){
			//0.0x
			prefix = "零点零";
		}else if (num.intValue() < 1){
			//0.x
			prefix = "零点";
		}
		s = prefix + s;
		if (s.endsWith("点")){
			s = s.substring(0, s.length()-1);
		}
		return s;
	}
	/**
	 * 得到大写金额
	 * @param num
	 */
	private String toChinese(String num) {
		num = num.replaceAll(",", "");// 去掉","
		String integerStr;// 整数部分数字
		String decimalStr;// 小数部分数字

		// 初始化：分离整数部分和小数部分
		if (num.indexOf(".") > 0) {
			integerStr = num.substring(0, num.indexOf("."));
			decimalStr = num.substring(num.indexOf(".") + 1);
		} else if (num.indexOf(".") == 0) {
			integerStr = "";
			decimalStr = num.substring(1);
		} else {
			integerStr = num;
			decimalStr = "";
		}
		// integerStr去掉首0，不必去掉decimalStr的尾0(超出部分舍去)
		if (!integerStr.equals("")) {
			integerStr = Long.toString(Long.parseLong(integerStr));
			if (integerStr.equals("0")) {
				integerStr = "";
			}
		}
		// overflow超出处理能力，直接返回
		if (integerStr.length() > IUNIT.length) {
			return num;
		}

		int[] integers = toArray(integerStr);// 整数部分数字
		boolean isMust5 = isMust5(integerStr);// 设置万单位
		int[] decimals = toArray(decimalStr);// 小数部分数字
		return getChineseInteger(integers, isMust5)
				+ getChineseDecimal(decimals);
	}

	/**
	 * 整数部分和小数部分转换为数组，从高位至低位
	 */
	private int[] toArray(String number) {
		int[] array = new int[number.length()];
		for (int i = 0; i < number.length(); i++) {
			array[i] = Integer.parseInt(number.substring(i, i + 1));
		}
		return array;
	}

	/**
	 * 得到中文金额的整数部分。
	 */
	private String getChineseInteger(int[] integers, boolean isMust5) {
		StringBuffer chineseInteger = new StringBuffer("");
		int length = integers.length;
		for (int i = 0; i < length; i++) {
			// 0出现在关键位置：1234(万)5678(亿)9012(万)3456(元)
			// 特殊情况：10(拾元、壹拾元、壹拾万元、拾万元)
			String key = "";
			if (integers[i] == 0) {
				if ((length - i) == 13)// 万(亿)(必填)
					key = IUNIT[4];
				else if ((length - i) == 9)// 亿(必填)
					key = IUNIT[8];
				else if ((length - i) == 5 && isMust5)// 万(不必填)
					key = IUNIT[4];
				else if ((length - i) == 1)// 元(必填)
					key = IUNIT[0];
				// 0遇非0时补零，不包含最后一位
				if ((length - i) > 1 && integers[i + 1] != 0)
					key += NUMBERS[0];
			}
			chineseInteger.append(integers[i] == 0 ? key
					: (NUMBERS[integers[i]] + IUNIT[length - i - 1]));
		}
		return chineseInteger.toString();
	}

	/**
	 * 得到中文金额的小数部分。
	 */
	private String getChineseDecimal(int[] decimals) {
		StringBuffer chineseDecimal = new StringBuffer("");
		for (int i = 0; i < decimals.length; i++) {
			// 舍去3位小数之后的
			if (i == 3)
				break;
			chineseDecimal.append(decimals[i] == 0 ? ""
					: (NUMBERS[decimals[i]] + DUNIT[i]));
		}
		return chineseDecimal.toString();
	}

	/**
	 * 判断第5位数字的单位"万"是否应加。
	 */
	private boolean isMust5(String integerStr) {
		int length = integerStr.length();
		if (length > 4) {
			String subInteger = "";
			if (length > 8) {
				// 取得从低位数，第5到第8位的字串
				subInteger = integerStr.substring(length - 8, length - 4);
			} else {
				subInteger = integerStr.substring(0, length - 4);
			}
			return Integer.parseInt(subInteger) > 0;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		String[] number = new String[]{
			"1.23", "1234567890123456.123", "0.0798", "10001000.09", "01.107700"
		};
		for (int i = 0; i < number.length; i++) {
			System.out.println(number[i] + " " + MoneyUtil.amountToChinese(new BigDecimal(number[i])));
		}
		for (int i = 0; i < number.length; i++) {
			System.out.println(number[i] + " " + MoneyUtil.qtyToChinese(new BigDecimal(number[i])));
		}
	}

}