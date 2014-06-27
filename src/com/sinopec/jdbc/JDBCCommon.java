package com.sinopec.jdbc;

import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-29 Time: 下午5:34 To
 * change this template use File | Settings | File Templates.
 */
public class JDBCCommon {

	private static final Log LOG = LogFactory.getLog(JDBCCommon.class);

	private String ipAddress;

	private String dataBaseName;

	private String user;

	private String password;

	public JDBCCommon(String ipAddress, String user, String password, String dataBaseName) {
		this.ipAddress = ipAddress;
		this.user = user;
		this.password = password;
		this.dataBaseName = dataBaseName;
	}

	public Connection getOrclConn() {

		String url = "jdbc:oracle:thin:@" + ipAddress + ":1521:" + dataBaseName; // 连接数据库的url地址

		Connection conn = null;
		try {
			// 加载JDBC驱动
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			// 创建数据库连接
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			LOG.error(LogUtils.getTrace(e));
		}
		return conn;
	}

	public Connection getMySQLConn() {

		String url = "jdbc:mysql://" + ipAddress + ":3306/" + dataBaseName; // 连接数据库的url地址
		Connection conn = null;
		try {
			// 加载JDBC驱动
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// 创建数据库连接
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			LOG.error(LogUtils.getTrace(e));
		}
		return conn;
	}

	public Connection getMySQLConn(String ipAddress) {

		String url = "jdbc:mysql://" + ipAddress + ":3306/" + dataBaseName; // 连接数据库的url地址
		Connection conn = null;
		try {
			// 加载JDBC驱动
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// 创建数据库连接
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			LOG.error(LogUtils.getTrace(e));
		}
		return conn;
	}

	public String getIpAddress() {
		return ipAddress;
	}
}
