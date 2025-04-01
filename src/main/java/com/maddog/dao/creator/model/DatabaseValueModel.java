package com.maddog.dao.creator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseValueModel {
    String databaseCol;
    String javaProp;
    String javaCapProp;
    String javaType;
    int dbType;
    boolean autoInc;
}
