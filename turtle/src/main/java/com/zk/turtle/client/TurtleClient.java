package com.zk.turtle.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zk.turtle.model.TurtleGroup;
import com.zk.turtle.service.ConfigureFactory;
import com.zk.turtle.service.ConfigureLoader;

public class TurtleClient {

	private static Log logger = LogFactory.getLog(TurtleClient.class);
	private static ConfigureLoader configureLoader = ConfigureFactory
			.getLoader();

	/**
	 * 取得当前的配置的值 如果该key对应的value不存在则返回null
	 * 
	 * @param group
	 *            Group name
	 * @param key
	 *            Key
	 * @return property 的value
	 */
	public static String getValue(final TurtleGroup group, final String key) {
		if (key == null || key.length() == 0) {
			return null;
		}
		return configureLoader.get(group, key);
	}

	/**
	 * 取得当前的配置的值 如果该key对应的value不存在则返回 defaultValue
	 * 
	 * @param group
	 *            Group name
	 * @param key
	 *            Key
	 * @param defaultValue
	 *            value 不存在的时候，默认的返回值
	 * @return property 的value
	 */

	public static String getValue(final TurtleGroup group, final String key,
			String defaultValue) {
		String val = getValue(group, key);
		if (val == null) {
			return null;
		}
		return defaultValue;
	}

	/**
	 * 尝试把value转换成int的形式,value不存在，返回null,转换失败会抛出异常
	 * 
	 * @param group
	 *            Group name
	 * @param key
	 *            Key
	 * @return property 的value
	 */
	public static Integer getValueAsInt(final TurtleGroup group,
			final String key) {
		String val = getValue(group, key);
		if (val == null) {
			return null;
		}
		return Integer.parseInt(val);
	}

	/**
	 * 尝试把value转换成int的形式，如果转换失败或者value不存在，返回defaultValue
	 * 
	 * @param group
	 *            Group name
	 * @param key
	 *            Key
	 * @param defaultValue
	 *            value 不存在的时候或者转换失败，默认的返回值
	 * @return Int类型的value
	 */
	public static Integer getValueAsInt(final TurtleGroup group,
			final String key, Integer defaultValue) {
		String val = getValue(group, key);
		if (val == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			logger.warn("error to parse property " + key + " value " + val, e);
		}
		return defaultValue;
	}

	/**
	 * 尝试把value转换成bool的形式，如果转换失败抛出异常，value不存在，返回null
	 * 
	 * @param group
	 *            Group name
	 * @param key
	 *            Key
	 * @return property 的value
	 */
	public static Boolean getValueAsBoolean(final TurtleGroup group,
			final String key) {
		String val = getValue(group, key);
		if (val == null) {
			return null;
		}
		return Boolean.parseBoolean(val);
	}

	/**
	 * 尝试把value转换成bool的形式，如果转换失败或者value不存在，返回 defaultValue
	 * 
	 * @param group
	 *            Group name
	 * @param key
	 *            Key
	 * @param defaultValue
	 *            value 不存在的时候，默认的返回值
	 * @return property 的value value 不存在的时候或者转换失败，默认的返回值
	 */
	public static Boolean getValueAsBoolean(final TurtleGroup group,
			final String key, Boolean defaultValue) {
		String val = getValue(group, key);
		if (val == null) {
			return defaultValue;
		}
		try {
			return Boolean.parseBoolean(val);
		} catch (Exception e) {
			logger.warn("error to parse property " + key + " value " + val, e);
		}
		return defaultValue;
	}

}
