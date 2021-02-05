package com.xyz.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: TestProperties
 * @Description: 获取配置文件信息
 * @date: 2017年11月25日 上午10:56:00
 * @version: 1.0.0
 */
public class PropertiesUtils {

    final static Properties prop = new Properties();

    /**
     * 根据key读取value
     *
     * @param filePath
     * @param keyWord
     * @return String
     * @throws
     * @Title: getProperties_2
     * @Description: 第二种方式：使用缓冲输入流读取配置文件，然后将其加载，再按需操作
     * 绝对路径或相对路径， 如果是相对路径，则从当前项目下的目录开始计算，
     * 如：当前项目路径/config/config.properties,
     * 相对路径就是config/config.properties
     */
    public static String getProperties(String filePath, String keyWord) {
        String value = null;
        InputStreamReader is = null;
        BufferedReader bs = null;
        try {
            // 通过输入缓冲流进行读取配置文件
            is = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
            //bs = new BufferedReader(new InputStreamReader(is));
            // 加载输入流
            prop.load(is);
            // 根据关键字获取value值
            value = prop.getProperty(keyWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (is != null) {
                    is.close();
                }
                if (bs != null) {
                    bs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return value;
    }

    /**
     * 读取配置文件所有信息
     *
     * @param filePath
     * @return void
     * @throws
     * @Title: getProperties_2
     * @Description: 第二种方式：使用缓冲输入流读取配置文件，然后将其加载，再按需操作
     * 绝对路径或相对路径， 如果是相对路径，则从当前项目下的目录开始计算，
     * 如：当前项目路径/config/config.properties,
     * 相对路径就是config/config.properties
     */
    public static Map<String, String> getProperties(String filePath) {
        Map<String, String> properties = new HashMap<>();
        try {
            // 通过输入缓冲流进行读取配置文件
            InputStream InputStream = new BufferedInputStream(new FileInputStream(new File(filePath)));
            // 加载输入流
            prop.load(InputStream);
            @SuppressWarnings("rawtypes")
            Enumeration en = prop.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String value = prop.getProperty(key);
                properties.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }


    public static void main(String[] args) {
       /* System.out.println("*********************************************");
        // 注意路径问题
        String properties_2 = getProperties("update.conf", "url");
        System.out.println("url = " + properties_2);
        Map<String, String> map = getProperties("update.conf");
        System.out.println(map);
        System.out.println("*********************************************");*/
    }
}