package com.sinopec.agent.infocollect;

import com.sinopec.agent.share.SharedArea;
import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午2:17
 * To change this template use File | Settings | File Templates.
 */
public class DynamicSwapInfo implements Runnable {

	private static final Log LOG = LogFactory.getLog(DynamicSwapInfo.class);

	private int samplingInterval;

	private volatile boolean running = true;

	public DynamicSwapInfo(int samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	public void getSwapInfoFromProc() {
		// int totalMem = 0;
		// int freeMem = 0;
		int totalSwap = 0;
		int freeSwap = 0;
		int usedSwap = 0;
		// int usedMem = 0;
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader("/proc/meminfo");
			br = new BufferedReader(reader);
			String str = null;

			while ((str = br.readLine()) != null) {
				StringBuilder sbb = new StringBuilder();
				char c = ' ';
				for (int i = 0; i < str.length(); i++) {
					char ch = str.charAt(i);
					if (ch != c) {
						sbb.append(ch);
					}
				}
				str = sbb.toString();
				String a[] = str.split(":");
				// if (a[0].equals("MemTotal")) {
				// Matcher m = Pattern.compile("\\d+").matcher(a[1]);
				// while (m.find()) {
				// totalMem = totalMem + Integer.parseInt(m.group(0));
				// totalMem = totalMem / 1024;
				// }
				// }
				// if (a[0].equals("MemFree")) {
				// Matcher m = Pattern.compile("\\d+").matcher(a[1]);
				// while (m.find()) {
				// freeMem = freeMem + Integer.parseInt(m.group(0));
				// freeMem = freeMem / 1024;
				// }
				// }
				if (a[0].equals("SwapTotal")) {
					Matcher m = Pattern.compile("\\d+").matcher(a[1]);
					while (m.find()) {
						totalSwap = totalSwap + Integer.parseInt(m.group(0));
						totalSwap = totalSwap / 1024;
					}
				}
				if (a[0].equals("SwapFree")) {
					Matcher m = Pattern.compile("\\d+").matcher(a[1]);
					while (m.find()) {
						freeSwap = freeSwap + Integer.parseInt(m.group(0));
						freeSwap = freeSwap / 1024;
					}
				}
				usedSwap = totalSwap - freeSwap;
				// usedMem = totalMem - freeMem;
			}
			// dynamicInfoSerializable.setTotalMemory(totalMem);
			// dynamicInfoSerializable.setUsedMemory(usedMem);
			// dynamicInfoSerializable.setIdleMemory(freeMem);
			SharedArea.dynamicInfoSerializable.setSwapSize(totalSwap);
			SharedArea.dynamicInfoSerializable.setUsedSwap(usedSwap);
			SharedArea.dynamicInfoSerializable.setIdleSwap(freeSwap);
		} catch (FileNotFoundException e) {
			LOG.error(LogUtils.getTrace(e));
		} catch (IOException e) {
			LOG.error(LogUtils.getTrace(e));
		} finally {
			try {
				if (br != null)
					br.close();
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(LogUtils.getTrace(e));
			}
		}
	}

	@Override
	public void run() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		while (running) {
			getSwapInfoFromProc();
			try {
				Thread.sleep(samplingInterval);
			} catch (InterruptedException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
		}
	}
}
