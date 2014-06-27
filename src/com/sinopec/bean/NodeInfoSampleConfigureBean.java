package com.sinopec.bean;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午4:54
 * To change this template use File | Settings | File Templates.
 */
public class NodeInfoSampleConfigureBean {

	private String nodeName;

	private String updateTime;

	private int timeInterval;

	private boolean processInfo;

	private boolean timeInfo;

	private boolean memoryInfo;

	private boolean swapInfo;

	private boolean diskIOInfo;

	private boolean networkIOInfo;

	private String gathererNodeName;

	private int gathererNodePort;

	private String dataBaseServerIP;

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

	public int getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public boolean isProcessInfo() {
		return processInfo;
	}

	public void setProcessInfo(String processInfo) {
		if (processInfo.equalsIgnoreCase("y")) {
			this.processInfo = true;
		} else {
			this.processInfo = false;
		}
	}

	public boolean isTimeInfo() {
		return timeInfo;
	}

	public void setTimeInfo(String timeInfo) {
		if (timeInfo.equalsIgnoreCase("y")) {
			this.timeInfo = true;
		} else {
			this.timeInfo = false;
		}
	}

	public boolean isMemoryInfo() {
		return memoryInfo;
	}

	public void setMemoryInfo(String memoryInfo) {
		if (memoryInfo.equalsIgnoreCase("y")) {
			this.memoryInfo = true;
		} else {
			this.memoryInfo = false;
		}
	}

	public boolean isSwapInfo() {
		return swapInfo;
	}

	public void setSwapInfo(String swapInfo) {
		if (swapInfo.equalsIgnoreCase("y")) {
			this.swapInfo = true;
		} else {
			this.swapInfo = false;
		}
	}

	public boolean isDiskIOInfo() {
		return diskIOInfo;
	}

	public void setDiskIOInfo(String diskIOInfo) {
		if (diskIOInfo.equalsIgnoreCase("y")) {
			this.diskIOInfo = true;
		} else {
			this.diskIOInfo = false;
		}
	}

	public boolean isNetworkIOInfo() {
		return networkIOInfo;
	}

	public void setNetworkIOInfo(String networkIOInfo) {
		if (networkIOInfo.equalsIgnoreCase("y")) {
			this.networkIOInfo = true;
		} else {
			this.networkIOInfo = false;
		}
	}

	public String getGathererNodeName() {
		return gathererNodeName;
	}

	public void setGathererNodeName(String gathererNodeName) {
		this.gathererNodeName = gathererNodeName;
	}

	public int getGathererNodePort() {
		return gathererNodePort;
	}

	public void setGathererNodePort(int gathererNodePort) {
		this.gathererNodePort = gathererNodePort;
	}

	public String getDataBaseServerIP() {
		return dataBaseServerIP;
	}

	public void setDataBaseServerIP(String dataBaseServerIP) {
		this.dataBaseServerIP = dataBaseServerIP;
	}
}
