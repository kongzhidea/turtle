package com.zk.turtle.service;

import com.zk.turtle.service.impl.ZKConfigureImpl;

public class ConfigureFactory {

	/**
	 * 封装get操作
	 * 
	 * @return
	 */
	public static ConfigureLoader getLoader() {
		return ZKConfigureImpl.getInstance();
	}

	/**
	 * 封装manager操作
	 * 
	 * @return
	 */
	public static ConfigureManager getManager() {
		return ZKConfigureImpl.getInstance();
	}

}
