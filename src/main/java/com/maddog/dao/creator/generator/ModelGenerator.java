package com.maddog.dao.creator.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.maddog.dao.creator.model.DatabaseValueModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ModelGenerator extends AbstractGenerator {
	

    public void generateModelClass(String capTable, List<DatabaseValueModel> dbValues) throws FileNotFoundException {
    	String packageName = StringUtils.lowerCase(generatorConfig.getBasepackage())+"."+StringUtils.lowerCase(generatorConfig.getClassPrefix());
        String filePath = packageName+".model";
        String dirs = filePath.replaceAll("\\.", File.separator);
        
        setFileName(dirs, capTable+".java");
        println("package "+packageName+".model;");
        println("");
        println("import java.util.Date;");
        println("");
        println("import lombok.Data;");
        println("");
        println("@Data");
        println("public class "+capTable+" {");
        println("");
        for(DatabaseValueModel dbValue : dbValues) {
            println("private "+dbValue.getJavaType()+" "+dbValue.getJavaProp()+";");
        }
        println("");
        println("}");
        closeCurrentFile();
    }

}
