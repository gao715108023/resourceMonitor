package com.sinopec.dao;

import com.sinopec.io.DynamicInfoSerializable;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午4:10
 * To change this template use File | Settings | File Templates.
 */
public interface DynamicInfoInterface extends Remote {
	public String transmitDynamicInfo(DynamicInfoSerializable dis) throws RemoteException;
}
