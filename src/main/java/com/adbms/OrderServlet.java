
package com.adbms;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        String sql =
            "SELECT ORDER_ID, CUSTOMER_NAME, PRODUCT_NAME, " +
            "QUANTITY, UNIT_PRICE, LINE_TOTAL, ORDER_STATUS " +
            "FROM ADBMS_SALES_VIEW ORDER BY ORDER_ID DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"orderId\":")
                    .append(rs.getInt("ORDER_ID"))
                    .append(",\"customer\":\"")
                    .append(escape(rs.getString("CUSTOMER_NAME")))
                    .append("\",\"product\":\"")
                    .append(escape(rs.getString("PRODUCT_NAME")))
                    .append("\",\"quantity\":")
                    .append(rs.getInt("QUANTITY"))
                    .append(",\"price\":")
                    .append(rs.getBigDecimal("UNIT_PRICE"))
                    .append(",\"total\":")
                    .append(rs.getBigDecimal("LINE_TOTAL"))
                    .append(",\"status\":\"")
                    .append(escape(rs.getString("ORDER_STATUS")))
                    .append("\"}");
            }

            json.append("]");
            response.getWriter().print(json);

        } catch (SQLException e) {
            getServletContext().log("Unable to load orders", e);
            response.setStatus(500);
            response.getWriter().print(
                "{\"error\":\"Unable to load orders\"}"
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        int customerId;
        int productId;
        int quantity;

        try {
            customerId = Integer.parseInt(
                request.getParameter("customerId"));
            productId = Integer.parseInt(
                request.getParameter("productId"));
            quantity = Integer.parseInt(
                request.getParameter("quantity"));

            if (customerId <= 0 || productId <= 0 || quantity <= 0) {
                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.getWriter().print(
                "{\"error\":\"Enter valid customer, product and quantity\"}"
            );
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (CallableStatement cs = con.prepareCall(
                    "{call ADBMS_PLACE_ORDER(?,?,?)}")) {

                cs.setInt(1, customerId);
                cs.setInt(2, productId);
                cs.setInt(3, quantity);

                cs.execute();
                con.commit();

                response.getWriter().print(
                    "{\"success\":true,\"message\":\"Order placed successfully\"}"
                );

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            getServletContext().log("Unable to place order", e);

            String message = e.getMessage();

            if (message != null &&
                    message.contains("ORA-20002")) {
                response.setStatus(400);
                response.getWriter().print(
                    "{\"error\":\"Insufficient stock\"}"
                );
            } else {
                response.setStatus(500);
                response.getWriter().print(
                    "{\"error\":\"Unable to place order\"}"
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
