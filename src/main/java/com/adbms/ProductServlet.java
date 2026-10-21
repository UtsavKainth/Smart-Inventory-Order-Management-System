
package com.adbms;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType(
            "application/json;charset=UTF-8"
        );

        String sql =
            "SELECT PRODUCT_ID, PRODUCT_NAME, " +
            "CATEGORY_NAME, SUPPLIER_NAME, " +
            "PRICE, STOCK_QUANTITY, STOCK_STATUS " +
            "FROM ADBMS_INVENTORY_VIEW " +
            "ORDER BY PRODUCT_ID";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            PrintWriter out = response.getWriter()
        ) {
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"id\":")
                    .append(rs.getInt("PRODUCT_ID"))
                    .append(",\"name\":\"")
                    .append(escape(rs.getString("PRODUCT_NAME")))
                    .append("\",\"category\":\"")
                    .append(escape(rs.getString("CATEGORY_NAME")))
                    .append("\",\"supplier\":\"")
                    .append(escape(rs.getString("SUPPLIER_NAME")))
                    .append("\",\"price\":")
                    .append(rs.getBigDecimal("PRICE"))
                    .append(",\"stock\":")
                    .append(rs.getInt("STOCK_QUANTITY"))
                    .append(",\"status\":\"")
                    .append(escape(rs.getString("STOCK_STATUS")))
                    .append("\"}");
            }

            json.append("]");
            out.print(json);

        } catch (Exception e) {
            getServletContext().log(
                "Unable to retrieve inventory", e
            );
            response.setStatus(500);
            response.getWriter().print(
                "{\"error\":\"Unable to retrieve inventory\"}"
            );
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
