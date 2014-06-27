package com.sinopec.agent.infocollect;

import com.sinopec.agent.share.SharedArea;
import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午2:29
 * To change this template use File | Settings | File Templates.
 */
public class DynamicNetworkInfo implements Runnable {

	private static final Log LOG = LogFactory.getLog(DynamicNetworkInfo.class);

	private int samplingInterval;

	private volatile boolean running = true;

	public DynamicNetworkInfo(int samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	public void getNetworkInfoByProc() throws IOException {
		double inReceivesPs = 0.00; // IP接受包率 包数/秒
		double inDeliversPs = 0.00; // IP回应包率 包数/秒
		double outRequestsPs = 0.00; // IP请求包率 包数/秒
		double inSegsPs = 0.00; // TCP接受段率 段数/秒
		double outSegsPs = 0.00; // TCP发送段率 段数/秒
		double retransSegsPs = 0.00; // TCP重发段率 段数/秒
		double inDatagramsPs = 0.00; // UDP接受包率 包数/秒
		double outDatagramsPs = 0.00; // UDP发送包率 包数/秒
		double[] bufferB = getCommondTCPUDPIP();
		int data = 3;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		double[] bufferA = getCommondTCPUDPIP();
		if ((bufferA != null) & (bufferB != null)) {
			DecimalFormat df = new DecimalFormat("000.00");
			inReceivesPs = Double.parseDouble(df.format((bufferA[0] - bufferB[0]) / data));
			inDeliversPs = Double.parseDouble(df.format((bufferA[1] - bufferB[1]) / data));
			outRequestsPs = Double.parseDouble(df.format((bufferA[2] - bufferB[2]) / data));
			inSegsPs = Double.parseDouble(df.format((bufferA[3] - bufferB[3]) / data));
			outSegsPs = Double.parseDouble(df.format((bufferA[4] - bufferB[4]) / data));
			retransSegsPs = Double.parseDouble(df.format((bufferA[5] - bufferB[5]) / data));
			inDatagramsPs = Double.parseDouble(df.format((bufferA[6] - bufferB[6]) / data));
			outDatagramsPs = Double.parseDouble(df.format((bufferA[7] - bufferB[7]) / data));
			SharedArea.dynamicInfoSerializable.setInReceivesPs(inReceivesPs);
			SharedArea.dynamicInfoSerializable.setInDeliversPs(inDeliversPs);
			SharedArea.dynamicInfoSerializable.setOutRequestsPs(outRequestsPs);
			SharedArea.dynamicInfoSerializable.setInSegsPs(inSegsPs);
			SharedArea.dynamicInfoSerializable.setOutSegsPs(outSegsPs);
			SharedArea.dynamicInfoSerializable.setRetransSegsPs(retransSegsPs);
			SharedArea.dynamicInfoSerializable.setInDatagramsPs(inDatagramsPs);
			SharedArea.dynamicInfoSerializable.setOutDatagramsPs(outDatagramsPs);
		}
	}

	/**
	 * 从/proc/net/snmp中获取TCP/UDP/IP的信息
	 * 
	 * @return mind
	 * @throws IOException
	 */
	private double[] getCommondTCPUDPIP() throws IOException {
		double mind[] = new double[8];
		BufferedReader buffer = null;
		try {
			buffer = new BufferedReader(new FileReader("/proc/net/snmp"));
			String line = null;
			int ipnum = 0, ipn = 0;
			int tcpnum = 0, tcpn = 0;
			int udpnum = 0, udpn = 0;
			while ((line = buffer.readLine()) != null) {
				if (line.contains("Ip:") && ipnum == 0) {
					ipnum++;
				} else if (line.contains("Ip:") && ipnum == 1) {
					ipnum++;
					StringTokenizer toker = new StringTokenizer(line);
					while (toker.hasMoreElements()) {
						toker.nextToken();
						ipn++;
						if (ipn == 3) {
							mind[0] = Double.parseDouble(toker.nextToken().trim());
							ipn++;
						} else if (ipn == 9) {
							mind[1] = Double.parseDouble(toker.nextToken().trim());
							ipn++;
							mind[2] = Double.parseDouble(toker.nextToken().trim());
							ipn++;
							break;
						}
					}
				}

				if (line.contains("Tcp:") && tcpnum == 0) {
					tcpnum++;
				} else if (line.contains("Tcp:") && tcpnum == 1) {
					tcpnum++;
					StringTokenizer toker = new StringTokenizer(line);
					while (toker.hasMoreElements()) {
						toker.nextToken();
						tcpn++;
						if (tcpn == 10) {
							mind[3] = Double.parseDouble(toker.nextToken().trim());
							mind[4] = Double.parseDouble(toker.nextToken().trim());
							mind[5] = Double.parseDouble(toker.nextToken().trim());
							break;
						}
					}
				}
				if (line.contains("Udp:") && udpnum == 0) {
					udpnum++;
				} else if (line.contains("Udp:") && udpnum == 1) {
					udpnum++;
					StringTokenizer toker = new StringTokenizer(line);
					while (toker.hasMoreElements()) {
						toker.nextToken();
						udpn++;
						if (udpn == 1) {
							mind[6] = Double.parseDouble(toker.nextToken().trim());
							udpn++;
						} else if (udpn == 4) {
							mind[7] = Double.parseDouble(toker.nextToken().trim());
							udpn++;
							break;
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			LOG.error(LogUtils.getTrace(e));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(LogUtils.getTrace(e));
		} finally {
			if (buffer != null)
				buffer.close();
		}
		return mind;
	}

	@Override
	public void run() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		while (running) {
			try {
				getNetworkInfoByProc();
			} catch (IOException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
			try {
				Thread.sleep(samplingInterval);
			} catch (InterruptedException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
		}
	}
}
