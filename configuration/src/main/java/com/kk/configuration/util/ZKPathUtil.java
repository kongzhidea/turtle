package com.kk.configuration.util;

public class ZKPathUtil {
//    public static final String ROOT_DIRECTORY = "turtle";

    /**
     * 给定group、prop，计算出其对应的path，绝对路径
     */
    public static String getPath(String group, String prop) {
        return "/" + group + "/" + prop;
    }

}
