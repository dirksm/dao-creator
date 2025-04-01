package com.maddog.dao.creator.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.maddog.dao.creator.model.DatabaseValueModel;
import com.maddog.dao.creator.model.TableValueModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DaoGenerator extends AbstractGenerator {

    public void createDaoInterface(String capTable, TableValueModel tVal, List<DatabaseValueModel> dbVals) throws FileNotFoundException {
        currentSpacing = 0;

        String packageName = StringUtils.lowerCase(generatorConfig.getBasepackage())+"."+StringUtils.lowerCase(generatorConfig.getClassPrefix());
        String filePath = packageName+".dao";
        String dirs = filePath.replaceAll("\\.", File.separator);

        setFileName(dirs, capTable+"Dao.java");
    	
        println("package "+packageName+".dao;");
        println("");
        println("import java.util.List;");
        println("");
        println("import "+packageName+".model."+capTable+";");
        println("");
        println("public interface "+capTable+"Dao {");
        
        println("public List<"+tVal.getJavaClass()+"> get"+tVal.getJavaClass()+(tVal.getJavaClass().endsWith("s")?"":"s")+"();");
        println("public void create"+tVal.getJavaClass()+"("+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+");");
        println("public void update"+tVal.getJavaClass()+"("+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+");");
        for(DatabaseValueModel dbVal : dbVals) {
            if(dbVal.isAutoInc()) {
                println("public "+tVal.getJavaClass()+" get"+tVal.getJavaClass()+"ById(int "+dbVal.getJavaProp()+");");
            }
        }
        println("}");
        println("");
        closeCurrentFile();
    }
    
    public void createDaoImpl(String capTable, TableValueModel tVal, List<DatabaseValueModel> dbVals) throws FileNotFoundException {
        
        currentSpacing = 0;

    	String packageName = StringUtils.lowerCase(generatorConfig.getBasepackage())+"."+StringUtils.lowerCase(generatorConfig.getClassPrefix());
        String filePath = packageName+".dao.impl";
        String dirs = filePath.replaceAll("\\.", File.separator);

        setFileName(dirs, capTable+"DaoImpl.java");

        println("package "+packageName+".dao.impl;");
        println("");
        println("import java.util.Collections;");
        println("import java.util.List;");
        println("import java.util.Map;");
        println("");
   		println("import javax.sql.DataSource;");
        println("");
   		println("import org.springframework.beans.factory.annotation.Autowired;");
   		println("import org.springframework.jdbc.core.JdbcTemplate;");
   		println("import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;");
   		println("import org.springframework.jdbc.support.GeneratedKeyHolder;");
   		println("import org.springframework.jdbc.support.KeyHolder;");
   		println("import org.springframework.stereotype.Component;");
        println("");
        println("import "+packageName+".dao."+capTable+"Dao;");
   		println("import "+packageName+".dao.creator."+capTable+"Creator;");
   		println("import "+packageName+".dao.extractor."+capTable+"Extractor;");
   		println("import "+packageName+".dao.updater."+capTable+"Updater;");
        println("import "+packageName+".model."+capTable+";");
        println("");
        println("");
        println("@Component(\""+StringUtils.lowerCase(capTable)+"Dao\")");
        println("public class "+capTable+"DaoImpl implements "+capTable+"Dao {");
        println();
        println("@Autowired");
        println("private JdbcTemplate jdbcTemplate;");
        println();
        println("@Autowired");
        println("private NamedParameterJdbcTemplate namedJdbcTemplate;");
        println();
        println("private JdbcTemplate getJdbcTemplate() {");
        println("return this.jdbcTemplate;");
        println("}");
        println("");
        println("private NamedParameterJdbcTemplate getNamedParamTemplate() {");
        println("return this.namedJdbcTemplate;");
        println("}");
        println("");
        String selectStr = "String selectStr = \"SELECT ";
        for(DatabaseValueModel dbVal : dbVals) {
            selectStr += dbVal.getDatabaseCol()+", ";
        }
        selectStr = selectStr.substring(0, selectStr.length()-2)+" FROM "+tVal.getDatabaseTable()+"\";";
        println(selectStr);
        println("");
        println("@Override");
        println("public List<"+tVal.getJavaClass()+"> get"+tVal.getJavaClass()+(tVal.getJavaClass().endsWith("s")?"":"s")+"() {");
        println("return getJdbcTemplate().query(selectStr, new "+tVal.getJavaClass()+"Extractor());");
        println("}");
        println("");
        println("@Override");
        println("public void create"+tVal.getJavaClass()+"("+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+") {");
        println("KeyHolder keyHolder = new GeneratedKeyHolder();");
        println("getJdbcTemplate().update(new "+tVal.getJavaClass()+"Creator("+tVal.getJavaClass().toLowerCase()+"), keyHolder);");
        for(DatabaseValueModel dbVal : dbVals) {
            if(dbVal.isAutoInc()) {
                println(""+tVal.getJavaClass().toLowerCase()+".set"+dbVal.getJavaCapProp()+"(keyHolder.getKey().intValue());");
                break;
            }
        }
        println("}");
        println("");
        println("@Override");
        println("public void update"+tVal.getJavaClass()+"("+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+") {");
        println("getJdbcTemplate().update(new "+tVal.getJavaClass()+"Updater("+tVal.getJavaClass().toLowerCase()+"));");
        println("}");
        String primaryKey = "";
        String primaryObj = "";
        for(DatabaseValueModel dbVal : dbVals) {
            if(dbVal.isAutoInc()) {
            	primaryKey = dbVal.getDatabaseCol();
            	primaryObj = dbVal.getJavaProp();
            }
        }
        String whereStr = "String sqlStr = selectStr + \" WHERE "+primaryKey+"=:"+primaryObj+"\";";
        println("");
    	println("@Override");
        println("public "+tVal.getJavaClass()+" get"+tVal.getJavaClass()+"ById(int "+primaryObj+") {");
        println("Map namedParameters = Collections.singletonMap(\""+primaryObj+"\", "+primaryObj+");");
        println(whereStr);
        println("return getNamedParamTemplate().queryForObject(sqlStr, namedParameters, new "+tVal.getJavaClass()+"Extractor());");
        println("}");
        println("");
        println("}");
        closeCurrentFile();
    }
    
    public void generateExtractorSubClass(String capTable, TableValueModel tVal, List<DatabaseValueModel> dbVals) throws FileNotFoundException {
    	
        currentSpacing = 0;

    	String packageName = StringUtils.lowerCase(generatorConfig.getBasepackage())+"."+StringUtils.lowerCase(generatorConfig.getClassPrefix());
        String filePath = packageName+".dao.extractor";
        String dirs = filePath.replaceAll("\\.", File.separator);

        setFileName(dirs, tVal.getJavaClass()+"Extractor.java");
        println("package "+packageName+".dao.extractor;");
        println("");
        println("import java.sql.ResultSet;");
        println("import java.sql.SQLException;");
        println("");
        println("import org.springframework.jdbc.core.RowMapper;");
        println("");
        println("import "+packageName+".model."+capTable+";");
        println("");
        println("public class "+tVal.getJavaClass()+"Extractor implements RowMapper<"+tVal.getJavaClass()+"> {");
        println("");
        println("@Override");
        println("public "+tVal.getJavaClass()+" mapRow(ResultSet rs, int i) throws SQLException {");
        String varName = "model";
        println(tVal.getJavaClass()+" "+varName+" = new "+tVal.getJavaClass()+"();");
        for(DatabaseValueModel dbValue : dbVals) {
            println(varName+".set"+dbValue.getJavaCapProp()+"(rs.get"+StringUtils.capitalize(dbValue.getJavaType())+"(\""+dbValue.getDatabaseCol()+"\"));");
        }
        println("return "+varName+";");
        println("}");
        println("");
        println("}");
        closeCurrentFile();
    }
    
    void generateCreatorSubClass(String capTable, TableValueModel tVal, List<DatabaseValueModel> dbVals) throws FileNotFoundException {
        currentSpacing = 0;

    	String packageName = StringUtils.lowerCase(generatorConfig.getBasepackage())+"."+StringUtils.lowerCase(generatorConfig.getClassPrefix());
        String filePath = packageName+".dao.creator";
        String dirs = filePath.replaceAll("\\.", File.separator);

        setFileName(dirs, tVal.getJavaClass()+"Creator.java");
        println("package "+packageName+".dao.creator;");
        println("");
        println("import java.sql.Connection;");
        println("import java.sql.PreparedStatement;");
        println("import java.sql.SQLException;");
        println("import java.sql.Statement;");
        println("");
        println("import org.springframework.jdbc.core.PreparedStatementCreator;");
        println("");
        println("import "+packageName+".model."+capTable+";");
        println("");
        println("public class "+tVal.getJavaClass()+"Creator implements PreparedStatementCreator {");
        println("");
        println("private "+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+";");
        println("");
        println("public "+tVal.getJavaClass()+"Creator("+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+") {");
        println("this."+tVal.getJavaClass().toLowerCase()+" = "+tVal.getJavaClass().toLowerCase()+";");
        println("}");
        println("");
        println("@Override");
        println("public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {");
        println("PreparedStatement ps = connection.prepareStatement(");
        String insertStr = "      \"INSERT INTO "+tVal.getDatabaseTable()+" (";
        for(DatabaseValueModel dbVal : dbVals) {
            if(!dbVal.isAutoInc()) {
                insertStr += dbVal.getDatabaseCol()+", ";
            }
        }
        insertStr = insertStr.substring(0, insertStr.length()-2)+") \"\n";
        insertStr += "      + \"VALUES (";
        for(DatabaseValueModel dbVal : dbVals) {
            if(!dbVal.isAutoInc()) {
                if(dbVal.getJavaType().equals("Date")) {
                    insertStr += "now(), ";
                } else {
                    insertStr += "?, ";
                }
            }
        }
        insertStr = insertStr.substring(0, insertStr.length()-2)+") \", Statement.RETURN_GENERATED_KEYS);";
        println(insertStr);
        int counter = 1;
        for(DatabaseValueModel dbVal : dbVals) {
            if(!dbVal.isAutoInc() && !dbVal.getJavaType().equals("Date")) {
                if(dbVal.getJavaType().equals("String")) {
                    println("ps.setString("+counter+", "+tVal.getJavaClass().toLowerCase()+".get"+dbVal.getJavaCapProp()+"());");
                    counter++;
                } else if(dbVal.getJavaType().equals("int")){
                    println("ps.setInt("+counter+", "+tVal.getJavaClass().toLowerCase()+".get"+dbVal.getJavaCapProp()+"());");
                    counter++;
                }
            }
        }
        println("return ps;");
        println("}");
        println("");
        println("}");
        closeCurrentFile();
    }
    
    void generateUpdaterSubClass(String capTable, TableValueModel tVal, List<DatabaseValueModel> dbVals) throws FileNotFoundException {
        currentSpacing = 0;

    	String packageName = StringUtils.lowerCase(generatorConfig.getBasepackage())+"."+StringUtils.lowerCase(generatorConfig.getClassPrefix());
        String filePath = packageName+".dao.updater";
        String dirs = filePath.replaceAll("\\.", File.separator);

        setFileName(dirs, tVal.getJavaClass()+"Updater.java");
        println("package "+packageName+".dao.updater;");
        println("");
        println("import java.sql.Connection;");
        println("import java.sql.PreparedStatement;");
        println("import java.sql.SQLException;");
        println("");
        println("import org.springframework.jdbc.core.PreparedStatementCreator;");
        println("");
        println("import "+packageName+".model."+capTable+";");
        println("");
        println("public class "+tVal.getJavaClass()+"Updater implements PreparedStatementCreator {");
        println("");
        println("private "+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+";");
        println("");
        println("public "+tVal.getJavaClass()+"Updater("+tVal.getJavaClass()+" "+tVal.getJavaClass().toLowerCase()+") {");
        println("this."+tVal.getJavaClass().toLowerCase()+" = "+tVal.getJavaClass().toLowerCase()+";");
        println("}");
        println("");
        println("@Override");
        println("public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {");
        println("PreparedStatement ps = connection.prepareStatement(");
        String updateStr = "      \"UPDATE "+tVal.getDatabaseTable()+" SET ";
        for(DatabaseValueModel dbVal : dbVals) {
            if(!dbVal.isAutoInc()) {
                updateStr += dbVal.getDatabaseCol()+"=?, ";
            }
        }
        updateStr = updateStr.substring(0, updateStr.length()-2)+" WHERE ";
        for(DatabaseValueModel dbVal : dbVals) {
            if(dbVal.isAutoInc()) {
                updateStr += dbVal.getDatabaseCol()+"=?\"";
            }
        }
        println(updateStr+");");
        
        int counter = 1;
        for(DatabaseValueModel dbVal : dbVals) {
            if(!dbVal.isAutoInc()) {
                if(dbVal.getJavaType().equals("Date")) {
                    println("ps.setDate("+counter+", new java.sql.Date("+tVal.getJavaClass().toLowerCase()+".get"+dbVal.getJavaCapProp()+"().getTime()));");
                    counter++;
                } else {
                    println("ps.set"+StringUtils.capitalize(dbVal.getJavaType())+"("+counter+", "+tVal.getJavaClass().toLowerCase()+".get"+dbVal.getJavaCapProp()+"());");
                    counter++;                   
                }
            }
        }
        for(DatabaseValueModel dbVal : dbVals) {
            if(dbVal.isAutoInc()) {
                println("ps.set"+StringUtils.capitalize(dbVal.getJavaType())+"("+counter+", "+tVal.getJavaClass().toLowerCase()+".get"+dbVal.getJavaCapProp()+"());");
                counter++;
                break;
            }
        }
        println("return ps;");
        println("}");
        println("");
        println("}");
        closeCurrentFile();
    }

    
}