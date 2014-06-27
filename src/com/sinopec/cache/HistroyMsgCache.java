package com.sinopec.cache;

import com.sinopec.common.MySQLSQL;
import com.sinopec.common.OracleSQL;
import com.sinopec.io.DynamicInfoSerializable;
import com.sinopec.jdbc.JDBCCommon;
import com.sinopec.utils.DoubleUtils;
import com.sinopec.utils.FloatUtils;
import com.sinopec.utils.LogUtils;
import com.sinopec.utils.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-24 Time: 下午2:58 To
 * change this template use File | Settings | File Templates.
 */
public class HistroyMsgCache {

	private static final Log LOG = LogFactory.getLog(HistroyMsgCache.class);

	/**
	 * 具体内容存放的地方
	 */
	ConcurrentHashMap<String, ConcurrentHashMap<String, DynamicInfoSerializable>> cache;

	private ScheduledExecutorService scheduleService;

	/**
	 * 存储数据的时间间隔，默认为30秒
	 */
	private int storeInterval = 30;

	private JDBCCommon jdbcCommon;

	private String dbType;

	private String dataBaseServerIP;

	public HistroyMsgCache() {
		init();
	}

	public HistroyMsgCache(int storeInterval, JDBCCommon jdbcCommon, String dbType, String dataBaseServerIP) {
		this.storeInterval = storeInterval;
		this.jdbcCommon = jdbcCommon;
		this.dbType = dbType;
		this.dataBaseServerIP = dataBaseServerIP;
		init();
	}

	private void init() {
		cache = new ConcurrentHashMap<String, ConcurrentHashMap<String, DynamicInfoSerializable>>();
		scheduleService = Executors.newScheduledThreadPool(1);
		scheduleService.scheduleAtFixedRate(new StoreHisMsgDataSchedule(cache, jdbcCommon, dbType), 0, storeInterval, TimeUnit.SECONDS);
		if (LOG.isInfoEnabled()) {
			LOG.info("The service of storing history data has started!");
		}
	}

	class StoreHisMsgDataSchedule implements Runnable {

		ConcurrentHashMap<String, ConcurrentHashMap<String, DynamicInfoSerializable>> cache;

		private Connection conn;

		private JDBCCommon jdbcCommon;

		private String dbType;

		private volatile boolean storeData;

		private PreparedStatement pstmtOne;

		private PreparedStatement pstmtThree;

		StoreHisMsgDataSchedule(ConcurrentHashMap<String, ConcurrentHashMap<String, DynamicInfoSerializable>> cache, JDBCCommon jdbcCommon, String dbType) {
			this.cache = cache;
			this.jdbcCommon = jdbcCommon;
			this.dbType = dbType;
			setStoreData(false);

		}

		private void initialOracleDB() throws SQLException {
			conn = jdbcCommon.getOrclConn();// 与Oracle数据库创建连接
			if (conn == null) {
				LOG.info("Can't connect to Oracle-->" + jdbcCommon.getIpAddress());
			} else {
				conn.setAutoCommit(false);
				pstmtOne = conn.prepareStatement(OracleSQL.insertComputeOneMonthSQL);
				pstmtThree = conn.prepareStatement(OracleSQL.insertComputeThreeMonthSQL);
				setStoreData(true);
			}
		}

		private void initialMySQLDB() throws SQLException {
			conn = jdbcCommon.getMySQLConn(dataBaseServerIP);// 与MySQL数据库创建连接
			if (this.conn == null) {
				LOG.info("Can't connect to History-MySQL-->" + dataBaseServerIP);
			} else {
				conn.setAutoCommit(false);
				pstmtOne = conn.prepareStatement(MySQLSQL.insertComputeOneMonthSQL);
				pstmtThree = conn.prepareStatement(MySQLSQL.insertComputeThreeMonthSQL);
				setStoreData(true);
			}
		}

