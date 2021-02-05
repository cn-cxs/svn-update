package com.xyz.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static List<File> getAll(String path, boolean addDirectory, boolean addFile) throws IOException {
        List<File> files = new ArrayList<>();
        Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                //System.err.println("正在访问：" + dir + "目录");
                /*if(dir.getFileName().toString().equals(".svn")){
                    return FileVisitResult.SKIP_SUBTREE;
                }*/
                if (addDirectory) {

                    files.add(dir.toFile());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //System.err.println("\t正在访问" + file + "文件");
                if (addFile) {
                    files.add(file.toFile());
                }
                return super.visitFile(file, attrs);
            }

        });
        return files;
    }

    public static List<File> getDirectory(String path) throws IOException {
        return getAll(path, true, false);
    }

    public static List<File> getFile(String path) throws IOException {
        return getAll(path, false, true);
    }

    public static List<File> getAll(String path) throws IOException {
        return getAll(path, true, true);
    }

    public static void main(String[] args) throws IOException {
        List<File> list = getDirectory("D:\\temp");
        //List<File> list = getFile("D:\\temp");
        System.out.println(list.size());
        for (File file : list) {
           /* System.out.println(file.getAbsoluteFile());
            System.out.println(file.length());*/
           /* if(file.length()==0){
                System.out.println(file.getAbsoluteFile());
                System.out.println(file.canExecute());
            }*/
            //System.out.println(file.getAbsoluteFile());
        }
    }

    public static void copy(String file, String dir) {

        try {
            Path path = Paths.get(dir);
            if (!Files.exists(path)) {
                path.toFile().mkdirs();
            }
            String fname = Paths.get(file).toFile().getName();
            Files.copy(Paths.get(file), Paths.get(path.toString() + "/" + fname), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
