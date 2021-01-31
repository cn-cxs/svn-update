package com.xyz.utils;

import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ShellUtils {
    private static final String shellHead = "#!/bin/bash";
    public static final String _ip = "ip.";
    public static final String _project = "project.";
    public static final Pattern p_ip = Pattern.compile("^ip\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    public static final Pattern p_project = Pattern.compile("^project\\.\\S*$");

    /**
     * @return java.lang.String
     * @desc 生成更新脚本
     * @author cxs
     * @date 2020-05-10 04:17:26
     **/
    public static String buildUpdateShell(Map<String, String> config) {
        StringBuffer shell = new StringBuffer();
        shell.append(shellHead);
        shell.append("\n	echo \"------------------------------------update start----------------------------------------\"                                              	");
        shell.append("\n	#获取当前时间                                                                                                                                  	");
        shell.append("\n	now=$(date +%Y%m%d-%H%M%S)                                                                                                                     	");
        shell.append("\n	#回滚脚本文件名                                                                                                                                	");
        shell.append("\n	rollback=rollback.sh                                                                                                                           	");
        shell.append("\n	                                                                                                                                               	");
        shell.append("\n	#获取服务器ip                                                                                                                                  	");
        shell.append("\n	server_ip=`ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1 -d '/'`                                                       	");
        shell.append("\n	#定义map,存放ip对应的要更新的项目                                                                                                              	    ");
        shell.append("\n	declare -A file_map=(                                                                                                                          	");
        shell.append("\n").append(getIpShell(config));
        shell.append("\n	)                                                                                                                                              	");
        shell.append("\n	#定义map,存放项目对应的路径                                                                                                                    	");
        shell.append("\n	declare -A path_map=(                                                                                                                          	");
        shell.append("\n").append(getProjectShell(config));
        shell.append("\n	)                                                                                                                                              	");
        shell.append("\n	#根据当前服务器IP获取项目名                                                                                                                    	");
        shell.append("\n	files=${file_map[\"${server_ip}\"]}                                                                                                            	");
        shell.append("\n	                                                                                                                                               	");
        shell.append("\n	function update() {                                                                                                                            	");
        shell.append("\n	    for file in  `find $1` #遍历要更新的文件夹                                                                                                 	");
        shell.append("\n	        do                                                                                                                                     	");
        shell.append("\n	          #组装成服务器的文件路径                                                                                                              	");
        shell.append("\n	          spath=${path_map[\"$1\"]}${file:0}                                                                                                   	");
        shell.append("\n	          oldf=${spath}.${now}                                                                                                                 	");
        shell.append("\n	          #echo $spath                                                                                                                         	");
        shell.append("\n	          if [ -d \"$file\" ]; #如果遍历处要更新的是文件夹                                                                                     	");
        shell.append("\n	            then                                                                                                                               	");
        shell.append("\n	              if [ ! -d \"$spath\" ]; #文件夹不存在则创建                                                                                      	");
        shell.append("\n	              then                                                                                                                             	");
        shell.append("\n	              `mkdir -p ${spath}`                                                                                                              	");
        shell.append("\n	              fi                                                                                                                               	");
        shell.append("\n	          elif [ -f \"${file}\" ];	#如果是文件                                                                                                	");
        shell.append("\n	            then                                                                                                                               	");
        shell.append("\n	              if [ -f \"$spath\" ]; #如果文件存在,则需要备份                                                                                   	");
        shell.append("\n	              then                                                                                                                             	");
        shell.append("\n	                echo \"cp -b ${spath} ${oldf}\"                                                                                                	");
        shell.append("\n	                `cp -b  ${spath} ${oldf}` #备份要被更新的文件                                                                                  	");
        shell.append("\n	                `echo \"mv ${oldf} ${spath}\" >>${rollback}`	#更新动作生成回滚脚本                                                          	");
        shell.append("\n	              fi                                                                                                                               	");
        shell.append("\n	              echo \"cp ${file} ${spath}\"                                                                                                     	");
        shell.append("\n	              `cp ${file} ${spath}` #复制文件到目标位置                                                                                        	");
        shell.append("\n	          fi                                                                                                                                   	");
        shell.append("\n	        done                                                                                                                                   	");
        shell.append("\n	}                                                                                                                                              	");
        shell.append("\n	                                                                                                                                               	");
        shell.append("\n	function main() {                                                                                                                              	");
        shell.append("\n	    if [ -z \"${files}\" ];                                                                                                                    	");
        shell.append("\n	      then                                                                                                                                     	");
        shell.append("\n	        echo \"no config for ${server_ip}\"                                                                                                    	");
        shell.append("\n	    else                                                                                                                                       	");
        shell.append("\n	      #备份回滚脚本                                                                                                                            	");
        shell.append("\n	      if [ -f \"${rollback}\" ];                                                                                                               	");
        shell.append("\n	        then                                                                                                                                   	");
        shell.append("\n	          mv ${rollback} ${rollback}.${now}                                                                                                    	");
        shell.append("\n	      fi                                                                                                                                       	");
        shell.append("\n	      #新建回滚脚本                                                                                                                            	");
        shell.append("\n	      `echo \"#! /bin/bash\">${rollback}`                                                                                                      	");
        shell.append("\n	      `echo \"echo ------------------------------------rollback start--------------------------------------\">>${rollback}`              	");
        shell.append("\n	                                                                                                                                               	");
        shell.append("\n	      #多项目名,先拆分项目名为数据,循环遍历对应更新文件                                                                                        	");
        shell.append("\n	      farray=(${files//,/ })                                                                                                                   	");
        shell.append("\n	      for var in ${farray[@]}                                                                                                                  	");
        shell.append("\n	        do                                                                                                                                     	");
        shell.append("\n	          update $var                                                                                                                          	");
        shell.append("\n	        done                                                                                                                                   	");
        shell.append("\n	      `echo \"echo ------------------------------------rollback end----------------------------------------\">>${rollback}`              	");
        shell.append("\n	      #修改回滚脚本为可执行                                                                                                                    	");
        shell.append("\n	      `chmod +x ${rollback}`                                                                                                                   	");
        shell.append("\n	    fi                                                                                                                                         	");
        shell.append("\n	    echo \"------------------------------------update end----------------------------------------\"                                            	");
        shell.append("\n	}                                                                                                                                              	");
        shell.append("\n	main                                                                                                                                           	");
        return shell.toString();
    }

    private static String getIpShell(Map<String, String> config) {
        StringBuffer sb = new StringBuffer();
        Set<String> keySet = config.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().trim();
            if (p_ip.matcher(key).matches()) {
                String ip = key.replaceAll(_ip, "");
                String project = StringUtils.isEmpty(config.get(key)) ? "" : config.get(key);
                sb.append("[\"").append(ip).append("\"]");
                sb.append("=");
                sb.append("\"").append(project.trim()).append("\"").append("\t");
            }
        }
        return sb.toString();
    }

    private static String getProjectShell(Map<String, String> config) {
        StringBuffer sb = new StringBuffer();
        Set<String> keySet = config.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().trim();
            if (p_project.matcher(key).matches()) {
                String project = key.replaceAll(_project, "");
                String path = StringUtils.isEmpty(config.get(key)) ? "" : config.get(key);
                sb.append("[\"").append(project).append("\"]");
                sb.append("=");
                sb.append("\"").append(path.trim()).append("\"").append("\t");
            }
        }
        return sb.toString();
    }
}
