/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sharpchnitzel.servletus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

/**
 *
 * @author SharpSchnitzel
 */
public class DatabaseHandler<T> {
    
    private final PGSimpleDataSource datasource;
    private String lastError;

    public DatabaseHandler() {
        this.datasource = new PGSimpleDataSource();
        datasource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        datasource.setUser("postgres");
        datasource.setPassword("postgres");
    }
    
    private void handleSQLException(SQLException e) {
        
        switch (e.getSQLState()) {
            case "08001" -> this.lastError = "Unable to connect.";
            case "42P01" -> this.lastError = "Specified table does not exist.";
            case "42703" -> this.lastError = "Wrong column name.";
            case "23502" -> this.lastError = "An attempt to assign a null value to a column with a not-null constraint.";     
            //...........
            default -> {
                this.lastError = e.getSQLState();
                Logger.getLogger(InitLoader.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    public String getLastError() {
        return this.lastError;
    }
    
    public T processQuery(String query) {
        String type = query.toLowerCase().trim().replaceAll("\\s.*", "");
        switch (type) {
            case "update", "delete" -> {
                return (T) executeUpdate(query);
            }
            case "select", "insert" -> {
                return (T) executeSelect(query);
            }
            default -> {
                this.lastError = "unacceptable query type.";
                return null;
            }
        }
    }
    
    private TableWrapper executeSelect(String query) {
        
        try (Connection cn = datasource.getConnection();
            PreparedStatement st = cn.prepareStatement(query);
            ResultSet rs = st.executeQuery()) {
            
            List<LinkedHashMap<String, String>> result = new ArrayList<>();
            ArrayList<String> header = new ArrayList<>();
            
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            
            if (!rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    header.add(meta.getColumnName(i));
                }
                return new TableWrapper(header);
            }
            do {
                LinkedHashMap<String, String> row = new LinkedHashMap<>();
                for (int i = 1; i <= columns; i++) {
                    row.put(meta.getColumnName(i), rs.getString(i));
                }
                result.add(row);
            } while (rs.next());
                    
            return new TableWrapper(result);
            
        } catch (SQLException e) {
            handleSQLException(e);
            return null;
        }
    }
    
    private Integer executeUpdate(String query) {
        try (Connection cn = datasource.getConnection();
            PreparedStatement st = cn.prepareStatement(query)) {
            return st.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            return -1;
        }
    }
        
}
