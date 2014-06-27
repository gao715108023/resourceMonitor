package com.sinopec.group.rpc;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sinopec.bean.AdjacentGroup;
import com.sinopec.dao.GroupInfoDao;
import com.sinopec.io.GroupStatus;
import com.sinopec.utils.LogUtils;

public class TopologyClient implements Runnable {

	private static final Log LOG = LogFactory.getLog(TopologyClient.class);

	private Remote remote;

	private static volatile boolean running = true;

	private static volatile boolean service = true;

	private String crontrolNodeIP;

	private int port;

	private int samplingInterval = 3000;

	private String localhostIP;

	private AdjacentGroup adjacentGroup;

	public TopologyClient(String crontrolNodeIP, int port, int samplingInterval, String localhostIP, AdjacentGroup adjacentGroup) {
		super();
		this.crontrolNodeIP = crontrolNodeIP;
		this.port = port;
		this.samplingInterval = samplingInterval;
		this.localhostIP = localhostIP;
		this.adjacentGroup = adjacentGroup;
	}

	public boolean isServerExists(String bindName) {
		boolean result = false;
		try {
			remote = Naming.lookup(bindName);
			if (remote != null)
				result = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void connectToCrontrolNode() {
		int retryCount = 1;
		while (running) {
			if (isServerExists("rmi://" + crontrolNodeIP + ":" + port + "/topology")) {
				running = false;
				service = true;
			} else {
				LOG.info("Retrying connect to CrontrolNode: " + crontrolNodeIP + ".Already tries " + retryCount + " time(s).");
				retryCount++;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public Remote getRemote() {
		return remote;
	}

	public void setRemote(Remote remote) {
		this.remote = remote;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		connectToCrontrolNode();
		while (service) {
			try {
				Thread.sleep(samplingInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GroupInfoDao groupInfoDao = (GroupInfoDao) getRemote();
			GroupStatus groupStatus = new GroupStatus();
			groupStatus.setGroupIP(localhostIP);
			try {
				String returnInfo = groupInfoDao.transmitGroupInfo(groupStatus);
				adjacentGroup.setNextGroupIP(returnInfo);
				if (LogUtils.enableDEBUG()) {
					LOG.info("The IP of next group: " + returnInfo);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				LOG.error("CrontrolNode [" + crontrolNodeIP + "]. Status:STOPPED");
				service = false;
				running = true;
				connectToCrontrolNode();
			}
		}
	}
}
