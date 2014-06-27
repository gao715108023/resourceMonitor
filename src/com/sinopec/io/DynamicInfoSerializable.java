package com.sinopec.io;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-12 Time: 下午4:04
 * To change this template use File | Settings | File Templates.
 */
public class DynamicInfoSerializable implements Serializable {

	private String nodeName;
	private String updateTime;

	private float oneMinsProcs;
	private float fiveMinsProcs;
	private float fifteenMinsProcs;

	private float userTime;
	private float niceTime;
	private float systemTime;
	private float iowaitTime;
	private float idleTime;

	private String allUserTime;
	private String allNiceTime;
	private String allSystemTime;
	private String allIowaitTime;
	private String allIdleTime;

	private int totalMemory;
	private int usedMemory;
	private int idleMemory;
	private int swapSize;
	private int usedSwap;
	private int idleSwap;

	private int ioTs;
	private int readSpeed;
	private int readKB;
	private int writeSpeed;
	private int writeKB;

	private double inReceivesPs;
	private double inDeliversPs;
	private double outRequestsPs;
	private double inSegsPs;
	private double outSegsPs;
	private double retransSegsPs;
	private double inDatagramsPs;
	private double outDatagramsPs;

	private String gpuUtil;
	private String memUtil;
	private String temperature;
	private String fanSpeed;
	private String pstate;
	private String computeMode;

	public DynamicInfoSerializable(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public float getOneMinsProcs() {
		return oneMinsProcs;
	}

	public void setOneMinsProcs(float oneMinsProcs) {
		this.oneMinsProcs = oneMinsProcs;
	}

	public float getFiveMinsProcs() {
		return fiveMinsProcs;
	}

	public void setFiveMinsProcs(float fiveMinsProcs) {
		this.fiveMinsProcs = fiveMinsProcs;
	}

	public float getFifteenMinsProcs() {
		return fifteenMinsProcs;
	}

	public void setFifteenMinsProcs(float fifteenMinsProcs) {
		this.fifteenMinsProcs = fifteenMinsProcs;
	}

	public float getUserTime() {
		return userTime;
	}

	public void setUserTime(float userTime) {
		this.userTime = userTime;
	}

	public float getNiceTime() {
		return niceTime;
	}

	public void setNiceTime(float niceTime) {
		this.niceTime = niceTime;
	}

	public float getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(float systemTime) {
		this.systemTime = systemTime;
	}

	public float getIowaitTime() {
		return iowaitTime;
	}

	public void setIowaitTime(float iowaitTime) {
		this.iowaitTime = iowaitTime;
	}

	public float getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(float idleTime) {
		this.idleTime = idleTime;
	}

	public String getAllUserTime() {
		return allUserTime;
	}

	public void setAllUserTime(String allUserTime) {
		this.allUserTime = allUserTime;
	}

	public String getAllNiceTime() {
		return allNiceTime;
	}

	public void setAllNiceTime(String allNiceTime) {
		this.allNiceTime = allNiceTime;
	}

	public String getAllSystemTime() {
		return allSystemTime;
	}

	public void setAllSystemTime(String allSystemTime) {
		this.allSystemTime = allSystemTime;
	}

	public String getAllIowaitTime() {
		return allIowaitTime;
	}

	public void setAllIowaitTime(String allIowaitTime) {
		this.allIowaitTime = allIowaitTime;
	}

	public String getAllIdleTime() {
		return allIdleTime;
	}

	public void setAllIdleTime(String allIdleTime) {
		this.allIdleTime = allIdleTime;
	}

	public int getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(int totalMemory) {
		this.totalMemory = totalMemory;
	}

	public int getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(int usedMemory) {
		this.usedMemory = usedMemory;
	}

	public int getIdleMemory() {
		return idleMemory;
	}

	public void setIdleMemory(int idleMemory) {
		this.idleMemory = idleMemory;
	}

	public int getSwapSize() {
		return swapSize;
	}

	public void setSwapSize(int swapSize) {
		this.swapSize = swapSize;
	}

	public int getUsedSwap() {
		return usedSwap;
	}

	public void setUsedSwap(int usedSwap) {
		this.usedSwap = usedSwap;
	}

	public int getIdleSwap() {
		return idleSwap;
	}

	public void setIdleSwap(int idleSwap) {
		this.idleSwap = idleSwap;
	}

	public int getIoTs() {
		return ioTs;
	}

	public void setIoTs(int ioTs) {
		this.ioTs = ioTs;
	}

	public int getReadSpeed() {
		return readSpeed;
	}

	public void setReadSpeed(int readSpeed) {
		this.readSpeed = readSpeed;
	}

	public int getReadKB() {
		return readKB;
	}

	public void setReadKB(int readKB) {
		this.readKB = readKB;
	}

	public int getWriteSpeed() {
		return writeSpeed;
	}

	public void setWriteSpeed(int writeSpeed) {
		this.writeSpeed = writeSpeed;
	}

	public int getWriteKB() {
		return writeKB;
	}

	public void setWriteKB(int writeKB) {
		this.writeKB = writeKB;
	}

	public double getInReceivesPs() {
		return inReceivesPs;
	}

	public void setInReceivesPs(double inReceivesPs) {
		this.inReceivesPs = inReceivesPs;
	}

	public double getInDeliversPs() {
		return inDeliversPs;
	}

	public void setInDeliversPs(double inDeliversPs) {
		this.inDeliversPs = inDeliversPs;
	}

	public double getOutRequestsPs() {
		return outRequestsPs;
	}

	public void setOutRequestsPs(double outRequestsPs) {
		this.outRequestsPs = outRequestsPs;
	}

	public double getInSegsPs() {
		return inSegsPs;
	}

	public void setInSegsPs(double inSegsPs) {
		this.inSegsPs = inSegsPs;
	}

	public double getOutSegsPs() {
		return outSegsPs;
	}

	public void setOutSegsPs(double outSegsPs) {
		this.outSegsPs = outSegsPs;
	}

	public double getRetransSegsPs() {
		return retransSegsPs;
	}

	public void setRetransSegsPs(double retransSegsPs) {
		this.retransSegsPs = retransSegsPs;
	}

	public double getInDatagramsPs() {
		return inDatagramsPs;
	}

	public void setInDatagramsPs(double inDatagramsPs) {
		this.inDatagramsPs = inDatagramsPs;
	}

	public double getOutDatagramsPs() {
		return outDatagramsPs;
	}

	public void setOutDatagramsPs(double outDatagramsPs) {
		this.outDatagramsPs = outDatagramsPs;
	}

	public String getGpuUtil() {
		return gpuUtil;
	}

	public void setGpuUtil(String gpuUtil) {
		this.gpuUtil = gpuUtil;
	}

	public String getMemUtil() {
		return memUtil;
	}

	public void setMemUtil(String memUtil) {
		this.memUtil = memUtil;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getFanSpeed() {
		return fanSpeed;
	}

	public void setFanSpeed(String fanSpeed) {
		this.fanSpeed = fanSpeed;
	}

	public String getPstate() {
		return pstate;
	}

	public void setPstate(String pstate) {
		this.pstate = pstate;
	}

	public String getComputeMode() {
		return computeMode;
	}

	public void setComputeMode(String computeMode) {
		this.computeMode = computeMode;
	}
}
