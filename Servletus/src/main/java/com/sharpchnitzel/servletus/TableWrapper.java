/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sharpchnitzel.servletus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author SharpSchnitzel
 */
public class TableWrapper {
    private String title;
    private List<String> columns;
    private List<LinkedHashMap<String, String>> list;
    
    public TableWrapper () {
        this.title = "resultSet";
        this.columns = new ArrayList<>();
        this.list = new ArrayList<>();
    }
    
    public TableWrapper (List<LinkedHashMap<String, String>> list) {
        this();
        this.setList(list);
    }
    
    public TableWrapper (ArrayList<String> columns) {
        this();
        this.columns = columns;
    }
    
    public TableWrapper (String title) {
        this();
        this.title = title;
    }
    
    public TableWrapper (String title, List<LinkedHashMap<String, String>> list) {
        this();
        this.title = title;
        this.setList(list);
    }
    
    public TableWrapper (String title, ArrayList<String> columns) {
        this();
        this.title = title;
        this.columns = columns;
    }
    
    public TableWrapper (ArrayList<String> columns, List<LinkedHashMap<String, String>> list) {
        this();
        if (columns.isEmpty()) this.columns = columns;
        this.setList(list);
    }
    
    public TableWrapper (String title, ArrayList<String> columns, List<LinkedHashMap<String, String>> list) {
        this();
        this.title = title;
        if (columns.isEmpty()) this.columns = columns;
        this.setList(list);
    }
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<LinkedHashMap<String, String>> getList() {
        return this.list;
    }
    
    public List<String> getColumns() {
        return this.columns;
    }

    public final void setList(List<LinkedHashMap<String, String>> list) {
        this.list = list;
        if (!this.list.isEmpty()) {
            this.columns = list.get(0).keySet().stream().collect(Collectors.toList());
        }
    }
    
}
