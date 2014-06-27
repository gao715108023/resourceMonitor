package com.sinopec.crontrolnode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sinopec.cache.NodeInfoCache;
import com.sinopec.common.Constants;
import com.sinopec.crontrolnode.rpc.TopologyServer;
import com.sinopec.utils.ConfigUtils;
import com.sinopec.utils.LogUtils;

public class ControlNode {

	private static final Log LOG = LogFactory.getLog(ControlNode.class);

	public void start() {
		ConfigUtils configUtils = getConfigUtils("/opt/CRMS/sd/conf/infoconfig.properties");
		int portForCrontrolNode = configUtils.getInt("port_for_crontrolnode");
		int expiryIntervalForCrontrolnode = configUtils.getInt("expiryInterval_for_crontrolnode");
		Constants.isDebug = configUtils.getInt("debug");
		if (LogUtils.enableDEBUG()) {
			LOG.info("Debug: " + Constants.isDebug);
			LOG.info("Group timeout: " + expiryIntervalForCrontrolnode + "m");
		}
		NodeInfoCache nodeInfoCache = new NodeInfoCache(expiryIntervalForCrontrolnode);
		startListen(portForCrontrolNode, nodeInfoCache);
	}

	public void startListen(int portForCrontrolNode, NodeInfoCache nodeInfoCache) {
		new Thread(new TopologyServer(portForCrontrolNode, nodeInfoCache)).start();
	}

	private ConfigUtils getConfigUtils(String filePath) {
		return new ConfigUtils(filePath);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ControlNode controlNode = new ControlNode();
		controlNode.start();
	}
}
