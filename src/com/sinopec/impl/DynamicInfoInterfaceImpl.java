package com.sinopec.impl;

import com.sinopec.bean.AdjacentGroup;
import com.sinopec.cache.InstantMsgCache;
import com.sinopec.cache.HistroyMsgCache;
import com.sinopec.io.DynamicInfoSerializable;
import com.sinopec.dao.DynamicInfoInterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午4:08
 * To change this template use File | Settings | File Templates.
 */
public class DynamicInfoInterfaceImpl extends UnicastRemoteObject implements DynamicInfoInterface {

	private static final Log LOG = LogFactory.getLog(DynamicInfoInterfaceImpl.class);

	private InstantMsgCache dynamicCache;

	private HistroyMsgCache histroyMsgCache;

	private AdjacentGroup adjacentGroup;

	public DynamicInfoInterfaceImpl(InstantMsgCache dynamicCache, HistroyMsgCache histroyMsgCache, AdjacentGroup adjacentGroup) throws RemoteException {
		this.dynamicCache = dynamicCache;
		this.histroyMsgCache = histroyMsgCache;
		this.adjacentGroup = adjacentGroup;
	}

	@Override
	public String transmitDynamicInfo(DynamicInfoSerializable dis) throws RemoteException {
		String nodeName = dis.getNodeName(); // 节点名称
		String updateTime = dis.getUpdateTime(); // 更新时间 格式 ‘2003-12-31
													// 23:59:59’
		String localhostIP = "test-127.0.0.1";
		// String localhostIP = dis.getLocalhostIP(); // 本机IP地址
		// boolean isFirst = dis.isFirst();
		boolean isFirst = true;
		float oneMinsProcs = dis.getOneMinsProcs(); // FLOAT(6,2)
		float fiveMinsProcs = dis.getFiveMinsProcs(); // FLOAT(6,2)
		float fifteenMinsProcs = dis.getFifteenMinsProcs();// FLOAT(6,2)
		float userTime = dis.getUserTime(); // FLOAT(6,2)
		float niceTime = dis.getNiceTime(); // FLOAT(6,2)
		float systemTime = dis.getSystemTime(); // FLOAT(6,2)
		float iowaitTime = dis.getIowaitTime(); // FLOAT(6,2)
		float idleTime = dis.getIdleTime(); // FLOAT(6,2)
		String allUserTime = dis.getAllUserTime();
		String allNiceTime = dis.getAllNiceTime();
		String allSystemTime = dis.getAllSystemTime();
		String allIowaitTime = dis.getAllIowaitTime();
		String allIdleTime = dis.getAllIdleTime();
		int totalMemory = dis.getTotalMemory(); // MEDIUMINT
		int usedMemory = dis.getUsedMemory(); // MEDIUMINT
		int idleMemory = dis.getIdleMemory(); // MEDIUMINT
		int swapSize = dis.getSwapSize(); // INT
		int usedSwap = dis.getUsedSwap(); // INT
		int idleSwap = dis.getIdleSwap(); // INT

		int ioTs = dis.getIoTs(); // MEDIUMINT
		int readSpeed = dis.getReadSpeed(); // MEDIUMINT
		int readKB = dis.getReadKB(); // MEDIUMINT
		int writeSpeed = dis.getWriteSpeed(); // MEDIUMINT
		int writeKB = dis.getWriteKB(); // MEDIUMINT

		double inReceivesPs = dis.getInReceivesPs(); // DOUBLE(12,2)
		double inDeliversPs = dis.getInDeliversPs(); // DOUBLE(12,2)
		double outRequestsPs = dis.getOutRequestsPs(); // DOUBLE(12,2)
		double inSegsPs = dis.getInSegsPs(); // DOUBLE(12,2)
		double outSegsPs = dis.getOutSegsPs(); // DOUBLE(12,2)
		double retransSegsPs = dis.getRetransSegsPs(); // DOUBLE(12,2)
		double inDatagramsPs = dis.getInDatagramsPs(); // DOUBLE(12,2)
		double outDatagramsPs = dis.getOutDatagramsPs(); // DOUBLE(12,2)
		// if (LogUtils.enableDEBUG()) {
		// LOG.info("----------------------------------------------------------------------------------------"
		// +
		// "-----------------------------------------------------------");
		// LOG.info("NodeName: " + nodeName + "     UpdateTime: " + updateTime +
		// "     LocalhostIP: " + localhostIP +
		// "     IsFirst: " + isFirst);
		// LOG.info("OneMinsProcs: " + oneMinsProcs + "     FiveMInsProcs: " +
		// fiveMinsProcs +
		// "     fifteenMinsProcs: " + fifteenMinsProcs);
		// LOG.info("UserTime: " + userTime + "     NiceTime: " + niceTime +
		// "     SystemTime: " + systemTime +
		// "     IowaitTime: " + iowaitTime + "     IdleTime: " + idleTime);
		// LOG.info("AllUserTime: " + allUserTime + "     AllNiceTime: " +
		// allNiceTime + "     AllSystemTime: " +
		// allSystemTime + "     AllIowaitTime: " + allIowaitTime +
		// "     AllIdleTime: " + allIdleTime);
		// LOG.info("TotalMemory: " + totalMemory + "     UsedMemory: " +
		// usedMemory + "     IdleMemory: " +
		// idleMemory + "     SwapSize: " + swapSize + "     UsedSwap: " +
		// usedSwap + "     IdleSwap: " +
		// idleSwap);
		// LOG.info("IoTs: " + ioTs + "     ReadKB: " + readKB +
		// "     ReadSpeed: " + readSpeed + "     WriteKB: " +
		// writeKB + "     WriteSpeed: " + writeSpeed);
		// LOG.info("InReceivesPs: " + inReceivesPs + "     InDeliversPs: " +
		// inDeliversPs + "     OutRequestsPs: " +
		// outRequestsPs + "     InSegsPs: " + inSegsPs + "     OutSegsPs: " +
		// outSegsPs +
		// "     RetransSegsPs: " + retransSegsPs + "     InDatagramsPs: " +
		// inDatagramsPs +
		// "     OutDatagramsPs: " + outDatagramsPs);
		// LOG.info("------------------------------------------------------------------------------------------"
		// +
		// "---------------------------------------------------------");
		// }
		// PrintTestInfo.printDynamicInfo(dis);
		handleDynamicInfo(dis);
		return adjacentGroup.getNextGroupIP(); // To change body of implemented
												// methods use File |
		// Settings | File Templates.
	}

	private void handleDynamicInfo(DynamicInfoSerializable dis) {
		dynamicCache.put(dis.getNodeName(), dis, new Date());
		histroyMsgCache.put(dis.getNodeName(), dis);
	}
}
