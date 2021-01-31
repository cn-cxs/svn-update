package com.xyz.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class WorkSpaceUtils {
    /**
     * @desc eclipse/idea项目标识
     * @author cxs
     * @date 2021-01-01 07:06:08
     **/
    private static Pattern project_eclipse = Pattern.compile("^\\.classpath$");
    private static Pattern project_idea = Pattern.compile("^\\.idea$");


    /**
     * @param workSpace
     * @return java.util.List<java.io.File>
     * @desc 根据工作空间识别项目, 如:D:\Work\WEBWorkSpaces\,查找该路径下的所有eclipse/idea项目
     * @author cxs
     * @date 2021-01-01 07:04:52
     **/
    public static List<File> findProject(String workSpace) {
        Path path = Paths.get(workSpace);
        List<File> projects = new ArrayList<>();
        if (Files.exists(path) && Files.isDirectory(path)) {
            File file = path.toFile();
            //遍历指定文件夹(工作空间)
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File project = files[i];
                if (project.isFile()) {
                    continue;
                } else {
                    //查找eclipse/idea项目标识
                    File[] files1 = project.listFiles();
                    Stream<File> fileStream = Arrays.stream(files1).filter(ff -> {
                        if (project_idea.matcher(ff.getName()).matches()
                                || project_eclipse.matcher(ff.getName()).matches()) {
                            return true;
                        }
                        return false;
                    });
                    if (fileStream.count() != 0) {
                        projects.add(project);
                        continue;
                    }
                }

            }
        }
        return projects;
    }

    public static File findConfigFile(String project) {
        if(StringUtils.isEmpty(project)){
            return null;
        }
        Path path = Paths.get(project);
        if (!Files.exists(path)) {
            return null;
        }
        File[] files1 = path.toFile().listFiles();
        //优先idea配置
        for (int i = 0; i < files1.length; i++) {
            File ff = files1[i];
            if (project_idea.matcher(ff.getName()).matches()) {
                File[] artifacts = ff.listFiles((f -> {
                    if ("artifacts".equals(f.getName())) {
                        return true;
                    }
                    return false;
                }));
                if (artifacts.length > 0 && artifacts[0] != null) {
                    return artifacts[0].listFiles()[0];
                }
            }
        }
        //idea配置没有,找eclipse配置
        for (int i = 0; i < files1.length; i++) {
            File ff = files1[i];
             if (project_eclipse.matcher(ff.getName()).matches()) {
                return ff;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String workSpace = "D:\\Work\\Idea\\";
        List<File> projects = findProject(workSpace);
        projects.forEach(file -> {
            String dir = file.getAbsolutePath() + "\\.svn\\wc.db";
            Path path = Paths.get(dir);
            if (Files.exists(path)) {
                JdbcTemplate jdbcTemplate = SqlLiteUtil.getJdbcTemplate("jdbc:sqlite:" + dir.replaceAll("\\\\", "/"));
                List<Map<String, Object>> list = jdbcTemplate.queryForList("select a.root||'/'||(replace(t.repos_path,t.local_relpath,'')) url  from nodes t,repository a limit 1");
                System.out.println(list.get(0).get("url"));
            }
        });
    }
}
