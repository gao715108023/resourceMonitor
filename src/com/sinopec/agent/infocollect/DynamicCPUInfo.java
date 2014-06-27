package com.sinopec.agent.infocollect;

import com.sinopec.agent.share.SharedArea;
import com.sinopec.utils.DoubleUtils;
import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-12 Time: 下午3:57
 * To change this template use File | Settings | File Templates.
 */
public class DynamicCPUInfo implements Runnable {

	private static final Log LOG = LogFactory.getLog(DynamicCPUInfo.class);

	private int samplingInterval;

	private volatile boolean running = true;

	public DynamicCPUInfo(int samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	public void convertToCPUInfo() throws IOException {
		float userTime = 0;
		float niceTime = 0;
		float systemTime = 0;
		float iowaitTime = 0;
		float idleTime = 0;
		StringBuffer allUserTime = new StringBuffer();
		StringBuffer allNiceTime = new StringBuffer();
		StringBuffer allSystemTime = new StringBuffer();
		StringBuffer allIowaitTime = new StringBuffer();
		StringBuffer allIdleTime = new StringBuffer();
		Map<String, double[]> mapB = getInfoFromStat();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		Map<String, double[]> mapA = getInfoFromStat();
		Iterator<Map.Entry<String, double[]>> iterA = mapA.entrySet().iterator();
		Iterator<Map.Entry<String, double[]>> iterB = mapB.entrySet().iterator();
		while (iterA.hasNext() && iterB.hasNext()) {
			Map.Entry<String, double[]> entryA = iterA.next();
			Map.Entry<String, double[]> entryB = iterB.next();
			if (entryA.getKey().equals("cpu")) {
				double buffer[] = calculateCPUInfo(entryA, entryB);
				userTime = (float) buffer[0];
				niceTime = (float) buffer[1];
				systemTime = (float) buffer[2];
				idleTime = (float) buffer[3];
				iowaitTime = (float) buffer[4];
			} else {
				double buffer[] = calculateCPUInfo(entryA, entryB);
				allUserTime.append(String.valueOf(buffer[0]));
				allUserTime.append("@@");
				allNiceTime.append(String.valueOf(buffer[1]));
				allNiceTime.append("@@");
				allSystemTime.append(String.valueOf(buffer[2]));
				allSystemTime.append("@@");
				allIdleTime.append(String.valueOf(buffer[3]));
				allIdleTime.append("@@");
				allIowaitTime.append(String.valueOf(buffer[4]));
				allIowaitTime.append("@@");
			}
		}
		SharedArea.dynamicInfoSerializable.setUserTime(userTime);
		SharedArea.dynamicInfoSerializable.setNiceTime(niceTime);
		SharedArea.dynamicInfoSerializable.setSystemTime(systemTime);
		SharedArea.dynamicInfoSerializable.setIowaitTime(iowaitTime);
		SharedArea.dynamicInfoSerializable.setIdleTime(idleTime);
		SharedArea.dynamicInfoSerializable.setAllUserTime(allUserTime.toString().substring(0, allUserTime.length() - 2));
		SharedArea.dynamicInfoSerializable.setAllNiceTime(allNiceTime.toString().substring(0, allNiceTime.length() - 2));
		SharedArea.dynamicInfoSerializable.setAllSystemTime(allSystemTime.toString().substring(0, allSystemTime.length() - 2));
		SharedArea.dynamicInfoSerializable.setAllIowaitTime(allIowaitTime.toString().substring(0, allIowaitTime.length() - 2));
		SharedArea.dynamicInfoSerializable.setAllIdleTime(allIdleTime.toString().substring(0, allIdleTime.length() - 2));
	}

	/**
	 * 将从stat文件获取之后放入HashMap的数据进行计算
	 * 
	 * @param entryA
	 * @param entryB
	 * @return 返回mind数组
	 */
	private double[] calculateCPUInfo(Map.Entry<String, double[]> entryA, Map.Entry<String, double[]> entryB) {
		double mind[] = new double[5];
		double total = 0;
		double bufferA[] = entryA.getValue();
		double bufferB[] = entryB.getValue();
		for (int i = 0; i < 5; i++) {
			mind[i] = bufferA[i] - bufferB[i];
		}
		for (int i = 0; i < 5; i++) {
			total += mind[i];
		}

		for (int i = 0; i < 5; i++) {
			if (i == 3)
				continue;
			mind[i] = DoubleUtils.convert((mind[i] / total) * 100);
		}
		mind[3] = DoubleUtils.convert(100 - mind[0] - mind[1] - mind[2] - mind[4]);
		return mind;
	}

	/**
	 * 将从stat文件取出来的每一行数据复制给mind[]数组
	 * 
	 * @param str
	 *            读文件的每一行数据
	 * @return mind数组
	 */
	private double[] getArray(String str) {
		double mind[] = new double[5];
		String a[] = str.trim().split(" ");
		switch (a.length) {
		case 5: // 兼容Red Hat 9版本，内核，2.4.20
			mind[0] = Double.parseDouble(a[1]);
			mind[1] = Double.parseDouble(a[2]);
			mind[2] = Double.parseDouble(a[3]);
			mind[3] = Double.parseDouble(a[4]);
			mind[4] = 0;
			break;
		case 8: // 兼容Red Hat Enterprise Linux AS release 4版本，/proc/stat中的数字个数为7
			mind[0] = Double.parseDouble(a[1]) + Double.parseDouble(a[7]);
			mind[1] = Double.parseDouble(a[2]);
			mind[2] = Double.parseDouble(a[3]) + Double.parseDouble(a[6]);
			mind[3] = Double.parseDouble(a[4]);
			mind[4] = Double.parseDouble(a[5]);
			break;
		case 9: // 兼容Red Hat Enterprise Linux Server release
			// 5.2版本,/proc/stat中的数字个数为8
			mind[0] = Double.parseDouble(a[1]) + Double.parseDouble(a[7]);
			mind[1] = Double.parseDouble(a[2]);
			mind[2] = Double.parseDouble(a[3]) + Double.parseDouble(a[6]) + Double.parseDouble(a[8]);
			mind[3] = Double.parseDouble(a[4]);
			mind[4] = Double.parseDouble(a[5]);
			break;
		case 10: // 兼容Red Hat Enterprise Linux Server release
			// 6.0版本,/proc/stat中的数字个数为9
			mind[0] = Double.parseDouble(a[1]) + Double.parseDouble(a[7]) + Double.parseDouble(a[9]);
			mind[1] = Double.parseDouble(a[2]);
			mind[2] = Double.parseDouble(a[3]) + Double.parseDouble(a[6]) + Double.parseDouble(a[8]);
			mind[3] = Double.parseDouble(a[4]);
			mind[4] = Double.parseDouble(a[5]);
			break;
		default:
			mind[0] = Double.parseDouble(a[1]);
			mind[1] = Double.parseDouble(a[2]);
			mind[2] = Double.parseDouble(a[3]);
			mind[3] = Double.parseDouble(a[4]);
			mind[4] = Double.parseDouble(a[5]);
			;
		}
		return mind;
	}

	/**
	 * 从stat文件获取信息，返回的是map结构
	 * 
	 * @return cpuInfoMap
	 * @throws IOException
	 */
	private Map<String, double[]> getInfoFromStat() throws IOException {
		Map<String, double[]> cpuInfoMap = new HashMap<String, double[]>();
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader("/proc/stat");
			br = new BufferedReader(reader);
			String str;
			while ((str = br.readLine()) != null) {
				if (str.contains("cpu")) {
					str = str.replaceAll(" {2,}", " ");
					String a[] = str.split(" ");
					double[] buffer = getArray(str);
					cpuInfoMap.put(a[0], buffer);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			LOG.error(LogUtils.getTrace(e));
		} finally {
			if (br != null)
				br.close();
			if (reader != null)
				reader.close();
		}
		return cpuInfoMap;
	}

	@Override
	public void run() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		while (running) {
			try {
				convertToCPUInfo();
				try {
					Thread.sleep(samplingInterval);
				} catch (InterruptedException e) {
					e.printStackTrace(); // To change body of catch statement
											// use File | Settings | File
											// Templates.
				}
			} catch (IOException e) {
				LOG.error(LogUtils.getTrace(e)); // To change body of catch
													// statement use File |
													// Settings | File
													// Templates.
			}
		}
	}
}
