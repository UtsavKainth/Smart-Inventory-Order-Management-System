
package com.adbms;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuditServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String sql =
            "SELECT A.AUDIT_ID, A.PRODUCT_ID, " +
            "P.PRODUCT_NAME, A.OLD_STOCK, A.NEW_STOCK, " +
            "TO_CHAR(A.CHANGE_DATE, 'YYYY-MM-DD HH24:MI:SS') " +
            "AS CHANGE_DATE, A.CHANGED_BY " +
            "FROM ADBMS_STOCK_AUDIT A " +
            "JOIN ADBMS_PRODUCTS P " +
            "ON A.PRODUCT_ID = P.PRODUCT_ID " +
            "ORDER BY A.AUDIT_ID DESC";

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
                    .append("\"auditId\":")
                    .append(rs.getInt("AUDIT_ID")).append(",")
                    .append("\"productId\":")
                    .append(rs.getInt("PRODUCT_ID")).append(",")
                    .append("\"product\":\"")
                    .append(escape(rs.getString("PRODUCT_NAME")))
                    .append("\",")
                    .append("\"oldStock\":")
                    .append(rs.getInt("OLD_STOCK")).append(",")
                    .append("\"newStock\":")
                    .append(rs.getInt("NEW_STOCK")).append(",")
                    .append("\"changeDate\":\"")
                    .append(rs.getString("CHANGE_DATE"))
                    .append("\",")
                    .append("\"changedBy\":\"")
                    .append(escape(rs.getString("CHANGED_BY")))
                    .append("\"}");
            }

            json.append("]");
            out.print(json);

        } catch (Exception e) {
            getServletContext().log(
                "Unable to retrieve stock audit records", e
            );

            if (!response.isCommitted()) {
                response.reset();
                response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(
                    "{\"error\":\"Unable to load stock audit records\"}"
                );
            }
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
