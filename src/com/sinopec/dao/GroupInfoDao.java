package com.sinopec.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sinopec.io.GroupStatus;

public interface GroupInfoDao extends Remote {
	public String transmitGroupInfo(GroupStatus groupStatusBean) throws RemoteException;
}
