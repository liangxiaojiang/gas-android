package net.safetone.rfid.utils;

import java.util.Locale;

public class Utils {
	private static final String DIGIT_STR = "0123456789ABCDEF";
	private static final byte[] DIGIT = DIGIT_STR.getBytes();
	
	/**
	 * 将数据转换为16进制表示的可打印ASCII字节数组 (0x5A -> {'5', 'A'})
	 * 
	 * @param rwa 原始数据
	 * @param offset 偏移
	 * @param len 欲转换的长度
	 * 
	 * @return 转换后的结果，16进制表示的可打印字节数组。
	 * 
	 * @see #rawToHex(byte[])
	 * @see #rawToHexString(byte[])
	 */
	public static byte[] rawToHex(byte[] rwa, int offset, int len) {
		byte[] byteArr = new byte[len * 2];
		for (int n = 0; n < len; ++n) {
			byteArr[n * 2] = DIGIT[(rwa[offset+n] & 0xFF) >> 4];
			byteArr[n * 2 + 1] = DIGIT[rwa[offset+n] & 0x0F];
		}
		return byteArr;
	}
	
	/**
	 * 将数据转换为16进制表示的可打印ASCII字节数组 (0x5A -> {'5', 'A'})
	 * <p>
	 * 等同于 rawToHex(raw, 0, raw.length)
	 * 
	 * @param raw 原始数据
	 * 
	 * @return 转换后的结果，16进制表示的可打印字节数组。
	 * 
	 * @see #rawToHex(byte[], int, int)
	 * @see #rawToHexString(byte[])
	 */
	public static byte[] rawToHex(byte[] raw) {
		return rawToHex(raw, 0, raw.length);
	}
	
	/**
	 * 将数据转换为16进制表示的字符串 (0x5A -> "5A")
	 * 
	 * @param raw 原始数据
	 * @param offset 偏移
	 * @param len 欲转换的长度
	 * 
	 * @return 转换后的结果，16进制表示的字符串。
	 * 
	 * @see #rawToHexString(byte[])
	 * @see #rawToHex(byte[], int, int)
	 */
	public static String rawToHexString(byte[] raw, int offset, int len) {
		return new String(rawToHex(raw, offset, len));
	}
	
	/**
	 * 将数据转换为16进制表示的字符串 (0x5A -> "5A")
	 * <p>
	 * 等同于 rawToHexString(raw, 0, raw.length)
	 * 
	 * @param raw 原始数据
	 * 
	 * @return 转换后的结果，16进制表示的字符串。
	 * 
	 * @see #rawToHexString(byte[], int, int)
	 * @see #rawToHex(byte[])
	 */
	public static String rawToHexString(byte[] raw) {
		return new String(rawToHex(raw));
	}
	
	/**
	 * 检查String是不是有效的16进制字符串。
	 * <p>
	 * 偶数个有效的16进制字符。
	 * 
	 * @param str 字符串。
	 * 
	 * @return 是规则的16进制字符串返回true，否则返回false。
	 */
	public static boolean isHexString(String str) {
    	String tmp = str.trim().replace(" ", "").toUpperCase(Locale.US);
    	
    	if (tmp.length() > 1 && tmp.length() % 2 == 0){
    		for(int i = 0; i < tmp.length(); i++)
    			if (!DIGIT_STR.contains(tmp.substring(i, i + 1)))
    				return false;
    		return true;
    	}

    	return false;
	}
	
	/**
	 * 将16进制表示的字符串转换为数据 ("5A" -> 0x5A)
	 * 
	 * @param str 字符串。
	 * 
	 * @return 转换后的结果
	 */
	public static byte[] HexStringToRaw(String str) {
		str = str.trim().replace(" ", "").toUpperCase(Locale.US);

        int len = str.length() / 2;
        byte[] ret = new byte[len];
        
        for (int i = 0; i < len; i++)
            ret[i] = (byte)(Integer.decode("0x"+ str.substring(i * 2, i * 2 + 2)) & 0xFF);

        return ret;
	}
	
	/**
	 * 查看byte字符是不是有效的16进制字符。
	 * 
	 * @param c 字符
	 * 
	 * @return 结果
	 */
	public static boolean isxdigit(byte c) {
		switch (c) {
		case '0': case '1': case '2': case '3': case '4': case '5': case '6': 
		case '7': case '8': case '9': case 'A': case 'B': case 'C': case 'D':
		case 'E': case 'F': case 'a': case 'b': case 'c': case 'd': case 'e': 
		case 'f':
			return true;
		default:
			return false;
		}
	}
}