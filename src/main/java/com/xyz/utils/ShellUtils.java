package com.xyz.utils;

import com.xyz.bean.Server;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class ShellUtils {
    private static final String shellHead = "#!/bin/bash";
    private static final String bin = "bin/";
    private static final String update = "update.sh";
    private static final String backup = "backup.sh";
    private static final String run_update = "run-update.sh";
    private static String binDir = "";

    /**
     * @desc 生成更新脚本
     * @author cxs
     * @date 2020-05-10 04:17:26
     **/
    public static void buildUpdateShell(String workDir, String shellFile, Server server) {
        binDir = workDir + bin;
        try {
            String path = PathUtils.toPath(ResourceUtils.getFile("classpath:").getPath());
            FileUtils.copy(path + update, workDir);
            FileUtils.copy(path + backup, binDir);
            FileUtils.copy(path + run_update, binDir);
            buildShellByServer(server);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     * @param server
     * @return void
     * @desc 创建对应服务器shell
     * @author cxs
     * @date 2021-02-06 12:35:29
     **/
    public static void buildShellByServer(Server server) {
        Set<String> ips = server.getIps();
        if (ips == null || ips.isEmpty()) {
            return;
        }
        Iterator<String> iterator = ips.iterator();
        while (iterator.hasNext()) {
            String ip = iterator.next();
            String fileName = binDir + ip + ".sh";
            Path fpath = Paths.get(fileName);
            try {
                if (Files.exists(fpath)) {
                    Files.delete(fpath);
                }
                Files.createFile(fpath);

                StringBuffer shell = new StringBuffer();
                String[] projects = server.getProjectsByIp(ip);
                shell.append(shellHead);
                shell.append("	\n#########################################################  	");
                shell.append("	\n####################指定服务器IP运行脚本#################  	");
                shell.append("	\n########################################################   	");
                shell.append("	\n                                                           	");
                shell.append("	\n#当前服务器项目                                            	");
                shell.append("	\nfiles=").append(Arrays.stream(projects).collect(Collectors.joining(","))).append("                                      	");
                shell.append("	\n#项目部署路径                                              	");
                shell.append("	\nspath=").append(server.getProject().get(projects[0])).append("                                   	");
                shell.append("	\n                                                           	");
                shell.append("	\n#备份文件                                                  	");
                shell.append("	\necho \"`./bin/").append(backup).append(" ${files} ${spath}`\"                     	");
                shell.append("	\n#复制文件                                                  	");
                shell.append("	\necho \"`./bin/").append(run_update).append(" ${files} ${spath}`\"                 	");
                Files.write(fpath, shell.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
