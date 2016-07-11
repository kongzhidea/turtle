package com.zk.turtle.model;

import java.util.UUID;

/**
 * Created by terry on 14-7-14.
 */
public class Configure {

	private String group;
	private String prop;
	private String value;

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "Configure [group=" + group + ", prop=" + prop + ", value="
				+ value + "]";
	}


}
