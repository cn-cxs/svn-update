package com.xyz.common;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Global {
    //public static final Pattern project = Pattern.compile("(\\w)+/((src)|(WebRoot)){1}/.*$");//匹配项目地址的正则
    public static final Pattern project = Pattern.compile("(/src/)|(/WebRoot/)");//匹配项目地址的正则
    public static final Pattern project_src = Pattern.compile("/src/");//匹配项目地址的正则
    public static final Pattern project_wr = Pattern.compile("/WebRoot/");//匹配项目地址的正则
    public static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
}
