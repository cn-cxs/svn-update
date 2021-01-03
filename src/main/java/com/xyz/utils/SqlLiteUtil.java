package com.xyz.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.List;
import java.util.Map;

public class SqlLiteUtil {

    /**
     * @desc 根据sqllite数据库文件路径获取JdbcTemplate
     * @param dbUrl
     * @return org.springframework.jdbc.core.JdbcTemplate
     * @author cxs
     * @date 2021-01-01 07:08:00
     **/
    public static JdbcTemplate getJdbcTemplate(String dbUrl) {
        JdbcTemplate jdbcTemplate = null;
        try {
            Driver driver = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
            DataSource ds = new SimpleDriverDataSource(driver, dbUrl);
            jdbcTemplate = new JdbcTemplate(ds);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return jdbcTemplate;

    }

    public static void main(String[] args) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate("jdbc:sqlite:D:/Work/Idea/AM/.svn/wc.db");
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select a.root||'/'||(replace(t.repos_path,t.local_relpath,'')) url  from nodes t,repository a limit 10");
        System.out.println(list);

    }
}
