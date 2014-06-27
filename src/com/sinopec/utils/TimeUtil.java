package com.sinopec.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-29 Time: 下午3:30 To
 * change this template use File | Settings | File Templates.
 */
public class TimeUtil {

	private static String timeFormats = "yyyy-MM-dd HH:mm:ss";

	private static SimpleDateFormat sdf_s = new SimpleDateFormat(timeFormats);

	public static String getString() {
		return sdf_s.format(new Date());
	}
}
