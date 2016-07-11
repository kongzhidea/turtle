package com.zk.turtle.model;

public enum TurtleGroup {

	TURTLE("turtle"), UNIT_TEST("unit_test"), KK("kk");

	private String name;

	TurtleGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static TurtleGroup fromString(String groupName) {
		if (groupName == null || groupName.equals("")) {
			throw new IllegalArgumentException(" bad group name " + groupName);
		}
		for (TurtleGroup tg : TurtleGroup.values()) {
			if (tg.getName().equals(groupName)) {
				return tg;
			}
		}
		throw new IllegalArgumentException(" bad group name " + groupName);
	}
}
