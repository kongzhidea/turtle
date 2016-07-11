package com.kk.configuration.test;

import com.kk.configuration.model.ConfigGroup;
import com.kk.configuration.service.IConfiguration;
import com.kk.configuration.service.impl.Configuration;

public class SampleConfiguration {
    private static IConfiguration configuration = null;


    static {
        // 区分测试环境和 线上环境
        System.setProperty("com.kk.configuration.environment", "1");


        configuration = new Configuration();
        configuration.init(ConfigGroup.WALLET);
    }

    public static IConfiguration getConfiguration() {
        return configuration;
    }

    public static String get(String key) {
        return get(ConfigGroup.WALLET, key);
    }

    public static String get(ConfigGroup group, String key) {
        return configuration.get(group, key);
    }
}
