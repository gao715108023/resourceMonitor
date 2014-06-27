package com.sinopec.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-30 Time: 下午2:51 To
 * change this template use File | Settings | File Templates.
 */
public class DBOpt {

	private String ipAddress;

	private String dataBaseName;

	private String user;

	private String password;

	public DBOpt(String ipAddress, String user, String password, String dataBaseName) {
		this.ipAddress = ipAddress;
		this.user = user;
		this.password = password;
		this.dataBaseName = dataBaseName;
	}

	public String getDataBaseServerIP(String gathererNodeName) {
		String dataBaseServerIP = null;
		String selectDataBaseServerIP = "select distinct DataBaseServerIP from NodeInfoSampleConfigure where GathererNodeName='" + gathererNodeName + "'";
		JDBCCommon jdbcCommon = new JDBCCommon(ipAddress, user, password, dataBaseName);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		conn = jdbcCommon.getMySQLConn();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectDataBaseServerIP);
			while (rs.next()) {
				dataBaseServerIP = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
		}
		return dataBaseServerIP;
	}
}
