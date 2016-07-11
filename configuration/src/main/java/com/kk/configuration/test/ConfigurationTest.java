package com.kk.configuration.test;

import com.kk.configuration.model.ConfigGroup;
import com.kk.configuration.service.IConfiguration;
import com.kk.configuration.service.impl.Configuration;

public class ConfigurationTest {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("com.kk.configuration.environment", "1");

        IConfiguration configuration = new Configuration();
        configuration.init(ConfigGroup.WALLET);

        System.out.println(configuration.getChild(ConfigGroup.WALLET));

        String key1 = "prop1";
        String key2 = "prop2";

        while (true) {
            System.out.println("v1=" + configuration.get(ConfigGroup.WALLET, key1));
            System.out.println("v2=" + configuration.get(ConfigGroup.WALLET, key2));

            Thread.sleep(3000);
        }
    }
}
