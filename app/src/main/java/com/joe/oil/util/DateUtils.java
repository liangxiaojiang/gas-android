package com.joe.oil.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.ParseException;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "HH:mm", "HH:mm:ss" };

	private static Calendar calS = Calendar.getInstance();

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern, Locale.ENGLISH);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString(), Locale.ENGLISH);
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd", Locale.ENGLISH);
		}
		return formatDate;
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 * 
	 * @throws java.text.ParseException
	 */
	public static Date parseDate(Object str) throws java.text.ParseException {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static String getDiffString(Date startTime, Date endTime) {

		if (startTime == null || endTime == null) {
			return "";
		}

		long nd = DateUtils.MILLIS_PER_DAY;// 一天的毫秒数
		long nh = DateUtils.MILLIS_PER_HOUR;// 一小时的毫秒数
		long nm = DateUtils.MILLIS_PER_MINUTE;// 一分钟的毫秒数
		long ns = DateUtils.MILLIS_PER_SECOND;// 一秒钟的毫秒数

		long diff = endTime.getTime() - startTime.getTime();
		long day = diff / nd;// 计算差多少天
		long hour = diff % nd / nh;// 计算差多少小时
		long min = diff % nd % nh / nm;// 计算差多少分钟
		long sec = diff % nd % nh % nm / ns;// 计算差多少秒

		StringBuilder sb = new StringBuilder();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (min > 0) {
			sb.append(min + "分");
		}
		if (sec >= 0) {
			sb.append(sec + "秒");
		}

		return sb.toString();
	}

	public static int getInterval(String timeOne, String timeTwo) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(timeOne);
		} catch (java.text.ParseException e1) {
			e1.printStackTrace();
		}
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);

		Calendar c2 = Calendar.getInstance();
		Date date2 = null;
		try {
			date2 = format.parse(timeTwo);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		c2.setTime(date2);
		return (int) (c2.getTimeInMillis() - c1.getTimeInMillis());
	}

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		// System.out.println(formatDate(parseDate("2010/3/6")));
		// System.out.println(getDate("yyyy年MM月dd日 E"));
		// long time = new Date().getTime()-parseDate("2012-11-19").getTime();
		// System.out.println(time/(24*60*60*1000));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse("2014-10-28 16:10:12");
		} catch (java.text.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);

		Calendar c2 = Calendar.getInstance();
		Date date2 = null;
		try {
			date2 = format.parse("2014-10-28 16:10:16");
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c2.setTime(date2);
		System.out.println((c2.getTimeInMillis() - c1.getTimeInMillis()));
	}

}
