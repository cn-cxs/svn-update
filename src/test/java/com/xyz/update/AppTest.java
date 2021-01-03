package com.xyz.update;

import com.xyz.App;
import com.xyz.utils.FileUtils6;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AppTest {
    public static void main(String[] args) throws IOException {
        System.out.println("AppTest....");
        // 获取URL
        URL _url = App.class.getClassLoader().getResource("update");
        // 通过url获取File的绝对路径
        File f = new File(_url.getFile());
        System.out.println(f.getAbsolutePath());
        FileUtils6.copyDir(f.getAbsolutePath(),"D:\\temp\\");
    }
}
