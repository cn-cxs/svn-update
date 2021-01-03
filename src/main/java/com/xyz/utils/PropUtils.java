package com.xyz.utils;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropUtils {
    private static final String defaultPropFile = "update.conf";

    public static Map<String, String> getAllProp() {
        try {
            String jarWholePath = PropUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            jarWholePath = java.net.URLDecoder.decode(jarWholePath, "UTF-8");
            String jarPath = new File(jarWholePath).getParentFile().getAbsolutePath();
            String propPath = jarPath + File.separator+ defaultPropFile;
            return getAllProp(propPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public static Map<String, String> getAllProp(String propPath) {
        Map<String, String> props = new HashMap<>();
        try {
            Path path = Paths.get(propPath);
            if (!Files.exists(path)) {
                System.err.println("文件不存在:" + propPath);
                return props;
            }
            List<String> list = Files.readAllLines(path);
            props = listToMap(list);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public static Map<String, String> listToMap(List<String> list) {
        Map<String, String> props = new HashMap<>();
        for (String str : list) {
            if (str == null || "".equals(str)) {
                continue;
            }
            str = str.trim();
            if (str.startsWith("#")){
                continue;
            }
            String[] kv = str.split("=");
            String key = kv[0] == null ? "" : kv[0].trim();
            String value = kv[1] == null ? "" : kv[1].trim();
            props.put(key, value);
        }
        return props;
    }

    public static String getProp(String key) {
        /*Map props = getAllProp();
        return (String) props.get(key);*/
        File file = new File("./update.conf");
        return  PropertiesUtils.getProperties(file.getPath(),key);
    }

    public static String getProp(String propPath, String key) {
        Map props = getAllProp(propPath);
        return (String) props.get(key);
    }
}
