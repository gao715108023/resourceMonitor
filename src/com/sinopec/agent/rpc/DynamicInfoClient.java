package com.sinopec.agent.rpc;

import com.sinopec.agent.share.SharedArea;
import com.sinopec.dao.DynamicInfoInterface;
import com.sinopec.test.PrintTestInfo;
import com.sinopec.utils.LogUtils;
import com.sinopec.utils.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午2:58
 * To change this template use File | Settings | File Templates.
 */
public class DynamicInfoClient implements Runnable {

	private static final Log LOG = LogFactory.getLog(DynamicInfoClient.class);

	private static volatile boolean running = true;

	private static volatile boolean service = true;

	private int samplingInterval;

	private String curGroupIP;

	private int port;

	private Remote remote;

	private int totalRetryCount;

	private int retryConnTimeout = 3000;

	private String nextGroupIP;

	public DynamicInfoClient(int samplingInterval, String curGroupIP, int port, int timeoutConnToNextGroup) {
		this.samplingInterval = samplingInterval;
		this.curGroupIP = curGroupIP;
		this.port = port;
		this.totalRetryCount = (timeoutConnToNextGroup * 60 * 1000) / retryConnTimeout;
	}

	public boolean isServerExists(String bindName) {
		boolean result = false;
		try {
			remote = Naming.lookup(bindName);
			if (remote != null)
				result = true;
		} catch (NotBoundException e) {
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		} catch (MalformedURLException e) {
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		} catch (RemoteException e) {
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		}
		return result;
	}

	public void connectToGroup() {
		int retryCount = 1;
		boolean b = true;
		while (running) {
			if (isServerExists("rmi://" + curGroupIP + ":" + port + "/heartbeat")) {
				running = false;
				service = true;
			} else {
				LOG.info("Retrying connect to Group: " + curGroupIP + ".Already tries " + retryCount + " time(s).");
				if (retryCount >= totalRetryCount && b) {
					curGroupIP = getNextGroupIP();
					retryCount = 1;
					b = false;
				}
				retryCount++;
				try {
					Thread.sleep(retryConnTimeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public Remote getRemote() {
		return remote;
	}

	public String getNextGroupIP() {
		return nextGroupIP;
	}

	public void setNextGroupIP(String nextGroupIP) {
		this.nextGroupIP = nextGroupIP;
	}

	@Override
	public void run() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		connectToGroup();
		while (service) {
			try {
				Thread.sleep(samplingInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// PrintTestInfo.printDynamicInfo(SharedArea.dynamicInfoSerializable);

			DynamicInfoInterface dynamicInfoInterface = (DynamicInfoInterface) getRemote();
			try {
				SharedArea.dynamicInfoSerializable.setUpdateTime(TimeUtil.getString());
				String returnInfo = dynamicInfoInterface.transmitDynamicInfo(SharedArea.dynamicInfoSerializable);
				setNextGroupIP(returnInfo);
				if (LogUtils.enableDEBUG()) {
					LOG.info("The IP of next group: " + returnInfo);
				}
				// System.out.println(returnInfo);
			} catch (RemoteException e) {
				LOG.error("Group [" + curGroupIP + "]. Status:STOPPED");
				service = false;
				running = true;
				connectToGroup();
			}
		}
	}
}
