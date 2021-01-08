package com.xyz.utils;

import com.xyz.common.Global;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;

public class SvnUtils {

    private String url; //svn地址
    private String userName; //svn用户名
    private String password; //svn密码
    private SVNRepository repository = null;
    private String workSpaces = null;

    class Config {
        final static String _src = "src";
        final static String _java = "java";
        final static String _class = "class";
        final static String WebRoot = "WebRoot";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SVNRepository getRepository() {
        return repository;
    }

    public void setRepository(SVNRepository repository) {
        this.repository = repository;
    }

    public SvnUtils(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public Collection filterCommitHistory(Date begin, Date end, boolean isSelf) throws Exception {
        DAVRepositoryFactory.setup();
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException e) {
            e.printStackTrace();
        }
        // 身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password.toCharArray());
        repository.setAuthenticationManager(authManager);

        long startRevision = repository.getDatedRevision(begin);
        long endRevision = repository.getDatedRevision(end);//表示最后一个版本
        final Collection<SVNLogEntry> history = new ArrayList<>();
        repository.log(new String[]{""},
                startRevision,
                endRevision,
                true,
                true,
                new ISVNLogEntryHandler() {
                    @Override
                    public void handleLogEntry(SVNLogEntry svnlogentry)
                            throws SVNException {
                        //是否获取自己的
                        if (isSelf && !svnlogentry.getAuthor().equals(userName)) {
                            return;
                        }
                        fillResult(svnlogentry);
                    }

                    public void fillResult(SVNLogEntry svnlogentry) {
                        history.add(svnlogentry);
                    }
                });
        return history;

    }

    /**
     * @param logEntries svn提交记录对象
     * @param workSpaces 本地项目工作空间,如:D:/Work/WEBWorkSpaces/
     * @return java.util.List<java.lang.String>
     * @desc 获取svn目录上对应本地要发版的文件目录
     * @author cxs
     * @date 2020-05-09 03:19:33
     **/
    public List<String> getLocalFiles(Collection logEntries, String workSpaces) {
        this.workSpaces = PathUtils.toPath(workSpaces);
        return getLocalFiles(logEntries);
    }

    /**
     * @param logEntries svn提交记录对象
     * @return java.util.List<java.lang.String>
     * @desc 获取svn目录上对应本地要发版的文件目录
     * @author cxs
     * @date 2020-05-09 03:21:18
     **/
    public List<String> getLocalFiles(Collection logEntries) {
        List<String> localFiles = new ArrayList<>();
        File configFile = WorkSpaceUtils.findConfigFile(workSpaces);
        if (configFile == null || !configFile.exists()) {
            return localFiles;
        }
        String outPath = XmlUtil.findOutputPath(configFile.getAbsolutePath());
        String webRoot = null;
        if (outPath.startsWith("$PROJECT_DIR$/")) {
            webRoot = outPath.replaceAll("\\$PROJECT_DIR\\$/", workSpaces);
        } else if (outPath.startsWith("WebRoot/")) {
            webRoot = workSpaces + "WebRoot/";
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
                if (svnPath.endsWith("." + Config._java)) {
                    svnPath = svnPath.replaceAll("." + Config._java, "." + Config._class);
                }
                //src及之前的路径替换为WEB-INF/classes/
                if (svnPath.lastIndexOf(Config._src + "/") > -1) {
                    String target = Config._src + "/";
                    int index = svnPath.lastIndexOf(target);
                    svnPath = webRoot + "WEB-INF/classes/" + (svnPath.substring(index + target.length()));
                }
                //路径/WebRoot直接替换
                if (svnPath.lastIndexOf(Config.WebRoot + "/") > -1) {
                    String target = Config.WebRoot + "/";
                    int index = svnPath.lastIndexOf(target);
                    svnPath = webRoot + (svnPath.substring(index + target.length()));
                }
                localFiles.add(PathUtils.toPath(svnPath));
            }
        }
        return localFiles;
    }

    public static void main(String[] args) throws Exception {
       /* String url = PropUtils.getProp("svnUrl");
        String userName = PropUtils.getProp("svnUser");
        String password = PropUtils.getProp("svnPassword");
        SvnUtils svnUtils = new SvnUtils(url, userName, password);
        // 过滤条件
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date begin = format.parse(PropUtils.getProp("svnBeginDate"));
        final Date end = format.parse(PropUtils.getProp("svnEndDate"));

        Collection logEntries = svnUtils.filterCommitHistory(begin, end,true);
        List<String> list = svnUtils.getLocalFiles(logEntries);
        //System.out.println(new ShellUtils("./","/web/webapp/").buildUpdateShell(list));
        for (String path :
                list) {
            System.out.println(path);
        }*/
    }
}

