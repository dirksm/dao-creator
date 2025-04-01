package com.maddog.dao.creator.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.maddog.dao.creator.config.DatabaseConfiguration;
import com.maddog.dao.creator.config.GeneratorConfig;
import com.maddog.dao.creator.model.DatabaseValueModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGenerator {

	@Autowired
	protected DatabaseConfiguration databaseConfiguration;
	
	@Autowired
	protected GeneratorConfig generatorConfig;
	
	private Connection conn = null;
	
	protected int currentSpacing = 0;
	
	protected PrintWriter pw;
	
	protected List<String> generatePrimaryKeys(String tableName, DatabaseMetaData dbMetaData) throws SQLException {
		List<String> pk = new ArrayList<>();
		ResultSet primaryKeys = dbMetaData.getPrimaryKeys(null, null, tableName);
		while (primaryKeys.next()) {
			pk.add(primaryKeys.getString("COLUMN_NAME"));
		}
		return pk;
	}
	
	List<DatabaseValueModel> generateDatabaseValues(String tableName, DatabaseMetaData dbMetaData) throws SQLException {
        List<DatabaseValueModel> dbValues = new ArrayList<DatabaseValueModel>();
        List<String> pk = generatePrimaryKeys(tableName, dbMetaData);
        log.info("pk for {} is {}.", tableName, pk);
        ResultSet rs = dbMetaData.getColumns(null, null, tableName, null);
        while(rs.next()) {
            String typeName = StringUtils.upperCase(rs.getString("TYPE_NAME"));
            String colName = rs.getString("COLUMN_NAME");
            String autoInc = rs.getString("IS_AUTOINCREMENT");
            
            String capPropName = convertSqlNameToProperty(colName);
            String propName = StringUtils.uncapitalize(capPropName);
            String javaType = "String";
            switch(typeName) {
            	case "CHAR":
            	case "CLOB":
                case "RAW":
                case "VARCHAR":
                case "VARCHAR2":
                case "NVARCHAR":
                    javaType = "String";
                    break;
                case "TEXT":
                    javaType = "String";
                    break;
                case "INT":
                case "SMALLINT":
                case "NUMBER":
                    javaType = "int";
                    break;
                case "TINYINT":
                    javaType = "boolean";
                    break;
                case "DOUBLE":
                    javaType = "double";
                    break;
                case "DECIMAL":
                    javaType = "long";
                    break;
                case "FLOAT":
                    javaType = "float";
                    break;
                case "DATE":
                case "DATETIME":
                    javaType = "Date";
                    break;
                default:
                    log.info("*******Unrecognized type: {}", typeName);
                    
            }
            DatabaseValueModel dbValue = new DatabaseValueModel(colName, propName, capPropName, javaType, rs.getInt("DATA_TYPE"),("YES".equals(autoInc) || pk.contains(colName)));
            dbValues.add(dbValue);
        }
        rs.close();
        return dbValues;
	}
	
    public String convertSqlNameToProperty(String sqlName) {
        if(sqlName.indexOf("_")!=-1) {
            String propName = sqlName.substring(0,sqlName.indexOf("_"));
            String capName = sqlName.substring(sqlName.indexOf("_")+1);
            capName = StringUtils.capitalize(StringUtils.lowerCase(capName));
            propName = StringUtils.capitalize(StringUtils.lowerCase(propName));
            return propName+convertSqlNameToProperty(capName);
        } else {
            return StringUtils.capitalize(StringUtils.lowerCase(sqlName));
        }
    }
    
    public Connection getConnection() throws SQLException {
    	if (this.conn == null) {
			establishConnection();
		}
    	return this.conn;
    }

    protected void incSpacing() {
        this.currentSpacing += generatorConfig.getSpacing();
    }
    
    protected void decSpacing() {
        this.currentSpacing -= generatorConfig.getSpacing();
    }

    protected String setSpacing(String s, int spacing) {
        if(spacing==0) {
            return s;
        }
        String os = String.format("%"+spacing+"c",' ');
        return os+s;
    }
    
    protected String println(String s) {
        if(s.startsWith("}")) {
            decSpacing();
        }
        s = setSpacing(s, this.currentSpacing);
        pw.println(s);
        if(s.endsWith("{")) {
            incSpacing();
        }
        return s;
    }
    
    public void println() {
        pw.println();
    }

    public void setFileName(String directory, String filename) throws FileNotFoundException {
        closeCurrentFile();
        
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        
        File f = new File(directory + File.separator + filename);

        pw = new PrintWriter(f);
    }
    
    public void closeCurrentFile() {
        if(pw!=null) {
            pw.close();
        }
        pw = null;
    }

    private void establishConnection() throws SQLException {
        String dbConnStr = "sqlserver".equals(databaseConfiguration.getType())?getMSSQLDBConnString():getDBConnString();
        log.info("Db connection string: {}, user:{}, password:{}", dbConnStr, databaseConfiguration.getUser(), databaseConfiguration.getPassword());
        this.conn = DriverManager.getConnection(dbConnStr,databaseConfiguration.getUser(), databaseConfiguration.getPassword());
        log.info("Connected to database");
    }
    
    private String getDBConnString() {
        StringBuffer sb = new StringBuffer();
        sb.append("jdbc:")
        .append(databaseConfiguration.getType());
        if ("oracle:thin".equals(databaseConfiguration.getType())) {
	        sb.append(":@");			
		} else {
	        sb.append("://");
		}
        sb.append(databaseConfiguration.getServer())
        .append(":")
        .append(databaseConfiguration.getPort())
        .append("/")
        .append(databaseConfiguration.getName())
        .append(databaseConfiguration.getCustomConnectionString());
        return sb.toString();
    }

    private String getMSSQLDBConnString() {
        StringBuffer sb = new StringBuffer();
        sb.append("jdbc:")
        .append(databaseConfiguration.getType())
        .append("://")
        .append(databaseConfiguration.getServer())
        .append(":")
        .append(databaseConfiguration.getPort())
        .append(";")
        .append(databaseConfiguration.getCustomConnectionString());
        return sb.toString();
    }

}
