package com.xyz.utils;

import java.io.*;
import java.util.List;

public class FileUtils6 {

    public static void close(Closeable... closeable) {
        for (int i = 0; i < closeable.length; i++) {
            if (closeable[i] != null) {
                try {
                    closeable[i].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copy(String sourceFile, String destDir) {
        copy(sourceFile, destDir, null);
    }

    public static void copy(String sourceFile, String destDir, String fileName) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File ifile = new File(sourceFile);
            in = new FileInputStream(ifile);
            fileName = fileName == null ? ifile.getName() : fileName;
            out = new FileOutputStream(new File(destDir + File.separator + fileName));
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(in, out);
        }
    }

    public static void readFile(String path, List<File> list) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        //System.out.println("文件夹:" + file2.getAbsolutePath());
                        readFile(file2.getAbsolutePath(), list);
                    } else {
                        //System.out.println("文件:" + file2.getAbsolutePath());
                        list.add(file2);
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

    public static void copyDir(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        //文件名称列表
        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(oldPath + file.separator + filePath[i])).isDirectory()) {
                copyDir(oldPath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }

            if (new File(oldPath + file.separator + filePath[i]).isFile()) {
                copyFile(oldPath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }

        }
    }

    /**
     * @param oldPath
     * @param newPath
     * @return void
     * @desc 复制文件及文件夹
     * @author cxs
     * @date 2020-05-09 09:24:16
     **/
    public static void copyFile(String oldPath, String newPath) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            File oldFile = new File(oldPath);
            File file = new File(newPath);
            in = new FileInputStream(oldFile);
            out = new FileOutputStream(file);
            byte[] buffer = new byte[2097152];
            while ((in.read(buffer)) != -1) {
                out.write(buffer);
            }
        } finally {
            close(in, out);
        }
    }

    /**
     * 读入TXT文件
     */
    public static String readFile(String pathname) {
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        FileReader reader = null;
        BufferedReader br = null;
        StringBuffer text = new StringBuffer();
        try {
            reader = new FileReader(pathname);
            br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(reader, br);
        }
        return text.toString();
    }

    /**
     * 写入TXT文件
     */
    public static void writeFile(File writeName, String text) {
        FileWriter writer = null;
        BufferedWriter out = null;
        try {
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            writer = new FileWriter(writeName);
            out = new BufferedWriter(writer);
            out.write(text); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer, out);
        }
    }
    /**
     * @desc 追加文件
     * @param writeName
     * @param text
     * @return void
     * @author cxs
     * @date 2020-05-09 09:40:25
     **/
    public static void appendFile(File writeName, String text) {
        FileWriter writer = null;
        BufferedWriter out = null;
        try {
            if(!writeName.exists()){
                writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            }
            writer = new FileWriter(writeName);
            out = new BufferedWriter(writer);
            out.append(text); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer, out);
        }
    }

}
