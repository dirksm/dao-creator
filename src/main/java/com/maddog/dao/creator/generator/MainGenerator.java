package com.maddog.dao.creator.generator;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.maddog.dao.creator.model.DatabaseValueModel;
import com.maddog.dao.creator.model.TableValueModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("mainGenerator")
public class MainGenerator extends AbstractGenerator {
	
	@Autowired
	ModelGenerator modelGenerator;
	
	@Autowired
	DaoGenerator daoGenerator;
	
	public void generate() {
		log.info("db config: {}",databaseConfiguration);
		log.info("generator config: {}", generatorConfig);
		Connection conn = null;
		try {
	        conn = getConnection();
	        DatabaseMetaData dbMetaData = conn.getMetaData();
	        List<String> tables = retrieveTableList(dbMetaData);
	        Map<TableValueModel, List<DatabaseValueModel>> tableMap = new HashMap<TableValueModel, List<DatabaseValueModel>>();
	        for(String table : tables) {
	            String capTable = convertSqlNameToProperty(table);
	            capTable = StringUtils.capitalize(StringUtils.lowerCase(capTable));

	            log.info("*** Grabbing Columns for {}",table);
	            List<DatabaseValueModel> dbValues = generateDatabaseValues(table, dbMetaData);
	            TableValueModel tVal = new TableValueModel(table, capTable);
	            tableMap.put(tVal, dbValues);
	            
	            // Generate model
	            log.info("generating model class for {} using {}", capTable, dbValues);
	            modelGenerator.generateModelClass(capTable, dbValues);
	            // Generate DAO Interface
	            log.info("generating dao interface class for {} using {}", capTable, dbValues);
	            daoGenerator.createDaoInterface(capTable, tVal, dbValues);
	            // Generate DAO Implementation
	            log.info("generating dao impl class for {} using {}", capTable, dbValues);
		        daoGenerator.createDaoImpl(capTable, tVal, dbValues);
	            log.info("generating dao extractor class for {} using {}", capTable, dbValues);
		        daoGenerator.generateExtractorSubClass(capTable, tVal, dbValues);
	            log.info("generating dao creator class for {} using {}", capTable, dbValues);
		        daoGenerator.generateCreatorSubClass(capTable, tVal, dbValues);
	            log.info("generating dao updater class for {} using {}", capTable, dbValues);
		        daoGenerator.generateUpdaterSubClass(capTable, tVal, dbValues);
	        }
			
		} catch (SQLException se) {
			log.error("Exception generating code: {}", se.getMessage(), se);
		} catch (FileNotFoundException fnfe) {
			log.error("File Not Found Exception generating code: {}", fnfe.getMessage(), fnfe);
		} finally {
	        try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
	
	private List<String> retrieveTableList(DatabaseMetaData dbMetaData) {
		ResultSet rs = null;
        List<String> tables = new ArrayList<String>();
        try {
			rs = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
	        while(rs.next()) {
	            String tableName = rs.getString("TABLE_NAME");
	            log.info("["+tableName+"]");
	            if (generatorConfig.isAllTables() || (
	            		!CollectionUtils.isEmpty(databaseConfiguration.getTables()) && 
	            		databaseConfiguration.getTables().contains(tableName))) {
		            tables.add(tableName);
		            log.info("["+tableName+"] is added");
				}
	        }
		} catch (SQLException e) {
			log.error("Exception retrieving table list: {}", e.getMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {}
		}
        return tables;
	}

}
