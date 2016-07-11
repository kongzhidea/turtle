package com.zk.turtle.service;

import com.google.common.collect.Table;
import com.zk.turtle.model.Configure;
import com.zk.turtle.model.TurtleGroup;

import java.util.List;

/**
 */
public interface ConfigureManager extends ConfigureLoader {

	public void set(TurtleGroup group, String prop, String value);

	public void remove(TurtleGroup group, String prop);

	public Table<TurtleGroup, String, Configure> getAll();

}
