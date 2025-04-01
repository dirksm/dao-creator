package com.maddog.dao.creator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "application.generator")
public class GeneratorConfig {
    private String basepackage;
    private int spacing;
    private String classPrefix;
    private boolean allTables;
}
