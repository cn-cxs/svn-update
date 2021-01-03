package com.xyz.utils;

public class ShellUtils {
    private static final String shellHead = "#!/bin/bash";
    /**
     * @desc 生成更新脚本
     * @param serverPath
     * @return java.lang.String
     * @author cxs
     * @date 2020-05-10 04:17:26
     **/
    public static String buildUpdateShell(String serverPath) {
        StringBuffer shell = new StringBuffer();
        shell.append(shellHead);
        shell.append("\necho \"------------------------------------update start----------------------------------------\"                             ");
        shell.append("\n#获取当前时间                                                                                                                 ");
        shell.append("\nnow=$(date +%Y%m%d-%H%M%S)                                                                                                         ");
        shell.append("\n#回滚脚本文件名                                                                                                               ");
        shell.append("\nrollback=rollback.sh                                                                                                          ");
        shell.append("\n#服务器项目路径                                                                                                               ");
        shell.append("\nserver_path=").append(serverPath);
        shell.append("\n#清空回滚脚本                                                                                                                 ");
        shell.append("\n`echo \"#! /bin/bash\">rollback.sh`                                                                                           ");
        shell.append("\n`echo \"echo \\\"------------------------------------rollback start--------------------------------------\\\"\">>rollback.sh`   ");
        shell.append("\n#遍历要更新的文件夹                                                                                                           ");
        shell.append("\nfor dir in  `ls -F | grep \"/$\"`                                                                                             ");
        shell.append("\n	do                                                                                                                          ");
        shell.append("\n		for file in  `find $dir` #遍历要更新的文件夹                                                                            ");
        shell.append("\n			do                                                                                                                  ");
        shell.append("\n				#组装成服务器的文件路径                                                                                         ");
        shell.append("\n				spath=${server_path}${file:0}                                                                                   ");
        shell.append("\n				#echo $spath                                                                                                    ");
        shell.append("\n				if [ -d \"$file\" ]; #如果遍历处要更新的是文件夹                                                                ");
        shell.append("\n					then                                                                                                        ");
        shell.append("\n						if [ ! -d \"$spath\" ]; #文件夹不存在则创建                                                             ");
        shell.append("\n						then                                                                                                    ");
        shell.append("\n						`mkdir -p ${spath}`                                                                                     ");
        shell.append("\n						fi                                                                                                      ");
        shell.append("\n				elif [ -f \"${file}\" ];	#如果是文件                                                                         ");
        shell.append("\n					then                                                                                                        ");
        shell.append("\n						if [ -f \"$spath\" ]; #如果文件存在,则需要备份                                                          ");
        shell.append("\n						then                                                                                                    ");
        shell.append("\n							echo \"cp -b ${spath} ${spath}.${now}\"                                                             ");
        shell.append("\n							`cp -b  ${spath} ${spath}.${now}` #备份要被更新的文件                                               ");
        shell.append("\n							`echo \"mv ${spath}.${now} ${spath}\" >>$rollback`	#更新动作生成回滚脚本                           ");
        shell.append("\n						fi                                                                                                      ");
        shell.append("\n						echo \"cp ${file} ${spath}\"                                                                            ");
        shell.append("\n						`cp ${file} ${spath}` #复制文件到目标位置                                                               ");
        shell.append("\n				fi                                                                                                              ");
        shell.append("\n			done                                                                                                                ");
        shell.append("\n	done                                                                                                                        ");
        shell.append("\n`echo \"echo \\\"------------------------------------rollback end----------------------------------------\\\"\">>rollback.sh`   ");
        shell.append("\n#修改回滚脚本为可执行                                                                                                         ");
        shell.append("\n`chmod +x rollback.sh`                                                                                                        ");
        shell.append("\necho \"------------------------------------update end----------------------------------------\"                               ");

        return shell.toString();
    }

}
