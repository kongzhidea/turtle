package com.kk.configuration.service.impl;

import com.kk.configuration.client.ZKClient;
import com.kk.configuration.model.ConfigGroup;
import com.kk.configuration.service.IConfiguration;
import com.kk.configuration.util.ZKOperator;
import com.kk.configuration.util.ZKPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 基于Zookeeper的配置系统
 */
public class Configuration implements IConfiguration {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private Map<String, String> propMap = new ConcurrentHashMap<String, String>();

    public Configuration() {
    }

    // 对某组初始化， 当节点发生变化时候会通知client
    public void init(ConfigGroup... groups) {
        try {
            for (ConfigGroup group : groups) {
                regWatcherOnLineRserver(group.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void regWatcherOnLineRserver(String group) throws Exception {
        final PathChildrenCache childrenCache = new PathChildrenCache(ZKClient.getClient(), "/" + group, false);
        childrenCache.start(PathChildrenCache.StartMode.NORMAL);
        childrenCache.getListenable().addListener(
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                            throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                logger.debug("CHILD_ADDED: " + event.getData().getPath());
                                break;
                            case CHILD_REMOVED:
                                logger.debug("CHILD_REMOVE: " + event.getData().getPath());
                                synchronized (propMap) {
                                    propMap.remove(event.getData().getPath());
                                }
                                break;
                            case CHILD_UPDATED:
                                logger.debug("CHILD_UPDATED: " + event.getData().getPath());
                                synchronized (propMap) {
                                    propMap.remove(event.getData().getPath());
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
        );
    }

    /**
     * 不存在则返回null
     */
    @Override
    public String get(ConfigGroup group, String prop) {
        if (group == null) {
            throw new IllegalArgumentException("the group be null ");
        }
        String key = ZKPathUtil.getPath(group.getName(), prop);
        if (!this.propMap.containsKey(key)) {
            synchronized (key.intern()) {
                if (!this.propMap.containsKey(key)) {
                    String value = null;
                    try {
                        value = ZKOperator.getValueAsString(group.getName(), prop);
                    } catch (Exception e) {
                        logger.error("获取zookeeper中prop失败", e);
                        return null;
                    }
                    if (value != null) {
                        this.propMap.put(key, value);
                    }

                    return value;
                }
            }
        }
        return propMap.get(key);
    }

    /**
     * 设置zookeeper中的属性值
     */
    @Override
    public void set(ConfigGroup group, String prop, String value) {
        if (group == null) {
            throw new IllegalArgumentException("the group be null ");
        }
        try {
            ZKOperator.set(group.getName(), prop, value);
        } catch (Exception e) {
            logger.error("设置zookeeper中prop失败", e);
        }
    }

    /**
     * 删除zookeeper节点
     */
    @Override
    public void remove(ConfigGroup group, String prop) {
        if (group == null) {
            throw new IllegalArgumentException("the group be null ");
        }
        try {
            ZKOperator.remove(group.getName(), prop);
        } catch (Exception e) {
            logger.error("删除zookeeper中prop失败", e);
        }
    }

    @Override
    public List<String> getChild(ConfigGroup group) {
        try {
            return ZKOperator.getChildren("/" + group.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }
}
