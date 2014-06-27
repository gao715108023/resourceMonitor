package com.sinopec.group.rpc;

import com.sinopec.bean.AdjacentGroup;
import com.sinopec.cache.InstantMsgCache;
import com.sinopec.cache.HistroyMsgCache;
import com.sinopec.impl.DynamicInfoInterfaceImpl;
import com.sinopec.dao.DynamicInfoInterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class DynamicInfoServer implements Runnable {

	private static final Log LOG = LogFactory.getLog(DynamicInfoServer.class);

	private int port;

	private InstantMsgCache dynamicCache;

	private HistroyMsgCache histroyMsgCache;

	private AdjacentGroup adjacentGroup;

	public DynamicInfoServer(int port, InstantMsgCache dynamicCache, HistroyMsgCache histroyMsgCache, AdjacentGroup adjacentGroup) {
		this.port = port;
		this.dynamicCache = dynamicCache;
		this.histroyMsgCache = histroyMsgCache;
		this.adjacentGroup = adjacentGroup;
	}

	public void startListen() {
		try {
			LocateRegistry.createRegistry(port);
			DynamicInfoInterface dynamicInfoInterface = new DynamicInfoInterfaceImpl(dynamicCache, histroyMsgCache, adjacentGroup);
			Naming.rebind("rmi://127.0.0.1:" + port + "/heartbeat", dynamicInfoInterface);
			LOG.info("Group is lintening on port " + port + "[Waiting for Agent connection]: starting");
		} catch (RemoteException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} catch (MalformedURLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
	}

	@Override
	public void run() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		startListen();
	}
}
