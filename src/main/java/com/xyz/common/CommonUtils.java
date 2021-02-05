package com.xyz.common;

import java.util.regex.Matcher;

public class CommonUtils {
    public static String getProjectPath(String path) {
        Matcher matcher = Global.project.matcher(path);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
