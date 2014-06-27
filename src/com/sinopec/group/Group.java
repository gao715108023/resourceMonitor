package com.sinopec.group;

import com.sinopec.bean.AdjacentGroup;
import com.sinopec.cache.InstantMsgCache;
import com.sinopec.cache.HistroyMsgCache;
import com.sinopec.common.Constants;
import com.sinopec.group.rpc.DynamicInfoServer;
import com.sinopec.group.rpc.TopologyClient;
import com.sinopec.jdbc.DBOpt;
import com.sinopec.jdbc.JDBCCommon;
import com.sinopec.utils.ConfigUtils;
import com.sinopec.utils.HostUtil;
import com.sinopec.utils.LogUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-23 Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class Group {

	private static final Log LOG = LogFactory.getLog(Group.class);

	public void startDynamicInfoListen(int port, InstantMsgCache dynamicCache, HistroyMsgCache histroyMsgCache, AdjacentGroup adjacentGroup) {
		new Thread(new DynamicInfoServer(port, dynamicCache, histroyMsgCache, adjacentGroup)).start();
	}

	public void startThreadAsClient(String crontrolNodeIP, int port, int samplingInterval, String localhostIP, AdjacentGroup adjacentGroup) {
		new Thread(new TopologyClient(crontrolNodeIP, port, samplingInterval * 1000, localhostIP, adjacentGroup)).start();
	}

	private ConfigUtils getConfigUtils(String filePath) {
		return new ConfigUtils(filePath);
	}

	private boolean isMySQL(String dbType) {
		if (dbType.equalsIgnoreCase("mysql")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isOracle(String dbType) {
		if (dbType.equalsIgnoreCase("Oracle")) {
			return true;
		} else {
			return false;
		}
	}

	public void start() {
		String nodeName = HostUtil.getHostName();
		ConfigUtils configUtils = getConfigUtils("/opt/CRMS/sd/conf/infoconfig.properties");
		int portForDynamicInfo = configUtils.getInt("port_for_dynamic_info");
		int storeInterval = configUtils.getInt("store_InstantMsg_Interval");
		int storeHistoryMsgInterval = configUtils.getInt("store_historyMsg_interval");
		int expiryInterval = configUtils.getInt("expiryInterval");
		String dbType = configUtils.getString("dbtype");
		String crontrolNodeIP = configUtils.getString("crontrolnode");
		int portForCrontrolNode = configUtils.getInt("port_for_crontrolnode");
		int samplingIntervalForCrontrolnode = configUtils.getInt("samplingInterval_for_crontrolnode");
		Constants.isDebug = configUtils.getInt("debug");

		if (LogUtils.enableDEBUG()) {
			LOG.info("The port for dynamic info: " + portForDynamicInfo);
			LOG.info("Debug: " + Constants.isDebug);
			LOG.info("The type of Database: " + dbType);
			LOG.info("The interval of store instant data: " + storeInterval + "s");
			LOG.info("Agent timeout: " + expiryInterval + "m");
			LOG.info("The port for CrontrolNode: " + portForCrontrolNode);
		}

		JDBCCommon jdbcCommon = null;

		if (isOracle(dbType)) {
			String oracleIPAddress = configUtils.getString("oracle_IPAddress"); // 获取Oracle的IP地址
			String oracleUser = configUtils.getString("oracle_user"); // 获取Oracle的用户名
			String oraclePassword = configUtils.getString("oracle_password"); // 获取Oracle的用户密码
			String oracleDataBaseName = configUtils.getString("oracle_databaseName"); // 获取Oracle的数据库名

			if (LogUtils.enableDEBUG()) {
				LOG.info("The IP of Oracle: " + oracleIPAddress);
				LOG.info("Oracle users:" + oracleUser);
				LOG.info("Oracle user passwords:" + oraclePassword);
				LOG.info("Oracle database:" + oracleDataBaseName);
			}
		}

		String dataBaseServerIP = null;

		if (isMySQL(dbType)) { // 如果数据库类型是MySQL
			String mySQLIPAddress = configUtils.getString("mysql_IPAddress"); // 获取Mysql数据库的IP地址

			String mySQLUser = configUtils.getString("mysql_user"); // 获取Mysql的用户名

			String mySQLPasswd = configUtils.getString("mysql_password"); // 获取Mysql的用户密码

			String mySQLDatabaseName = configUtils.getString("mysql_databaseName"); // 获取Mysql的数据库名称

			DBOpt dbOpt = new DBOpt(mySQLIPAddress, mySQLUser, mySQLPasswd, mySQLDatabaseName);
			dataBaseServerIP = dbOpt.getDataBaseServerIP(nodeName);

			jdbcCommon = new JDBCCommon(mySQLIPAddress, mySQLUser, mySQLPasswd, mySQLDatabaseName);

			if (LogUtils.enableDEBUG()) {
				LOG.info("The IP of MySQL:" + mySQLIPAddress);
				LOG.info("MySQL users:" + mySQLUser);
				LOG.info("MySQL password: " + mySQLPasswd);
				LOG.info("The name of database:" + mySQLDatabaseName);
				LOG.info("The DB's IP of storing history data: " + dataBaseServerIP);
			}
		}

		AdjacentGroup adjacentGroup = new AdjacentGroup();
		InstantMsgCache dynamicCache = new InstantMsgCache(storeInterval, expiryInterval, jdbcCommon, dbType);
		HistroyMsgCache histroyMsgCache = new HistroyMsgCache(storeHistoryMsgInterval, jdbcCommon, dbType, dataBaseServerIP);
		startThreadAsClient(crontrolNodeIP, portForCrontrolNode, samplingIntervalForCrontrolnode, HostUtil.getLocalHostIP(), adjacentGroup);
		startDynamicInfoListen(portForDynamicInfo, dynamicCache, histroyMsgCache, adjacentGroup);
	}

	public static void main(String[] args) {
		Group group = new Group();
		group.start();
	}
}
