package com.kk.configuration.util;

import com.kk.configuration.client.ZKClient;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

public class ZKOperator {

    /**
     * 取得当前的配置的值 如果该key对应的value不存在则返回null
     *
     * @param group
     * @param prop
     * @return
     * @throws Exception
     */
    public static byte[] getValue(final String group, final String prop) throws Exception {
        if (prop == null || prop.length() == 0) {
            return null;
        }
        if (exist(group, prop)) {
            return ZKClient.getClient().getData().forPath(ZKPathUtil.getPath(group, prop));
        } else {
            return null;
        }
    }

    /**
     * 尝试把value转换成String的形式,value不存在，返回null,转换失败会抛出异常
     *
     * @param group
     * @param prop
     * @return
     * @throws Exception
     */
    public static String getValueAsString(final String group,
                                          final String prop) throws Exception {
        byte[] val = getValue(group, prop);
        if (val == null) {
            return null;
        }
        return new String(val, "utf-8");
    }

    /**
     * 设置节点数据
     *
     * @param group
     * @param prop
     * @param value
     * @throws Exception
     */
    public static void set(final String group,
                           final String prop, final String value) throws Exception {
        if (exist(group, prop)) {
            ZKClient.getClient().setData().forPath(ZKPathUtil.getPath(group, prop), value.getBytes("utf-8"));
        } else {
            ZKClient.getClient().create().forPath(ZKPathUtil.getPath(group, prop), value.getBytes("utf-8"));
        }
    }

    /**
     * 删除节点
     *
     * @param group
     * @param prop
     * @throws Exception
     */
    public static void remove(final String group,
                              final String prop) throws Exception {
        if (exist(group, prop)) {
            ZKClient.getClient().delete().forPath(ZKPathUtil.getPath(group, prop));
        }
    }

    /**
     * 查询节点是否存在
     *
     * @param group
     * @param prop
     * @return
     * @throws Exception
     */
    public static boolean exist(final String group,
                                final String prop) throws Exception {
        if (prop == null || prop.length() == 0) {
            return false;
        }
        Stat stat = ZKClient.getClient().checkExists().forPath(ZKPathUtil.getPath(group, prop));
        return stat != null;
    }

    /**
     * 获取子节点
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static List<String> getChildren(final String path) throws Exception {
        Stat stat = ZKClient.getClient().checkExists().forPath(path);
        if (stat != null) {
            return ZKClient.getClient().getChildren().forPath(path);
        } else {
            return new ArrayList<String>();
        }
    }
}
