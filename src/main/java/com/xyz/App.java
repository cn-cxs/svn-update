package com.xyz;

import com.xyz.bean.Server;
import com.xyz.bean.Svn;
import com.xyz.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class App implements ApplicationRunner {
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static String crrDir = PathUtils.toPath(System.getProperty("user.dir"));//当前工作路径
    private static String workDir = crrDir + "update/";//文件存放路径
    private static String properties = "application.properties";//配置文件名称
    private static String shellFile = "update.sh";//shell脚本名称
    @Autowired
    private Svn svn;

    @Autowired
    private Server server;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
        System.out.println("======================开始======================");
        try {
            createUpdates();
            ShellUtils.buildUpdateShell(workDir,shellFile,server);

            System.out.println("待更新文件已复制到:" + crrDir);
            System.out.println("请把" + crrDir + "整个文件夹上传到要更新的服务器,执行:");
            System.out.println("chomd +x "+shellFile+" & ./"+shellFile);
            System.out.println("命令即可完成文件备份和更新操作");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("======================结束======================");
        System.exit(0);
    }

    /**
     * @param
     * @return void
     * @desc 初始化
     * @author cxs
     * @date 2021-02-06 12:12:01
     **/
    public void init() {
        //创建工作目录
        Path dir = Paths.get(workDir);
        if (!Files.exists(dir)) {
            dir.toFile().mkdirs();
        }
    }


    public void createUpdates(){
        final Date begin = svn.getBeginDate();
        final Date end = svn.getEndDate();
        System.out.printf("检索时间段:%s~%s\r\n", format.format(begin), format.format(end));
        String copyPath = workDir;
        List<File> projects = WorkSpaceUtils.findProject(svn.getWorkSpace());
        projects.forEach(file -> {
            String dir = file.getAbsolutePath() + "\\.svn\\wc.db";
            Path path = Paths.get(dir);
            if (Files.exists(path)) {
                JdbcTemplate jdbcTemplate = SqlLiteUtil.getJdbcTemplate("jdbc:sqlite:" + dir.replaceAll("\\\\", "/"));
                List<Map<String, Object>> list = jdbcTemplate.queryForList("select a.root||'/'||(replace(t.repos_path,t.local_relpath,'')) url  from nodes t,repository a limit 1");
                String url = (String) list.get(0).get("url");
                SvnUtils svnUtils = new SvnUtils(url, svn.getUserName(), svn.getPassword());
                try {
                    Collection logEntries = svnUtils.filterCommitHistory(begin, end, svn.isSelf());
                    System.out.println(Arrays.toString(logEntries.toArray()));
                    List<String> files = OutPutFileUtils.getLocalFiles(logEntries, file.getAbsolutePath());
                    for (int i = 0; i < files.size(); i++) {
                        String f = PathUtils.toPath(files.get(i));
                        Path pt = Paths.get(f);
                        if (!Files.exists(pt)) {
                            System.err.println("文件不存在:" + f);
                        } else if (!Files.isDirectory(pt)) {
                            String newp = copyPath + PathUtils.getRelativePath(svn.getWorkSpace(), Paths.get(f).getParent().toString()).replaceAll("/WebRoot/", "/");
                            Iterator<String> iterator = XmlUtil.ideaOutDirs.stream().iterator();
                            while (iterator.hasNext()) {
                                String s = iterator.next();
                                newp = PathUtils.toPath(newp.replaceAll(s, "/"));
                            }
                            FileUtils.copy(f, newp);
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("文件不存在:" + path);
            }
        });
    }
}
