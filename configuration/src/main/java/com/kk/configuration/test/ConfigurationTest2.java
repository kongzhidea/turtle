package com.kk.configuration.test;

import com.kk.configuration.model.ConfigGroup;
import com.kk.configuration.service.IConfiguration;
import com.kk.configuration.service.impl.Configuration;

public class ConfigurationTest2 {
    public static void main(String[] args) throws InterruptedException {
        IConfiguration configuration = new Configuration();

        configuration.set(ConfigGroup.WALLET, "prop2", "000");
    }
}
