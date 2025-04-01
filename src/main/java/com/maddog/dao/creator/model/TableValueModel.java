package com.maddog.dao.creator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableValueModel {
    String databaseTable;
    String javaClass;
}
