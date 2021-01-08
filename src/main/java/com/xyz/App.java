package com.xyz;

import com.xyz.utils.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private static String prop = "";//默认配置文件路径
    private static String prop_file = "update.conf";//默认配置文件路径
    private static String crrDir = PathUtils.toPath(System.getProperty("user.dir")) + "update/";//当前工作路径
    final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws Exception {

        String newProp = args.length > 0 ? args[0] : null;
        if (newProp != null && !"".equals(newProp)) {
            prop = newProp;
        } else {
            prop = PathUtils.toPath(System.getProperty("user.dir")) + prop_file;
        }
        System.out.println("======================开始======================");
        createUpdates();
        String shell = ShellUtils.buildUpdateShell(PropUtils.getProp(prop, "serverPath"));
        Path shellFile = Paths.get(crrDir + "update.sh");
        if (!Files.exists(shellFile)) {
            Files.createFile(shellFile);
        }
        Files.write(shellFile, shell.getBytes());
        System.out.println("待更新文件已复制到:" + crrDir);
        System.out.println("请把" + crrDir + "整个文件夹上传到要更新的服务器,执行:");
        System.out.println("chom +x update.sh & ./update.sh");
        System.out.println("命令即可完成文件备份和更新操作");
        System.out.println("======================结束======================");
    }

    public static void createUpdates() throws ParseException {
        final String userName = PropUtils.getProp(prop, "svnUser");
        final String password = PropUtils.getProp(prop, "svnPassword");
        final String workSpace = PropUtils.getProp(prop, "workSpace");
        final String svnBeginDate = PropUtils.getProp(prop, "svnBeginDate");
        final String svnEndDate = PropUtils.getProp(prop, "svnEndDate");
        final boolean isSelf = Boolean.parseBoolean(PropUtils.getProp(prop, "isSelf"));
        final Date begin = format.parse(svnBeginDate);
        final Date end = format.parse(svnEndDate);
        //yyyy-MM-dd时间格式,结束时间需要+1
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(end);
        calendar.add(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        String copyPath = crrDir;
        List<File> projects = WorkSpaceUtils.findProject(workSpace);
        projects.forEach(file -> {
            String dir = file.getAbsolutePath() + "\\.svn\\wc.db";
            Path path = Paths.get(dir);
            if (Files.exists(path)) {
                JdbcTemplate jdbcTemplate = SqlLiteUtil.getJdbcTemplate("jdbc:sqlite:" + dir.replaceAll("\\\\", "/"));
                List<Map<String, Object>> list = jdbcTemplate.queryForList("select a.root||'/'||(replace(t.repos_path,t.local_relpath,'')) url  from nodes t,repository a limit 1");
                String url = (String) list.get(0).get("url");
                SvnUtils svnUtils = new SvnUtils(url, userName, password);
                try {
                    Collection logEntries = svnUtils.filterCommitHistory(begin, end, isSelf);
                    List<String> files = svnUtils.getLocalFiles(logEntries, file.getAbsolutePath());
                    for (int i = 0; i < files.size(); i++) {
                        String f = PathUtils.toPath(files.get(i));
                        Path pt = Paths.get(f);
                        if (!Files.exists(pt)) {
                            System.err.println("文件不存在:" + f);
                        } else if (!Files.isDirectory(pt)) {
                            /*Matcher temp = Global.project.matcher(f);//正则匹配项目子目录
                            if (temp.find()) {
                                String newp = copyPath + PathUtils.getRelativePath(workSpace, Paths.get(f).getParent().toString()).replaceAll("/WebRoot/", "/");
                                FileUtils.copy(f, newp);
                            }*/
                            String newp = copyPath + PathUtils.getRelativePath(workSpace, Paths.get(f).getParent().toString()).replaceAll("/WebRoot/", "/");
                            Iterator<String> iterator = XmlUtil.ideaOutDirs.stream().iterator();
                            while (iterator.hasNext()) {
                                String s = iterator.next();
                                newp = PathUtils.toPath(newp.replaceAll(s, "/"));
                            }
                            FileUtils.copy(f, newp);
                        }
                    }
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("文件不存在:" + path);
            }
        });
    }
}
