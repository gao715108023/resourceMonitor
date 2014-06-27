package com.sinopec.cache;

import com.sinopec.bean.ComputeNodeStatusBean;
import com.sinopec.common.SQLConstants;
import com.sinopec.io.DynamicInfoSerializable;
import com.sinopec.jdbc.JDBCCommon;
import com.sinopec.mybatis.MybatisCommon;
import com.sinopec.test.PrintTestInfo;
import com.sinopec.utils.BeanUtil;
import com.sinopec.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-24 Time: 上午11:17 To
 * change this template use File | Settings | File Templates.
 */
public class InstantMsgCache implements ICache<String, Object> {

	private static final Log LOG = LogFactory.getLog(InstantMsgCache.class);

	/**
	 * 具体内容存放的地方
	 */
	ConcurrentHashMap<String, DynamicInfoSerializable> cache;

	/**
	 * 超期信息存储
	 */
	ConcurrentHashMap<String, Long> expiryCache;

	/**
	 * 清理超期内容的服务
	 */
	private ScheduledExecutorService scheduleService;

	/**
	 * 存储数据的时间间隔，默认为5秒
	 */
	private int storeInterval = 5;

	/**
	 * 清理超期信息的时间间隔，默认10分钟
	 */
	private int expiryInterval = 10;

	private JDBCCommon jdbcCommon;

	private String dbType;

	public InstantMsgCache() {
		init();
	}

	public InstantMsgCache(int expiryInterval) {
		this.expiryInterval = expiryInterval;
		init();
	}

	public InstantMsgCache(int storeInterval, int expiryInterval) {
		this.storeInterval = storeInterval;
		this.expiryInterval = expiryInterval;
		init();
	}

	public InstantMsgCache(int storeInterval, int expiryInterval, JDBCCommon jdbcCommon, String dbType) {
		this.storeInterval = storeInterval;
		this.expiryInterval = expiryInterval;
		this.jdbcCommon = jdbcCommon;
		this.dbType = dbType;
		init();
	}

	private void init() {
		// catches = new ConcurrentHashMap[moduleSize];
		// for (int i = 0; i < moduleSize; i++)
		// catches[i] = new ConcurrentHashMap<String, Object>();
		cache = new ConcurrentHashMap<String, DynamicInfoSerializable>();
		expiryCache = new ConcurrentHashMap<String, Long>();
		scheduleService = Executors.newScheduledThreadPool(2);
		scheduleService.scheduleAtFixedRate(new CheckOutOfDateSchedule(cache, expiryCache), 0, expiryInterval * 60, TimeUnit.SECONDS);
		// storeToDBService = Executors.newScheduledThreadPool(1);
		scheduleService.scheduleAtFixedRate(new StoreInstantMsgDataSchedule(cache, jdbcCommon, dbType), 0, storeInterval, TimeUnit.SECONDS);
		if (LOG.isInfoEnabled()) {
			LOG.info("The service of checking Agent timeout has started!");
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("The service of storing instant data has started!");
		}
	}

	class CheckOutOfDateSchedule implements Runnable {

		/**
		 * 具体内容存放的地方
		 */
		ConcurrentHashMap<String, DynamicInfoSerializable> cache;

		ConcurrentHashMap<String, Long> expiryCache;

		CheckOutOfDateSchedule(ConcurrentHashMap<String, DynamicInfoSerializable> cache, ConcurrentHashMap<String, Long> expiryCache) {
			this.cache = cache;
			this.expiryCache = expiryCache;
		}

		public void check() {
			// for (ConcurrentHashMap<String, Object> cache : catches) {
			PrintTestInfo.printInstantMsg(cache);
			Iterator<String> keys = cache.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (expiryCache.get(key) == null)
					continue;
				long date = expiryCache.get(key);
				if ((date > 0) && (new Date(date).before(new Date()))) {
					expiryCache.remove(key);
					cache.remove(key);
				}
				// }
			}
		}

