/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sharpchnitzel.servletus;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author SharpSchnitzel
 */
public class DataProcessor extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        DatabaseHandler handler = new DatabaseHandler();
        PrintWriter writer = response.getWriter();
        List<String> params = Collections.list(request.getParameterNames());
        if ((!params.contains("id") && !params.contains("row")) || !params.contains("title")) {
            writer.println("{\"success\": false, \"data\": \"forbidden request type\"}");
            return;
        }
        String title = request.getParameterValues("title")[0];
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> columns;
        List<String> primary = new ArrayList<>();
        String query;
        
        if (params.contains("id")) {
            primary = mapper.readValue(request.getParameterValues("id")[0], ArrayList.class);
            if (primary.size() < 1) {
                writer.println("{\"success\": false, \"data\": \"invalid id argument\"}");
                return;
            }
        }
        
        if (params.contains("row")) {
            columns = mapper.readValue(request.getParameterValues("row")[0], LinkedHashMap.class);
            
            if (columns.isEmpty()) {
                writer.println("{\"success\": false, \"data\": \"invalid row argument\"}");
                return;
            }
            
            if (primary.size() > 1) {
                //UPDATE
                query = "update " + title + " set ";
                query += columns.keySet().stream().map(col -> {
                    return col + " = " + (columns.get(col).isBlank() ? "null" : "'" + columns.get(col) + "'");
                }).collect(Collectors.joining(", "));
                query += " where " + primary.get(0) + " = " + primary.get(1);
                writer.println(processUpdate(handler, query));
            } else {
                //INSERT
                query = "insert into " + title + "(";
                query += columns.keySet().stream().collect(Collectors.joining(", ")) + ") values (";
                query += columns.keySet().stream().map(col -> "'" + columns.get(col) + "'").collect(Collectors.joining(", ")) + ")";
                query += "returning " + primary.get(0);
                
                TableWrapper row = (TableWrapper) handler.processQuery(query);
                if (row == null) {
                    writer.println("{\"success\": false, \"data\": \"" + handler.getLastError() + "\"}");
                } else {
                    writer.println("{\"success\": true, \"data\": \"" + (String)row.getList().get(0).values().toArray()[0] + "\"}");
                }
            }
        } else {
            //DELETE
            query = "delete from " + title + " where " + primary.get(0) + " = " + primary.get(1);
            writer.println(processUpdate(handler, query));
        }
    }
    
    private String processUpdate(DatabaseHandler handler, String query) {
        Integer result = (Integer) handler.processQuery(query);
        if (result < 0) {
            return "{\"success\": false, \"data\": \"" + handler.getLastError() + "\"}";
        } else {
         return "{\"success\": true, \"data\": \"" + result + "\"}";   
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        response.sendRedirect("empty"); 
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
