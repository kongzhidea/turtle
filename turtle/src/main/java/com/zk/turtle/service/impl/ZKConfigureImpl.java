package com.zk.turtle.service.impl;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zk.turtle.model.Configure;
import com.zk.turtle.model.TurtleGroup;
import com.zk.turtle.service.ConfigureManager;
import com.zk.turtle.service.TurtleException;

/**
 * zookeeper配置项，读取数据到内存，修改zk时候通知，reload
 * 
 * @author zhihui.kong@renren-inc.com
 * 
 */
public class ZKConfigureImpl implements ConfigureManager, Watcher {
	private static Log logger = LogFactory.getLog(ZKConfigureImpl.class);
	private static Charset ZK_VALUE_CHARSET = Charset.forName("UTF-8");
	private static long REFRESH_INTEVAL = 10000;
	private AtomicReference<Table<String, String, String>> configureCache;
	private ZooKeeper zookeeper = null;
	private Set<String> loadedGroups;

	private ZKConfigureImpl() {
		this.loadedGroups = new HashSet<String>();
		configureCache = new AtomicReference<Table<String, String, String>>();
		Table<String, String, String> configureMap = HashBasedTable.create();
		configureCache.set(configureMap);
		zookeeper = ZooKeeperFactory.newZooKeeper(this);
		startRefreshThread();
	}

