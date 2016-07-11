package com.zk.turtle.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ZooKeeperFactory {

	static String HOSTS = null;

	private static final String CONFIG_FILE_NAME = "turtle_zkcfg.properties";

	// 根路径
	private static final String ROOT_PATH = "/turtle_config";

	static {
		loadConnectString();
	}

	private static final int DEFUALT_ZOOKEEPER_SESSION_TIMEOUT = 30000;

	private static Log logger = LogFactory.getLog(ZooKeeperFactory.class);

	public static ZooKeeper newZooKeeper(Watcher watcher) {
		return newZooKeeper(getConnectString(), watcher);
	}

	public static ZooKeeper newZooKeeper(String connectString, Watcher watcher) {
		try {
			ZooKeeper zookeeper = new ZooKeeper(connectString,
					DEFUALT_ZOOKEEPER_SESSION_TIMEOUT, watcher);
			return zookeeper;
		} catch (IOException e) {
			throw new RuntimeException(
					"Error occurs while creating ZooKeeper instance.", e);
		}
	}

	static void loadConnectString() {
		InputStream is = ZooKeeperFactory.class.getClassLoader()
				.getResourceAsStream(CONFIG_FILE_NAME);
		try {

			Properties prop = new Properties();
			prop.load(is);

			String hosts = (String) prop.get("zk_hosts");
			if (hosts == null) {
				throw new RuntimeException("Need conf for zookeeper hosts.");
			}
			HOSTS = hosts;
		} catch (IOException e) {
			logger.error("error get config string", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("error get config string", e);
				}
			}
		}
	}

	static String getConnectString() {
		return HOSTS + ROOT_PATH;
	}
}
