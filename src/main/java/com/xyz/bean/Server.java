package com.xyz.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties("app.server")
public class Server {
    private Map<String, String> ip = new HashMap<>();
    private Map<String, String> project = new HashMap<>();

    public Map<String, String> getIp() {
        return ip;
    }

    public void setIp(Map<String, String> ip) {
        this.ip = ip;
    }

    public Map<String, String> getProject() {
        return project;
    }

    public void setProject(Map<String, String> project) {
        this.project = project;
    }

    public Set<String> getIps() {
        return ip.keySet();
    }

    public String[] getProjectsByIp(String ip) {
        String ps = getIp().get(ip);
        if (StringUtils.hasLength(ps)) {
            return ps.split(",");
        }
        return null;
    }

    @Override
    public String toString() {
        return "Server{" +
                "ip=" + ip +
                ", project=" + project +
                '}';
    }
}
