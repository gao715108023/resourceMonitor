package com.sinopec.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-24 Time: 上午8:35 To
 * change this template use File | Settings | File Templates.
 */
public class HostUtil {

	private static final Log LOG = LogFactory.getLog(HostUtil.class);

	public static String getHostName() {
		String hostName = null;
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			hostName = inetAddress.getHostName();
		} catch (UnknownHostException e) {
			LOG.error(LogUtils.getTrace(e)); // To change body of catch
												// statement use File | Settings
												// | File Templates.
		}
		return hostName;
	}

	public static String getLocalHostIP() {
		String localHostIP = null;
		Enumeration<?> e1;
		try {
			e1 = NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				Enumeration<?> e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					if (ia instanceof Inet6Address)
						continue;
					localHostIP = ia.getHostAddress();
					if (localHostIP == null)
						continue;
					else
						break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		return localHostIP;
	}
}
