package com.sinopec.test;

import com.sinopec.agent.share.SharedArea;
import com.sinopec.bean.ComputeNodeStatusBean;
import com.sinopec.io.DynamicInfoSerializable;
import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-24 Time: 下午4:53 To
 * change this template use File | Settings | File Templates.
 */
public class PrintTestInfo {

	private static final Log LOG = LogFactory.getLog(PrintTestInfo.class);

	public static void printDynamicInfo(DynamicInfoSerializable dynamicInfoSerializable) {
		if (LogUtils.enableDEBUG()) {
			LOG.info("OneMinsProcs: " + dynamicInfoSerializable.getOneMinsProcs() + "     FiveMInsProcs: " + dynamicInfoSerializable.getFiveMinsProcs() + "     fifteenMinsProcs: " + dynamicInfoSerializable.getFifteenMinsProcs());
			LOG.info("UserTime: " + dynamicInfoSerializable.getUserTime() + "     NiceTime: " + dynamicInfoSerializable.getNiceTime() + "     SystemTime: " + dynamicInfoSerializable.getSystemTime() + "     IowaitTime: " + dynamicInfoSerializable.getIowaitTime() + "     IdleTime: " + dynamicInfoSerializable.getIdleTime());
			LOG.info("AllUserTime: " + dynamicInfoSerializable.getAllUserTime() + "     AllNiceTime: " + dynamicInfoSerializable.getAllNiceTime() + "     AllSystemTime: " + dynamicInfoSerializable.getAllSystemTime() + "     AllIowaitTime: " + dynamicInfoSerializable.getAllIowaitTime() + "     AllIdleTime: " + dynamicInfoSerializable.getAllIdleTime());
			LOG.info("TotalMemory: " + dynamicInfoSerializable.getTotalMemory() + "     UsedMemory: " + dynamicInfoSerializable.getUsedMemory() + "     IdleMemory: " + dynamicInfoSerializable.getIdleMemory() + "     SwapSize: " + dynamicInfoSerializable.getSwapSize() + "     UsedSwap: " + dynamicInfoSerializable.getUsedSwap() + "     IdleSwap: " + dynamicInfoSerializable.getIdleSwap());
			LOG.info("IoTs: " + dynamicInfoSerializable.getIoTs() + "     ReadKB: " + dynamicInfoSerializable.getReadKB() + "     ReadSpeed: " + dynamicInfoSerializable.getReadSpeed() + "     WriteKB: " + dynamicInfoSerializable.getWriteKB() + "     WriteSpeed: " + dynamicInfoSerializable.getWriteSpeed());
			LOG.info("InReceivesPs: " + dynamicInfoSerializable.getInReceivesPs() + "     InDeliversPs: " + dynamicInfoSerializable.getInDeliversPs() + "     OutRequestsPs: " + dynamicInfoSerializable.getOutRequestsPs() + "     InSegsPs: " + dynamicInfoSerializable.getInSegsPs() + "     OutSegsPs: " + dynamicInfoSerializable.getOutSegsPs() + "     RetransSegsPs: " + dynamicInfoSerializable.getRetransSegsPs() + "     InDatagramsPs: " + dynamicInfoSerializable.getInDatagramsPs() + "     OutDatagramsPs: " + dynamicInfoSerializable.getOutDatagramsPs());
		}
	}

	public static void printInstantMsg(ConcurrentHashMap<String, DynamicInfoSerializable> cache) {
		Iterator iterator = cache.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, DynamicInfoSerializable> entry = (Map.Entry<String, DynamicInfoSerializable>) iterator.next();
			// String key = entry.getKey();
			DynamicInfoSerializable dis = entry.getValue();
			printDynamicInfo(dis);
		}
	}

	public static void printInstantCacheMsg(ComputeNodeStatusBean dynamicInfoSerializable) {
		if (LogUtils.enableDEBUG()) {
			LOG.info("NodeName: " + dynamicInfoSerializable.getNodeName() + "     UpdateTime: " + dynamicInfoSerializable.getUpdateTime());
			LOG.info("OneMinsProcs: " + dynamicInfoSerializable.getOneMinsProcs() + "     FiveMInsProcs: " + dynamicInfoSerializable.getFiveMinsProcs() + "     fifteenMinsProcs: " + dynamicInfoSerializable.getFifteenMinsProcs());
			LOG.info("UserTime: " + dynamicInfoSerializable.getUserTime() + "     NiceTime: " + dynamicInfoSerializable.getNiceTime() + "     SystemTime: " + dynamicInfoSerializable.getSystemTime() + "     IowaitTime: " + dynamicInfoSerializable.getIowaitTime() + "     IdleTime: " + dynamicInfoSerializable.getIdleTime());
			LOG.info("AllUserTime: " + dynamicInfoSerializable.getAllUserTime() + "     AllNiceTime: " + dynamicInfoSerializable.getAllNiceTime() + "     AllSystemTime: " + dynamicInfoSerializable.getAllSystemTime() + "     AllIowaitTime: " + dynamicInfoSerializable.getAllIowaitTime() + "     AllIdleTime: " + dynamicInfoSerializable.getAllIdleTime());
			LOG.info("TotalMemory: " + dynamicInfoSerializable.getTotalMemory() + "     UsedMemory: " + dynamicInfoSerializable.getUsedMemory() + "     IdleMemory: " + dynamicInfoSerializable.getIdleMemory() + "     SwapSize: " + dynamicInfoSerializable.getSwapSize() + "     UsedSwap: " + dynamicInfoSerializable.getUsedSwap() + "     IdleSwap: " + dynamicInfoSerializable.getIdleSwap());
			LOG.info("IoTs: " + dynamicInfoSerializable.getIoTs() + "     ReadKB: " + dynamicInfoSerializable.getReadKB() + "     ReadSpeed: " + dynamicInfoSerializable.getReadSpeed() + "     WriteKB: " + dynamicInfoSerializable.getWriteKB() + "     WriteSpeed: " + dynamicInfoSerializable.getWriteSpeed());
			LOG.info("InReceivesPs: " + dynamicInfoSerializable.getInReceivesPs() + "     InDeliversPs: " + dynamicInfoSerializable.getInDeliversPs() + "     OutRequestsPs: " + dynamicInfoSerializable.getOutRequestsPs() + "     InSegsPs: " + dynamicInfoSerializable.getInSegsPs() + "     OutSegsPs: " + dynamicInfoSerializable.getOutSegsPs() + "     RetransSegsPs: " + dynamicInfoSerializable.getRetransSegsPs() + "     InDatagramsPs: " + dynamicInfoSerializable.getInDatagramsPs() + "     OutDatagramsPs: " + dynamicInfoSerializable.getOutDatagramsPs());
		}
	}
}
