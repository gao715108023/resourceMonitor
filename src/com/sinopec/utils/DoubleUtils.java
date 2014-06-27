package com.sinopec.utils;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-12 Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class DoubleUtils {

	public static double convert(double value) {
		double ret = (double) (Math.round(value * 100)) / 100.0;
		return ret;
	}
}
