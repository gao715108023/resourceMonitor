package com.sinopec.io;

import java.io.Serializable;

public class GroupStatus implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7233282731914049420L;

	private String groupIP;
	
	private String nodeName;

	public String getGroupIP() {
		return groupIP;
	}

	public void setGroupIP(String groupIP) {
		this.groupIP = groupIP;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
}
