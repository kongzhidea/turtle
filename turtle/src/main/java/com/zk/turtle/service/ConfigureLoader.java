package com.zk.turtle.service;

import com.zk.turtle.model.TurtleGroup;

/**
 * client  get操作
 * 
 */
public interface ConfigureLoader {

	public String get(TurtleGroup group, String prop);

}
