package com.kk.configuration.model;

public enum ConfigGroup {

    WALLET("wallet");

    private String name;

    ConfigGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ConfigGroup fromString(String groupName) {
        if (groupName == null || groupName.equals("")) {
            throw new IllegalArgumentException(" bad group name " + groupName);
        }
        for (ConfigGroup tg : ConfigGroup.values()) {
            if (tg.getName().equals(groupName)) {
                return tg;
            }
        }
        throw new IllegalArgumentException(" bad group name " + groupName);
    }
}
