package com.xyz.utils;


import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutPutFileUtils {
    private static String workSpaces = null;

    static class Config {
        final static String _src = "/src";
        final static String _java = ".java";
        final static String _class = ".class";
        final static String WebRoot = "/WebRoot";
        final static String WEB_INF = "/WEB-INF";
        final static String classes = "/classes";
        final static String PROJECT_DIR = "$PROJECT_DIR$";
    }

    /**
     * @param logEntries svn提交记录对象
     * @param workSpaces 本地项目工作空间,如:D:/Work/WEBWorkSpaces/
     * @return java.util.List<java.lang.String>
     * @desc 获取svn目录上对应本地要发版的文件目录
     * @author cxs
     * @date 2020-05-09 03:19:33
     **/
    public static List<String> getLocalFiles(Collection logEntries, String workSpaces) {
        OutPutFileUtils.workSpaces = PathUtils.toPath(workSpaces);
        return getLocalFiles(logEntries);
    }

    /**
     * @param logEntries svn提交记录对象
     * @return java.util.List<java.lang.String>
     * @desc 获取svn目录上对应本地要发版的文件目录
     * @author cxs
     * @date 2020-05-09 03:21:18
     **/
    public static List<String> getLocalFiles(Collection logEntries) {
        List<String> localFiles = new ArrayList<>();
        File configFile = WorkSpaceUtils.findConfigFile(workSpaces);
        if (configFile == null || !configFile.exists()) {
            return localFiles;
        }
        String outPath = XmlUtil.findOutputPath(configFile.getAbsolutePath());
        String webRoot = null;
        if (outPath.startsWith(Config.PROJECT_DIR)) {
            webRoot = outPath.replace(Config.PROJECT_DIR, workSpaces);
        } else if (outPath.startsWith(Config.WebRoot)) {
            webRoot = workSpaces + Config.WebRoot;
        } else {
            System.err.println("未知的输出路径:" + outPath);
            return localFiles;
        }
        webRoot = PathUtils.toPath(webRoot);
        for (Object temp : logEntries) {
            SVNLogEntry logEntry = (SVNLogEntry) temp;
            Map<String, SVNLogEntryPath> cPaths = logEntry.getChangedPaths();
            Collection<SVNLogEntryPath> values = cPaths.values();
            for (SVNLogEntryPath svnLogEntryPath : values) {
                String svnPath = svnLogEntryPath.getPath();

                //.java替换成.class
                if (svnPath.endsWith(Config._java)) {
                    svnPath = svnPath.replaceAll(Config._java, Config._class);
                }
                //src及之前的路径替换为WEB-INF/classes/
                if (svnPath.contains(Config._src)) {
                    String target = Config._src;
                    int index = svnPath.lastIndexOf(target);
                    svnPath = webRoot + Config.WEB_INF + Config.classes + (svnPath.substring(index + target.length()));
                }
                //路径/WebRoot直接替换
                if (svnPath.contains(Config.WebRoot)) {
                    String target = Config.WebRoot;
                    int index = svnPath.lastIndexOf(target);
                    svnPath = webRoot + (svnPath.substring(index + target.length()));
                }
                //如果是class文件,则找带$的class文件
                if (svnPath.endsWith(Config._class)) {
                    Path path = Paths.get(svnPath);
                    Path parent = path.getParent();
                    String fileName = path.getFileName().toString().replaceAll(Config._class, "");
                    String regex = fileName + "(\\$\\S*)?\\" + Config._class;
                    final Pattern compile = Pattern.compile(regex);
                    File[] files = parent.toFile().listFiles((dir, name) -> {
                        if (compile.matcher(name).find()) {
                            return true;
                        }
                        return false;
                    });
                    if (files == null || files.length == 0) {
                        continue;
                    }
                    for (File file : files) {
                        localFiles.add(PathUtils.toPath(file.getPath()));
                    }
                } else {
                    localFiles.add(PathUtils.toPath(svnPath));
                }

            }
        }
        return localFiles;
    }
}