		@Override
		public void run() {
			// To change body of implemented methods use File | Settings | File
			// Templates.
			check();
		}
	}

	class StoreInstantMsgDataSchedule implements Runnable {

		ConcurrentHashMap<String, DynamicInfoSerializable> cache;

		private Connection conn;
		private Statement stmt;
		private JDBCCommon jdbcCommon;
		private String dbType;
		private volatile boolean storeData;

		StoreInstantMsgDataSchedule(ConcurrentHashMap<String, DynamicInfoSerializable> cache) {
			this.cache = cache;
		}

		StoreInstantMsgDataSchedule(ConcurrentHashMap<String, DynamicInfoSerializable> cache, JDBCCommon jdbcCommon, String dbType) {
			this.cache = cache;
			this.jdbcCommon = jdbcCommon;
			this.dbType = dbType;
			setStoreData(false);
		}

		public void updateComputeNodeStatusOrcl() throws SQLException {
			// 如果computeNodeCache不为空，则遍历Map函数
			if (!cache.isEmpty()) {
				Iterator<Map.Entry<String, DynamicInfoSerializable>> iter = cache.entrySet().iterator();

				while (iter.hasNext()) {
					Map.Entry<String, DynamicInfoSerializable> entry = iter.next();

					DynamicInfoSerializable dis = entry.getValue();

					// 取出信息
					String nodeName = dis.getNodeName();
					String updateTime = dis.getUpdateTime();
					float oneMinsProcs = dis.getOneMinsProcs();
					float fiveMinsProcs = dis.getFiveMinsProcs();
					float fifteenMinsProcs = dis.getFifteenMinsProcs();
					float userTime = dis.getUserTime();
					float niceTime = dis.getNiceTime();
					float systemTime = dis.getSystemTime();
					float iowaitTime = dis.getIowaitTime();
					float idleTime = dis.getIdleTime();
					String allUserTime = dis.getAllUserTime();
					String allNiceTime = dis.getAllNiceTime();
					String allSystemTime = dis.getAllSystemTime();
					String allIowaitTime = dis.getAllIowaitTime();
					String allIdleTime = dis.getAllIdleTime();
					int totalMemory = dis.getTotalMemory();
					int usedMemory = dis.getUsedMemory();
					int idleMemory = dis.getIdleMemory();
					int swapSize = dis.getSwapSize();
					int usedSwap = dis.getUsedSwap();
					int idleSwap = dis.getIdleSwap();
					int ioTs = dis.getIoTs();
					int readSpeed = dis.getReadSpeed();
					int readKb = dis.getReadKB();
					int writeSpeed = dis.getWriteSpeed();
					int writeKb = dis.getWriteKB();
					double inReceivesPs = dis.getInReceivesPs();
					double inDeliversPs = dis.getInDeliversPs();
					double outRequestsPs = dis.getOutRequestsPs();
					double inSegsPs = dis.getInSegsPs();
					double outSegsPs = dis.getOutSegsPs();
					double retransSegsPs = dis.getRetransSegsPs();
					double inDatagramsPs = dis.getInDatagramsPs();
					double outDatagramsPs = dis.getOutDatagramsPs();
					stmt.executeUpdate(SQLConstants.updateComputeNodeStatusOrcl(nodeName, updateTime, oneMinsProcs, fiveMinsProcs, fifteenMinsProcs, userTime, niceTime, systemTime, iowaitTime, idleTime, allUserTime, allNiceTime, allSystemTime, allIowaitTime, allIdleTime, totalMemory, usedMemory, idleMemory, swapSize, usedSwap, idleSwap, ioTs, readSpeed, readKb, writeSpeed, writeKb, inReceivesPs, inDeliversPs, outRequestsPs, inSegsPs, outSegsPs, retransSegsPs, inDatagramsPs, outDatagramsPs));
					conn.commit();
					// MaintainInfo.computeNodeStatusCount++;
				}
			}
		}

		public void updateComputeNodeStatusMySQL() throws SQLException {
			// 如果computeNodeCache不为空，则遍历Map函数
			if (!cache.isEmpty()) {
				Iterator<Map.Entry<String, DynamicInfoSerializable>> iter = cache.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, DynamicInfoSerializable> entry = iter.next();

					DynamicInfoSerializable dis = entry.getValue();

					// 取出信息
					String nodeName = dis.getNodeName();
					String updateTime = dis.getUpdateTime();
					float oneMinsProcs = dis.getOneMinsProcs();
					float fiveMinsProcs = dis.getFiveMinsProcs();
					float fifteenMinsProcs = dis.getFifteenMinsProcs();
					float userTime = dis.getUserTime();
					float niceTime = dis.getNiceTime();
					float systemTime = dis.getSystemTime();
					float iowaitTime = dis.getIowaitTime();
					float idleTime = dis.getIdleTime();
					String allUserTime = dis.getAllUserTime();
					String allNiceTime = dis.getAllNiceTime();
					String allSystemTime = dis.getAllSystemTime();
					String allIowaitTime = dis.getAllIowaitTime();
					String allIdleTime = dis.getAllIdleTime();
					int totalMemory = dis.getTotalMemory();
					int usedMemory = dis.getUsedMemory();
					int idleMemory = dis.getIdleMemory();
					int swapSize = dis.getSwapSize();
					int usedSwap = dis.getUsedSwap();
					int idleSwap = dis.getIdleSwap();
					int ioTs = dis.getIoTs();
					int readSpeed = dis.getReadSpeed();
					int readKb = dis.getReadKB();
					int writeSpeed = dis.getWriteSpeed();
					int writeKb = dis.getWriteKB();
					double inReceivesPs = dis.getInReceivesPs();
					double inDeliversPs = dis.getInDeliversPs();
					double outRequestsPs = dis.getOutRequestsPs();
					double inSegsPs = dis.getInSegsPs();
					double outSegsPs = dis.getOutSegsPs();
					double retransSegsPs = dis.getRetransSegsPs();
					double inDatagramsPs = dis.getInDatagramsPs();
					double outDatagramsPs = dis.getOutDatagramsPs();
					stmt.executeUpdate(SQLConstants.updateComputeNodeStatusMySQL(nodeName, updateTime, oneMinsProcs, fiveMinsProcs, fifteenMinsProcs, userTime, niceTime, systemTime, iowaitTime, idleTime, allUserTime, allNiceTime, allSystemTime, allIowaitTime, allIdleTime, totalMemory, usedMemory, idleMemory, swapSize, usedSwap, idleSwap, ioTs, readSpeed, readKb, writeSpeed, writeKb, inReceivesPs, inDeliversPs, outRequestsPs, inSegsPs, outSegsPs, retransSegsPs, inDatagramsPs, outDatagramsPs));
					conn.commit();
					// MaintainInfo.computeNodeStatusCount++;
				}
			}
		}

		private boolean dbTypeIsOracle() {
			if (dbType.equalsIgnoreCase("Oracle")) {
				return true;
			} else {
				return false;
			}
		}

		private boolean dbTypeIsMySQL() {
			if (dbType.equalsIgnoreCase("MySQL")) {
				return true;
			} else {
				return false;
			}
		}

		private void initialOracleDB() throws SQLException {
			conn = jdbcCommon.getOrclConn();
			if (conn == null) {
				LOG.error("Can't connect to Oracle-->" + jdbcCommon.getIpAddress());
			} else {
				LOG.info("Sucessfully connect to Oracle-->" + jdbcCommon.getIpAddress());
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				setStoreData(true);
			}
		}

		private void initialMySQLDB() throws SQLException {
			conn = jdbcCommon.getMySQLConn();
			if (conn == null) {
				LOG.error("Can't connect to MySQL-->" + jdbcCommon.getIpAddress());
			} else {
				LOG.info("Sucessfully connect to MySQL-->" + jdbcCommon.getIpAddress());
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				setStoreData(true);
			}
		}

		private void closeConn() {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOG.error(LogUtils.getTrace(e));
			}
		}

		public void offerService() {
			System.out.println("test");
			Iterator<Map.Entry<String, DynamicInfoSerializable>> iterator = cache.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, DynamicInfoSerializable> entry = iterator.next();
				String nodeName = entry.getKey();
				DynamicInfoSerializable dis = entry.getValue();
				ComputeNodeStatusBean computeNodeStatusBean = new ComputeNodeStatusBean();
				BeanUtil.copyProperties(dis, computeNodeStatusBean);
				PrintTestInfo.printInstantCacheMsg(computeNodeStatusBean);
				int index = MybatisCommon.updateComputeNodeStatus(nodeName, computeNodeStatusBean);
				// int index =
				// MybatisCommon.insertComputeNodeStatus(computeNodeStatusBean);
				MybatisCommon.commitSession();
				System.out.println(index);
			}
		}

		boolean isStoreData() {
			return storeData;
		}

		void setStoreData(boolean storeData) {
			this.storeData = storeData;
		}

		@Override
		public void run() {
			try {
				if (isStoreData()) {
					if (dbTypeIsMySQL()) {
						updateComputeNodeStatusMySQL();
					} else if (dbTypeIsOracle()) {
						updateComputeNodeStatusOrcl();
					}
				} else {
					if (dbTypeIsMySQL()) {
						initialMySQLDB();
					} else if (dbTypeIsOracle()) {
						initialOracleDB();
					}
				}
			} catch (SQLException e) {
				LOG.error("The status of storing real-time data:STOPPED"); 
				e.printStackTrace();
				setStoreData(false);
				closeConn();
			}
		}
	}

	@Override
	public DynamicInfoSerializable put(String key, DynamicInfoSerializable value) {
		DynamicInfoSerializable result = cache.put(key, value);
		expiryCache.put(key, (long) -1);
		return result; 
	}

	@Override
	public Object put(String key, DynamicInfoSerializable value, Date expiry) {
		Object result = cache.put(key, value);
		expiryCache.put(key, expiry.getTime());
		return result; 
	}

	@Override
	public DynamicInfoSerializable put(String key, DynamicInfoSerializable value, int TTL) {
		DynamicInfoSerializable result = cache.put(key, value);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, TTL);
		expiryCache.put(key, calendar.getTime().getTime());
		return result;
	}

	@Override
	public Object get(String key) {
		checkValidate(key);
		return cache.get(key);
	}

	@Override
	public Object remove(String key) {
		Object result = cache.remove(key);
		expiryCache.remove(key);
		return result; 
	}

	@Override
	public boolean clear() {
		if (cache != null)
			cache.clear();
		if (expiryCache != null)
			expiryCache.clear();
		return true; 
	}

	@Override
	public int size() {
		checkAll();
		return expiryCache.size(); 
	}

	@Override
	public Set<String> keySet() {
		checkAll();
		return expiryCache.keySet(); 
	}

	@Override
	public Collection<DynamicInfoSerializable> values() {
		checkAll();
		return cache.values(); 
	}

	@Override
	public boolean containsKey(String key) {
		checkValidate(key);
		return cache.containsKey(key); 
	}

	@Override
	public void destroy() {
		clear();
		if (scheduleService != null)
			scheduleService.shutdown();
		scheduleService = null;
	}

	private void checkValidate(String key) {
		if (key != null && expiryCache.get(key) != null && expiryCache.get(key) != -1 && new Date(expiryCache.get(key)).before(new Date())) {
			cache.remove(key);
			expiryCache.remove(key);
		}
	}

	private void checkAll() {
		Iterator<String> iterator = expiryCache.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			checkValidate(key);
		}
	}
}