		public void insertComputeNodeHistoryAvgData() throws SQLException {

			// 如果计算节点历史数据不为空，则存储数据
			if (!cache.isEmpty()) {
				Iterator<Map.Entry<String, ConcurrentHashMap<String, DynamicInfoSerializable>>> iter = cache.entrySet().iterator();// 遍历缓存中的历史数据
				while (iter.hasNext()) {
					// 初始化参数
					float oneMinsProcs = 0, fiveMinsProcs = 0, fifteenMinsProcs = 0, userTime = 0, niceTime = 0, systemTime = 0, iowaitTime = 0, idleTime = 0;
					String allUserTime = null, allNiceTime = null, allSystemTime = null, allIowaitTime = null, allIdleTime = null;
					int totalMemory = 0, usedMemory = 0, idleMemory = 0, swapSize = 0, usedSwap = 0, idleSwap = 0, ioTs = 0, readSpeed = 0, readKb = 0, writeSpeed = 0, writeKb = 0;
					double inReceivesPs = 0, inDeliversPs = 0, outRequestsPs = 0, inSegsPs = 0, outSegsPs = 0, retransSegsPs = 0, inDatagramsPs = 0, outDatagramsPs = 0;
					float[] allUserTimeArray = null;
					float[] allNiceTimeArray = null;
					float[] allSystemTimeArray = null;
					float[] allIowaitTimeArray = null;
					float[] allIdleTimeArray = null;
					boolean first = true; // 初始化数组的开关
					Map.Entry<String, ConcurrentHashMap<String, DynamicInfoSerializable>> entry = iter.next();
					String nodeName = entry.getKey();// 获取节点名
					Map<String, DynamicInfoSerializable> computeNodeStatusOneMonthHisMap = entry.getValue();// 获取该节点的给定时间段内的历史数据
					int size = computeNodeStatusOneMonthHisMap.size();// 在给定时间段内采集的数据大小
					Iterator<Map.Entry<String, DynamicInfoSerializable>> iter1 = computeNodeStatusOneMonthHisMap.entrySet().iterator();
					// 遍历该时间段内的所有数据
					while (iter1.hasNext()) {
						Map.Entry<String, DynamicInfoSerializable> entry1 = iter1.next();
						DynamicInfoSerializable dis = entry1.getValue();

						// 将该时间段内的所有数据相加
						oneMinsProcs += dis.getOneMinsProcs();
						fiveMinsProcs += dis.getFiveMinsProcs();
						fifteenMinsProcs += dis.getFifteenMinsProcs();

						userTime += dis.getUserTime();
						niceTime += dis.getNiceTime();
						systemTime += dis.getSystemTime();
						iowaitTime += dis.getIowaitTime();
						idleTime += dis.getIdleTime();

						// 如果是第一次遍历，则初始化数组大小
						if (first) {
							int arraySize = getArraySize(dis.getAllUserTime());// 获取数组的初始化大小
							allUserTimeArray = new float[arraySize];
							allNiceTimeArray = new float[arraySize];
							allSystemTimeArray = new float[arraySize];
							allIowaitTimeArray = new float[arraySize];
							allIdleTimeArray = new float[arraySize];
							first = false;// 关闭数组的初始化
						}

						// 将字符串转化为数组
						stringToArray(dis.getAllUserTime(), allUserTimeArray);
						stringToArray(dis.getAllNiceTime(), allNiceTimeArray);
						stringToArray(dis.getAllSystemTime(), allSystemTimeArray);
						stringToArray(dis.getAllIowaitTime(), allIowaitTimeArray);
						stringToArray(dis.getAllIdleTime(), allIdleTimeArray);

						totalMemory += dis.getTotalMemory();
						usedMemory += dis.getUsedMemory();
						idleMemory += dis.getIdleMemory();

						swapSize += dis.getSwapSize();
						usedSwap += dis.getUsedSwap();
						idleSwap += dis.getIdleSwap();

						ioTs += dis.getIoTs();
						readSpeed += dis.getReadSpeed();
						readKb += dis.getReadKB();
						writeSpeed += dis.getWriteSpeed();
						writeKb += dis.getWriteKB();

						inReceivesPs += dis.getInReceivesPs();
						inDeliversPs += dis.getInDeliversPs();
						outRequestsPs += dis.getOutRequestsPs();
						inSegsPs += dis.getInSegsPs();
						outSegsPs += dis.getOutSegsPs();
						retransSegsPs += dis.getRetransSegsPs();
						inDatagramsPs += dis.getInDatagramsPs();
						outDatagramsPs += dis.getOutDatagramsPs();
					}

					// 计算在该时间段内的历史数据的平均值
					oneMinsProcs /= size;
					fiveMinsProcs /= size;
					fifteenMinsProcs /= size;

					userTime /= size;
					niceTime /= size;
					systemTime /= size;
					iowaitTime /= size;
					idleTime /= size;

					// 计算数组中的平均值
					calculateArrayFloat(allUserTimeArray, size);
					calculateArrayFloat(allNiceTimeArray, size);
					calculateArrayFloat(allSystemTimeArray, size);
					calculateArrayFloat(allIowaitTimeArray, size);
					calculateArrayFloat(allIdleTimeArray, size);

					totalMemory /= size;
					usedMemory /= size;
					idleMemory /= size;

					swapSize /= size;
					usedSwap /= size;
					idleSwap /= size;

					ioTs /= size;
					readSpeed /= size;
					readKb /= size;
					writeSpeed /= size;
					writeKb /= size;

					inReceivesPs /= size;
					inDeliversPs /= size;
					outRequestsPs /= size;
					inSegsPs /= size;
					outSegsPs /= size;
					retransSegsPs /= size;
					inDatagramsPs /= size;
					outDatagramsPs /= size;

					// 将数据转化为小数点只保留两位的格式
					oneMinsProcs = FloatUtils.convert(oneMinsProcs);
					fiveMinsProcs = FloatUtils.convert(fiveMinsProcs);
					fifteenMinsProcs = FloatUtils.convert(fifteenMinsProcs);

					userTime = FloatUtils.convert(userTime);
					niceTime = FloatUtils.convert(niceTime);
					systemTime = FloatUtils.convert(systemTime);
					iowaitTime = FloatUtils.convert(iowaitTime);
					idleTime = FloatUtils.convert(idleTime);

					allUserTime = arrayToString(allUserTimeArray);
					allNiceTime = arrayToString(allNiceTimeArray);
					allSystemTime = arrayToString(allSystemTimeArray);
					allIowaitTime = arrayToString(allIowaitTimeArray);
					allIdleTime = arrayToString(allIdleTimeArray);

					inReceivesPs = DoubleUtils.convert(inReceivesPs);
					inDeliversPs = DoubleUtils.convert(inDeliversPs);
					outRequestsPs = DoubleUtils.convert(outRequestsPs);
					inSegsPs = DoubleUtils.convert(inSegsPs);
					outSegsPs = DoubleUtils.convert(outSegsPs);
					retransSegsPs = DoubleUtils.convert(retransSegsPs);
					inDatagramsPs = DoubleUtils.convert(inDatagramsPs);
					outDatagramsPs = DoubleUtils.convert(outDatagramsPs);

					// 将数据存储至数据库中
					pstmtOne.setString(1, nodeName);
					pstmtOne.setString(2, TimeUtil.getString());
					pstmtOne.setFloat(3, oneMinsProcs);
					pstmtOne.setFloat(4, fiveMinsProcs);
					pstmtOne.setFloat(5, fifteenMinsProcs);
					pstmtOne.setFloat(6, userTime);
					pstmtOne.setFloat(7, niceTime);
					pstmtOne.setFloat(8, systemTime);
					pstmtOne.setFloat(9, iowaitTime);
					pstmtOne.setFloat(10, idleTime);
					pstmtOne.setString(11, allUserTime);
					pstmtOne.setString(12, allNiceTime);
					pstmtOne.setString(13, allSystemTime);
					pstmtOne.setString(14, allIowaitTime);
					pstmtOne.setString(15, allIdleTime);
					pstmtOne.setInt(16, totalMemory);
					pstmtOne.setInt(17, usedMemory);
					pstmtOne.setInt(18, idleMemory);
					pstmtOne.setInt(19, swapSize);
					pstmtOne.setInt(20, usedSwap);
					pstmtOne.setInt(21, idleSwap);
					pstmtOne.setInt(22, ioTs);
					pstmtOne.setInt(23, readSpeed);
					pstmtOne.setInt(24, readKb);
					pstmtOne.setInt(25, writeSpeed);
					pstmtOne.setInt(26, writeKb);
					pstmtOne.setDouble(27, inReceivesPs);
					pstmtOne.setDouble(28, inDeliversPs);
					pstmtOne.setDouble(29, outRequestsPs);
					pstmtOne.setDouble(30, inSegsPs);
					pstmtOne.setDouble(31, outSegsPs);
					pstmtOne.setDouble(32, retransSegsPs);
					pstmtOne.setDouble(33, inDatagramsPs);
					pstmtOne.setDouble(34, outDatagramsPs);
					pstmtOne.addBatch();
					// MaintainInfo.computeNodeStatusHistoryCount++;
					pstmtThree.setString(1, nodeName);
					pstmtThree.setString(2, TimeUtil.getString());
					pstmtThree.setFloat(3, oneMinsProcs);
					pstmtThree.setFloat(4, fiveMinsProcs);
					pstmtThree.setFloat(5, fifteenMinsProcs);
					pstmtThree.setFloat(6, userTime);
					pstmtThree.setFloat(7, niceTime);
					pstmtThree.setFloat(8, systemTime);
					pstmtThree.setFloat(9, iowaitTime);
					pstmtThree.setFloat(10, idleTime);
					pstmtThree.setString(11, allUserTime);
					pstmtThree.setString(12, allNiceTime);
					pstmtThree.setString(13, allSystemTime);
					pstmtThree.setString(14, allIowaitTime);
					pstmtThree.setString(15, allIdleTime);
					pstmtThree.setInt(16, totalMemory);
					pstmtThree.setInt(17, usedMemory);
					pstmtThree.setInt(18, idleMemory);
					pstmtThree.setInt(19, swapSize);
					pstmtThree.setInt(20, usedSwap);
					pstmtThree.setInt(21, idleSwap);
					pstmtThree.setInt(22, ioTs);
					pstmtThree.setInt(23, readSpeed);
					pstmtThree.setInt(24, readKb);
					pstmtThree.setInt(25, writeSpeed);
					pstmtThree.setInt(26, writeKb);
					pstmtThree.setDouble(27, inReceivesPs);
					pstmtThree.setDouble(28, inDeliversPs);
					pstmtThree.setDouble(29, outRequestsPs);
					pstmtThree.setDouble(30, inSegsPs);
					pstmtThree.setDouble(31, outSegsPs);
					pstmtThree.setDouble(32, retransSegsPs);
					pstmtThree.setDouble(33, inDatagramsPs);
					pstmtThree.setDouble(34, outDatagramsPs);
					pstmtThree.addBatch();
					// MaintainInfo.computeNodeStatusHistoryCount++;
				}
				pstmtOne.executeBatch();
				pstmtThree.executeBatch();
				conn.commit();
				pstmtOne.clearBatch();
				pstmtOne.clearBatch();
				// MaintainInfo.computeNodeStatusHistoryStatus = true;//
				// 操作计算历史数据库状态正常
				// 清空计算节点历史数据缓存
				clear();
			}
		}

