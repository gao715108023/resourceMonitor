package com.sinopec.agent.infocollect;

import com.sinopec.agent.share.SharedArea;
import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午2:26
 * To change this template use File | Settings | File Templates.
 */
public class DynamicProcessInfo implements Runnable {

	private static final Log LOG = LogFactory.getLog(DynamicProcessInfo.class);

	private int samplingInterval;

	private volatile boolean running = true;

	public DynamicProcessInfo(int samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	public void getProcessInfoFromProc() {
		FileReader reader = null;
		BufferedReader br = null;
		try {
			float oneMinsProcs = 0;
			float fiveMInsProcs = 0;
			float fifteenMinsProcs = 0;
			reader = new FileReader("/proc/loadavg");
			br = new BufferedReader(reader);
			String str = null;
			while ((str = br.readLine()) != null) {
				String a[] = str.split(" ");
				oneMinsProcs = Float.parseFloat(a[0]);
				fiveMInsProcs = Float.parseFloat(a[1]);
				fifteenMinsProcs = Float.parseFloat(a[2]);
			}
			SharedArea.dynamicInfoSerializable.setOneMinsProcs(oneMinsProcs);
			SharedArea.dynamicInfoSerializable.setFiveMinsProcs(fiveMInsProcs);
			SharedArea.dynamicInfoSerializable.setFifteenMinsProcs(fifteenMinsProcs);
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
			getProcessInfoFromProc();
			try {
				Thread.sleep(samplingInterval);
			} catch (InterruptedException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
		}
	}
}
