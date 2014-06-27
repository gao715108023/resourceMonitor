package com.sinopec.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

import com.sinopec.cache.NodeInfoCache;
import com.sinopec.dao.GroupInfoDao;
import com.sinopec.io.GroupStatus;

public class GroupInfoDaoImpl extends UnicastRemoteObject implements GroupInfoDao {

	private NodeInfoCache nodeInfoCache;

	public GroupInfoDaoImpl(NodeInfoCache nodeInfoCache) throws RemoteException {
		super();
		this.nodeInfoCache = nodeInfoCache;
	}

	@Override
	public String transmitGroupInfo(GroupStatus groupStatus) throws RemoteException {
		// TODO Auto-generated method stub
		handleGroupInfo(groupStatus);
		return nodeInfoCache.getNextGroupIP(groupStatus.getGroupIP());
	}

	private void handleGroupInfo(GroupStatus groupStatus) {
		nodeInfoCache.add(groupStatus.getGroupIP(), new Date());
	}
}
