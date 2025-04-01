package com.maddog.dao.creator.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "application.database")
public class DatabaseConfiguration {
    private String server;
    private String port;
    private String user;
    private String password;
    private String name;
    private String type;
    private String customConnectionString;
    private List<String> tables;
}