	private void startRefreshThread() {
		Thread refreshThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(REFRESH_INTEVAL);
						loadConfig();
					} catch (Exception e) {
						logger.warn("Exception on refresh threads ", e);
					}
					if (logger.isDebugEnabled()) {
						logger.debug("refresh turtle config ");
					}
				}
			}
		});
		refreshThread.setDaemon(true);
		refreshThread.start();
		if (logger.isInfoEnabled()) {
			logger.info("refresh thread started");
		}
	}

	private static class LazyHolder {
		private static final ZKConfigureImpl INSTANCE = new ZKConfigureImpl();
	}

	public static ZKConfigureImpl getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * 不存在则返回null
	 */
	@Override
	public String get(TurtleGroup group, String prop) {
		if (group == null) {
			throw new IllegalArgumentException("the group be null ");
		}
		if (!this.loadedGroups.contains(group.getName())) {
			synchronized (this.loadedGroups) {
				if (!this.loadedGroups.contains(group.getName())) {
					this.loadedGroups.add(group.getName());
					this.loadConfig();
				}
			}
		}
		return configureCache.get().get(group.getName(), prop);
	}

	@Override
	public void process(WatchedEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("event " + event.getType() + "happen on path "
					+ event.getPath());
		}
		if (event.getType() == Event.EventType.None) {
			switch (event.getState()) {
			case SyncConnected:
				if (logger.isInfoEnabled()) {
					logger.info("Zookeeper SyncConnected: " + event);
				}
				break;
			case Expired:
				logger.warn("Zookeeper session expired: " + event);
				loadConfig();
				break;
			}

		} else if (event.getType() == Event.EventType.NodeChildrenChanged) {
			loadConfig();
			logger.info("child node changed. current active node : ");
		} else if (event.getType() == Event.EventType.NodeDeleted) {
			loadConfig();
			logger.info("child node deleted. current active node : ");
		} else if (event.getType() == Event.EventType.NodeDataChanged) {
			loadConfig();
			logger.info("child node deleted. current active node : ");
		} else if (event.getType() == Event.EventType.NodeCreated) {
			loadConfig();
			logger.info("child node deleted. current active node : ");
		} else {
			logger.warn("Unhandled event:" + event);
		}
	}

	private void loadConfig() {
		Table<String, String, String> configureMap = HashBasedTable
				.create(configureCache.get());
		try {
			if (zookeeper == null) {
				zookeeper = ZooKeeperFactory.newZooKeeper(this);
			}
			for (String group : this.loadedGroups) {
				try {
					loadConfigForGroup(configureMap, group);
				} catch (KeeperException.SessionExpiredException e) {
					logger.error(
							"zookeeper session expired , try to re conn  ", e);
					try {
						zookeeper.close();
					} catch (Exception e1) {
						logger.error(" error close zk session ", e);
					}
					zookeeper = ZooKeeperFactory.newZooKeeper(this);
				} catch (Exception e) {
					logger.error("zookeeper connect error. for group ", e);
				}
			}
			// 如果load 过程中发生异常不要修改缓存中的设置
			configureCache.set(configureMap);
		} catch (Exception e) {
			logger.error("zookeeper connect error.", e);
		}
	}

	private void loadConfigForGroup(Table<String, String, String> configureMap,
			String group) throws KeeperException, InterruptedException {
		Map<String, String> rowValue = new HashMap<String, String>();
		Stat stat = zookeeper.exists("/" + group, true);
		if (stat == null) {
			logger.warn("no node for " + group + " exists ");
		} else {
			List<String> props = zookeeper.getChildren("/" + group, this);
			for (String prop : props) {
				byte[] data = zookeeper.getData(buildPath(group, prop), true,
						null);
				if (logger.isDebugEnabled()) {
					logger.debug("get prop group  " + group + ", key " + prop
							+ " , value: " + new String(data));
				}
				if (data != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("the charset is " + ZK_VALUE_CHARSET);
					}
					rowValue.put(prop, new String(data, ZK_VALUE_CHARSET));
				} else {
					logger.warn("get null values for group:" + group + "|prop:"
							+ prop);
					rowValue.put(prop, null);
				}
			}
		}

		// 正常返回的情况下就清掉这个这个group的所有属性，异常的话，就保留内存里面的拷贝
		Set<String> propKeyss = new HashSet<String>();
		propKeyss.addAll(configureMap.row(group).keySet());

		for (String prop : propKeyss) {
			configureMap.remove(group, prop);
		}
		for (String prop : rowValue.keySet()) {
			configureMap.put(group, prop, rowValue.get(prop));
		}
	}

	private String buildPath(String group, String prop) {
		return "/" + group + "/" + prop;
	}

	@Override
	public void set(TurtleGroup turtleGroup, String prop, String value) {
		if (turtleGroup == null) {
			throw new IllegalArgumentException("the group be null ");
		}
		String group = turtleGroup.getName();

		if (zookeeper == null) {
			zookeeper = ZooKeeperFactory.newZooKeeper(this);
		}
		try {
			createGroupIfNeeded(group);
			String path = buildPath(group, prop);
			Stat pathStat = zookeeper.exists(path, true);
			if (pathStat == null) {
				zookeeper.create(path, value.getBytes(ZK_VALUE_CHARSET),
						ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} else {
				// 强制覆盖zk上的属性值 -1忽略版本
				zookeeper.setData(path, value.getBytes(ZK_VALUE_CHARSET), -1);
			}
			Table<String, String, String> configureMap = HashBasedTable
					.create(this.configureCache.get());
			configureMap.put(group, prop, value);
			this.configureCache.set(configureMap);

		} catch (KeeperException e) {
			logger.error("error set prop to zk  group:" + group + "|prop:"
					+ prop + "|value:" + value, e);
			throw new TurtleException("error set prop to zk  group:" + group
					+ "|prop:" + prop + "|value:" + value, e);

		} catch (InterruptedException e) {
			logger.error("error set prop to zk  group:" + group + "|prop:"
					+ prop + "|value:" + value, e);
			throw new TurtleException("error set prop to zk  group:" + group
					+ "|prop:" + prop + "|value:" + value, e);
		}
	}

	@Override
	public void remove(TurtleGroup turtleGroup, String prop) {

		if (turtleGroup == null) {
			throw new IllegalArgumentException("the group can not be null ");
		}
		String group = turtleGroup.getName();
		try {
			String path = buildPath(group, prop);
			Stat pathStat = zookeeper.exists(path, true);
			if (pathStat != null) {
				zookeeper.delete(path, -1);
			}
			tryDeleteEmptyGroup(group);
			Table<String, String, String> configureMap = HashBasedTable
					.create(this.configureCache.get());
			configureMap.remove(group, prop);
			this.configureCache.set(configureMap);

		} catch (KeeperException e) {
			logger.error("error set prop to zk  group:" + group + "|prop:"
					+ prop, e);
			throw new TurtleException("error set prop to zk  group:" + group
					+ "|prop:" + prop, e);

		} catch (InterruptedException e) {
			logger.error("error set prop to zk  group:" + group + "|prop:"
					+ prop, e);
			throw new TurtleException("error set prop to zk  group:" + group
					+ "|prop:" + prop, e);
		}
	}

	@Override
	public Table<TurtleGroup, String, Configure> getAll() {
		Table<TurtleGroup, String, Configure> configures = HashBasedTable
				.create();
		Table<String, String, String> table = this.configureCache.get();
		for (String group : table.rowKeySet()) {
			for (String prop : table.row(group).keySet()) {
				Configure configure = new Configure();
				configure.setGroup(group);
				configure.setProp(prop);
				configure.setValue(table.get(group, prop));
				try {
					TurtleGroup t = TurtleGroup.fromString(group);
					configures.put(t, prop, configure);
				} catch (IllegalArgumentException e) {
					logger.warn("there are invalid group in Zk ", e);
				}
			}
		}
		return configures;
	}

	private void tryDeleteEmptyGroup(String group) throws KeeperException,
			InterruptedException {
		final String groupPath = "/" + group;
		boolean groupExists = groupExists(group);
		if (groupExists) {
			List<String> children = zookeeper.getChildren(groupPath, true);
			if (children == null || children.size() == 0) {
				zookeeper.delete(groupPath, -1);
			}
		}
	}

	private boolean groupExists(String group) throws KeeperException,
			InterruptedException {
		final String groupPath = "/" + group;
		Stat stat = zookeeper.exists(groupPath, true);
		return stat != null;
	}

	private void createGroupIfNeeded(String group) throws KeeperException,
			InterruptedException {
		Stat stat = zookeeper.exists("/" + group, true);
		if (stat == null) {
			zookeeper.create("/" + group, group.getBytes(ZK_VALUE_CHARSET),
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}
}
