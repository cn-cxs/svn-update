package com.xyz.utils;

import java.nio.file.Paths;

public class PathUtils {
    public static void main(String[] args) {
        String temp = "D:\\temp\\\\\\";

        System.out.println(toPath(temp));
    }

    /**
     * @param path
     * @return java.lang.String
     * @desc 路径转化成URI路径
     * @author cxs
     * @date 2021-01-03 05:43:15
     **/
    public static String toUriPath(String path) {
        return (Paths.get(path).toUri().getPath());
    }

    public static String toPath(String path) {
        return (toUriPath(path)).substring(1);
    }

    /**
     * @param parentPath 父路径
     * @param allPath    全路径
     * @return java.lang.String
     * @desc 获取相对路径
     * @author cxs
     * @date 2021-01-03 05:43:35
     **/
    public static String getRelativePath(String parentPath, String allPath) {
        return toUriPath(allPath).replaceAll(toUriPath(parentPath), "");
    }
}
