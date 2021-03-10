/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sharpchnitzel.servletus;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SharpSchnitzel
 */
public class InitLoader extends HttpServlet {

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
        
        String query = "select table_name from information_schema.tables where table_schema = 'public' order by 1";
        TableWrapper tableList = (TableWrapper) handler.processQuery(query);
        
        if (tableList == null) {
            writer.println("{\"success\": false, \"data\": \"" + handler.getLastError() + "\"}");
            return;
        }
        
        if (tableList.getList().isEmpty()) {
            writer.println("{\"success\": false, \"data\": \"There are no tables to read\"}");
            return;
        }

        List<TableWrapper> data = new ArrayList<>();

        tableList.getList().forEach(el -> {
            String tableName = (String)el.values().toArray()[0];
            String dataQuery = "select * from " + tableName + " order by 1";
            TableWrapper table = (TableWrapper) handler.processQuery(dataQuery);
            
            if (table == null) {
                writer.println("{\"success\": false, \"data\": \"" + handler.getLastError() + "\"}");
                return;
            }
            
            table.setTitle(tableName);
            data.add(table);
        });

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);
        writer.println("{\"success\": true, \"data\": " + json + "}");
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
        response.sendRedirect("empty");   
        //processRequest(request, response);
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
