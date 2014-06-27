package com.sinopec.utils;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-31 Time: 上午9:33 To
 * change this template use File | Settings | File Templates.
 */
public class FloatUtils {

	public static float convert(float value) {
		return (float) ((Math.round(value * 100)) / 100.0);
	}
}
