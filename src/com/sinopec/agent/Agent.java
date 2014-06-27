package com.sinopec.agent;

import com.sinopec.agent.infocollect.*;
import com.sinopec.agent.rpc.DynamicInfoClient;
import com.sinopec.agent.share.SharedArea;
import com.sinopec.bean.NodeInfoSampleConfigureBean;
import com.sinopec.io.DynamicInfoSerializable;
import com.sinopec.utils.ConfigUtils;
import com.sinopec.utils.HostUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-12 Time: 下午3:56
 * To change this template use File | Settings | File Templates.
 */
public class Agent {

	private static final Log LOG = LogFactory.getLog(Agent.class);

	static {
		SharedArea.nodeName = HostUtil.getHostName();
		if (SharedArea.nodeName == null) {
			LOG.error("Can't get the hostname!");
			System.exit(1); // 异常退出
		} else if (SharedArea.nodeName.length() > 6) {
			SharedArea.nodeName = SharedArea.nodeName.substring(0, 6);
		}
		SharedArea.dynamicInfoSerializable = new DynamicInfoSerializable(SharedArea.nodeName);
	}

	public void startAllCollectThread(boolean isProcessInfo, boolean isCpuInfo, boolean isMemInfo, boolean isSwapInfo, boolean isDiskIOInfo, boolean isNetworkInfo, int samplingInterval) {

		if (isProcessInfo) {
			new Thread(new DynamicProcessInfo(samplingInterval)).start();
		}

		if (isCpuInfo) {
			new Thread(new DynamicCPUInfo(samplingInterval)).start();
		}

		if (isMemInfo) {
			new Thread(new DynamicMemInfo(samplingInterval)).start();
		}

		if (isSwapInfo) {
			new Thread(new DynamicSwapInfo(samplingInterval)).start();
		}

		if (isDiskIOInfo) {
			new Thread(new DynamicDiskIOInfo(samplingInterval)).start();
		}

		if (isNetworkInfo) {
			new Thread(new DynamicNetworkInfo(samplingInterval)).start();
		}
	}

	public void startSendDynamicInfoThread(NodeInfoSampleConfigureBean nisc, String curGroupIP, int timeoutConnToNextGroup) {
		new Thread(new DynamicInfoClient(nisc.getTimeInterval(), curGroupIP, nisc.getGathererNodePort(), timeoutConnToNextGroup)).start();
	}

	public NodeInfoSampleConfigureBean getInitialInfoFromDB() {
		NodeInfoSampleConfigureBean nodeInfoSampleConfigureBean = new NodeInfoSampleConfigureBean();
		nodeInfoSampleConfigureBean.setNodeName("cp2013");
		nodeInfoSampleConfigureBean.setUpdateTime("");
		nodeInfoSampleConfigureBean.setTimeInterval(3000);
		nodeInfoSampleConfigureBean.setProcessInfo("y");
		nodeInfoSampleConfigureBean.setTimeInfo("y");
		nodeInfoSampleConfigureBean.setMemoryInfo("y");
		nodeInfoSampleConfigureBean.setSwapInfo("y");
		nodeInfoSampleConfigureBean.setDiskIOInfo("y");
		nodeInfoSampleConfigureBean.setNetworkIOInfo("y");
		nodeInfoSampleConfigureBean.setGathererNodeName("cp2013");
		nodeInfoSampleConfigureBean.setGathererNodePort(2006);
		nodeInfoSampleConfigureBean.setDataBaseServerIP("");
		return nodeInfoSampleConfigureBean;
	}

	public static void main(String[] args) {
		Agent agent = new Agent();
		NodeInfoSampleConfigureBean nisc = agent.getInitialInfoFromDB();
		agent.startAllCollectThread(nisc.isProcessInfo(), nisc.isTimeInfo(), nisc.isMemoryInfo(), nisc.isSwapInfo(), nisc.isDiskIOInfo(), nisc.isNetworkIOInfo(), nisc.getTimeInterval());
		ConfigUtils configUtils = new ConfigUtils("/opt/CRMS/sd/conf/infoconfig.properties");
		String curGroupIP = configUtils.getString("Group_" + nisc.getGathererNodeName());
		int timeoutConnToNextGroup = configUtils.getInt("time_connect_next_group");
		agent.startSendDynamicInfoThread(nisc, curGroupIP, timeoutConnToNextGroup);
	}
}
