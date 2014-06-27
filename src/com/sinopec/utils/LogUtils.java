package com.sinopec.utils;

import com.sinopec.common.Constants;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-12 Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class LogUtils {

	public static String getTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

	public static boolean enableDEBUG() {
		return Constants.isDebug == 1;
	}
}
