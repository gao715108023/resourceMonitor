package com.sinopec.crontrolnode.rpc;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sinopec.cache.NodeInfoCache;
import com.sinopec.dao.GroupInfoDao;
import com.sinopec.impl.GroupInfoDaoImpl;

public class TopologyServer implements Runnable {

	private static final Log LOG = LogFactory.getLog(TopologyServer.class);

	private int port;

	private NodeInfoCache nodeInfoCache;

	public TopologyServer(int port, NodeInfoCache nodeInfoCache) {
		super();
		this.port = port;
		this.nodeInfoCache = nodeInfoCache;
	}

	public void startListen() {
		try {
			LocateRegistry.createRegistry(port);
			GroupInfoDao groupInfoDao = new GroupInfoDaoImpl(nodeInfoCache);
			Naming.rebind("rmi://127.0.0.1:" + port + "/topology", groupInfoDao);
			LOG.info("CrontrolNode is lintening on port " + port + "[Waiting for Group connection]: starting");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startListen();
	}
}
