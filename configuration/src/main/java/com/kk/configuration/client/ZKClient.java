package com.kk.configuration.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

import java.io.InputStream;
import java.util.Properties;

public class ZKClient {
    public static Log logger = LogFactory.getLog(ZKClient.class);
    private static CuratorFramework client;

    public static CuratorFramework getClient() {
        if (client == null) {
            synchronized (ZKClient.class) {
                if (client == null) {
                    try {
                        InputStream in = ZKClient.class.getResourceAsStream("zk.properties");
                        Properties prop = new Properties();
                        prop.load(in);

                        String hosts = null;

                        String environment = System.getProperty("com.kk.configuration.environment");
                        if (environment != null && "1".equals(environment)) {
                            hosts = (String) prop.get("zk_hosts_test");
                            logger.info("configuration.environment is test now");
                        } else {
                            hosts = (String) prop.get("zk_hosts");
                            logger.info("configuration.environment is online now");
                        }

                        if (hosts == null) {
                            throw new RuntimeException("Need conf for zookeeper hosts.");
                        }
                        // 命名空间 zkClient下所有的data都在该地址下 zk中使用 '/' 可以指定目录结构
                        String namespace = (String) prop.get("zk_root");


//                        logger.info("zkHosts: " + hosts);
                        client = CuratorFrameworkFactory
                                .builder()
                                .connectString(hosts)
                                .namespace(namespace)
                                .retryPolicy(
                                        new RetryNTimes(Integer.MAX_VALUE, 5 * 60 * 1000))
                                .connectionTimeoutMs(5000).build();
                    } catch (Exception e) {
                        logger.error("get client error", e);
                    }
                    client.start();
                }
            }
        }
        return client;
    }

    public static void destroy() throws Exception {
        client.close();
    }
}