		private int getArraySize(String str) {
			int size;
			if (str.contains("@@")) {
				String[] array = str.split("@@");
				size = array.length;
			} else {
				size = 1;
			}
			return size;
		}

		private void calculateArrayFloat(float[] arrayFloat, int size) {
			for (int i = 0; i < arrayFloat.length; i++) {
				arrayFloat[i] /= size;
			}
		}

		private void stringToArray(String str, float[] arrayFloat) {
			if (str.contains("@@")) {
				String[] array = str.split("@@");
				for (int i = 0; i < array.length; i++) {
					arrayFloat[i] += Float.parseFloat(array[i]);
				}
			} else {
				arrayFloat[0] += Float.parseFloat(str);
			}
		}

		private String arrayToString(float[] arrayFloat) {
			String str = null;
			if (arrayFloat.length > 1) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < arrayFloat.length; i++) {
					sb.append(FloatUtils.convert(arrayFloat[i])).append("@@");
				}
				String result = sb.toString();
				int len = result.length();
				str = result.substring(0, len - 2);
			} else {
				str = String.valueOf(FloatUtils.convert(arrayFloat[0]));
			}
			return str;
		}

		boolean isStoreData() {
			return storeData;
		}

		void setStoreData(boolean storeData) {
			this.storeData = storeData;
		}

		private boolean dbTypeIsMySQL() {
			if (dbType.equalsIgnoreCase("MySQL")) {
				return true;
			} else {
				return false;
			}
		}

		private boolean dbTypeIsOracle() {
			if (dbType.equalsIgnoreCase("Oracle")) {
				return true;
			} else {
				return false;
			}
		}

		private void closeConn() {
			try {
				if (pstmtOne != null)
					pstmtOne.close();
				if (pstmtThree != null)
					pstmtThree.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOG.error(LogUtils.getTrace(e));
			}
		}

		@Override
		public void run() {
			// To change body of implemented methods use File | Settings | File
			// Templates.
			try {
				if (isStoreData()) {
					insertComputeNodeHistoryAvgData();
				} else {
					if (dbTypeIsMySQL()) {
						initialMySQLDB();
					} else if (dbTypeIsOracle()) {
						initialOracleDB();
					}
				}
			} catch (SQLException e) {
				LOG.error("The status of storing real-time data:STOPPED"); // To
																			// change
																			// body
																			// of
																			// catch
																			// statement
																			// use
																			// File
																			// |
																			// Settings
																			// |
																			// File
																			// Templates.
				setStoreData(false);
				closeConn();
			}
		}
	}

	public void put(String key, DynamicInfoSerializable value) {
		String updateTime = value.getUpdateTime();
		if (cache.containsKey(key)) {
			cache.get(key).put(updateTime, value);
		} else {
			ConcurrentHashMap<String, DynamicInfoSerializable> timeMap = new ConcurrentHashMap<String, DynamicInfoSerializable>();
			timeMap.put(updateTime, value);
			cache.put(key, timeMap);
		}
	}

	public DynamicInfoSerializable get(String key1, String key2) {
		return cache.get(key1).get(key2);
	}

	public DynamicInfoSerializable remove(String key1, String key2) {
		return cache.get(key1).remove(key2);
	}

	public boolean clear() {
		if (cache != null)
			cache.clear();
		return true; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	public int size() {
		return cache.size(); // To change body of implemented methods use File |
								// Settings | File Templates.
	}

	public Set<String> keySet() {
		return cache.keySet(); // To change body of implemented methods use File
								// | Settings | File Templates.
	}

	public Collection<DynamicInfoSerializable> values(String key) {
		return cache.get(key).values(); // To change body of implemented methods
										// use File | Settings | File Templates.
	}

	public boolean containsKey(String key) {
		return cache.containsKey(key); // To change body of implemented methods
										// use File | Settings | File Templates.
	}

	public void destroy() {
		if (cache != null)
			cache.clear();
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

}
