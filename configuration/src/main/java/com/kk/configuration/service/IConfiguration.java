package com.kk.configuration.service;

import com.kk.configuration.model.ConfigGroup;

import java.util.List;

public interface IConfiguration {

    void init(ConfigGroup... groups);

    String get(ConfigGroup group, String prop);

    void set(ConfigGroup group, String prop, String value);

    void remove(ConfigGroup group, String prop);

    List<String> getChild(ConfigGroup group);
}
