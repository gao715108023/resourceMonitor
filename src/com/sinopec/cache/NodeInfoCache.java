package com.sinopec.cache;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NodeInfoCache {

	private static final Log LOG = LogFactory.getLog(NodeInfoCache.class);

	private Vector<String> allGroupIPList;

	ConcurrentHashMap<String, Long> expiryCache;

	private ScheduledExecutorService scheduleService;

	/**
	 * 清理超期信息的时间间隔，默认10分钟
	 */
	private int expiryInterval = 10;

	public NodeInfoCache() {
		super();
		init();
		// TODO Auto-generated constructor stub

	}

	public NodeInfoCache(int expiryInterval) {
		super();
		this.expiryInterval = expiryInterval;
		init();
	}

	private void init() {
		allGroupIPList = new Vector<String>();
		expiryCache = new ConcurrentHashMap<String, Long>();
		scheduleService = Executors.newScheduledThreadPool(1);
		scheduleService.scheduleAtFixedRate(new CheckOutOfDateSchedule(allGroupIPList, expiryCache), 0, expiryInterval * 60, TimeUnit.SECONDS);
		if (LOG.isInfoEnabled()) {
			LOG.info("The service of checking Group timeout has started!");
		}
	}

	class CheckOutOfDateSchedule implements Runnable {

		Vector<String> allGroupIPList;

		ConcurrentHashMap<String, Long> expiryCache;

		CheckOutOfDateSchedule(Vector<String> allGroupIPList, ConcurrentHashMap<String, Long> expiryCache) {
			this.allGroupIPList = allGroupIPList;
			this.expiryCache = expiryCache;
		}

		public void check() {
			for (String groupIP : allGroupIPList) {
				if (expiryCache.get(groupIP) == null)
					continue;
				long date = expiryCache.get(groupIP);
				if ((date > 0) && (new Date(date).before(new Date()))) {
					expiryCache.remove(groupIP);
					allGroupIPList.remove(groupIP);
				}
			}
		}

		@Override
		public void run() {
			// To change body of implemented methods use File | Settings | File
			// Templates.
			check();
		}
	}

	public void add(String key, Date expiry) {
		if (!allGroupIPList.contains(key)) {
			allGroupIPList.add(key);
		}
		expiryCache.put(key, expiry.getTime());
	}

	public String getNextGroupIP(String curGroupIP) {
		if (allGroupIPList.size() == 0)
			return "no group";
		if (allGroupIPList.size() == 1)
			return "only one group";
		return allGroupIPList.get((allGroupIPList.indexOf(curGroupIP) + 1) % allGroupIPList.size());
	}
}
