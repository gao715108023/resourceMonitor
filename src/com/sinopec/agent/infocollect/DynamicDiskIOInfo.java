package com.sinopec.agent.infocollect;

import com.sinopec.agent.share.SharedArea;
import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午2:22
 * To change this template use File | Settings | File Templates.
 */
public class DynamicDiskIOInfo implements Runnable {

	private static final Log LOG = LogFactory.getLog(DynamicDiskIOInfo.class);

	private int samplingInterval;

	private volatile boolean running = true;

	public DynamicDiskIOInfo(int samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	public void getDiskIOInfoByCmd() {
		double ioTs = 0; // MEDIUMINT
		double readSpeed = 0; // MEDIUMINT
		double readKB = 0; // MEDIUMINT
		double writeSpeed = 0; // MEDIUMINT
		double writeKB = 0; // MEDIUMINT
		List<String> diskNameList = getDiskNameByFile();
		String commond = "iostat -d -k";
		BufferedInputStream bis = null;
		BufferedReader br = null;
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec(commond);
			bis = new BufferedInputStream(p.getInputStream());
			br = new BufferedReader(new InputStreamReader(bis));
			String lineStr;
			while ((lineStr = br.readLine()) != null) {
				lineStr = lineStr.trim().replaceAll(" {2,}", " ");
				String[] array = lineStr.split(" ");
				if (diskNameList.contains(array[0])) {
					ioTs += Double.parseDouble(array[1]);
					readSpeed += Double.parseDouble(array[2]);
					writeSpeed += Double.parseDouble(array[3]);
					readKB += Double.parseDouble(array[4]);
					writeKB += Double.parseDouble(array[5]);
				}
			}
			readKB /= 1024;
			writeKB /= 1024;
			try {
				if (p.waitFor() != 0) {
					if (p.exitValue() == 1)
						LOG.error("Command execution failed!");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOG.error(LogUtils.getTrace(e));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(LogUtils.getTrace(e));
		} finally {
			try {
				if (br != null)
					br.close();
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(LogUtils.getTrace(e));
			}
		}
		SharedArea.dynamicInfoSerializable.setIoTs((int) ioTs);
		SharedArea.dynamicInfoSerializable.setReadKB((int) readKB);
		SharedArea.dynamicInfoSerializable.setReadSpeed((int) readSpeed);
		SharedArea.dynamicInfoSerializable.setWriteKB((int) writeKB);
		SharedArea.dynamicInfoSerializable.setWriteSpeed((int) writeSpeed);
	}

	private List<String> getDiskNameByFile() {
		List<String> diskNameList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/proc/partitions"));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					if (line.length() != 0) {
						line = line.trim().replaceAll(" {2,}", " ");
						String[] array = line.split(" ");
						if (isNumeric(array[0])) {
							int i = Integer.parseInt(array[0]);
							int j = Integer.parseInt(array[1]);
							if (i % 8 == 0 && j % 16 == 0) {
								diskNameList.add(array[3]);
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(LogUtils.getTrace(e));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			LOG.error(LogUtils.getTrace(e));
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(LogUtils.getTrace(e));
			}
		}
		return diskNameList;
	}

	private boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		while (running) {
			getDiskIOInfoByCmd();
			try {
				Thread.sleep(samplingInterval);
			} catch (InterruptedException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
		}
	}
}
