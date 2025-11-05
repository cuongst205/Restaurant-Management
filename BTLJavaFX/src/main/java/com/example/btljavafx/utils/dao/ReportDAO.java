package com.example.btljavafx.utils.dao;

import com.example.btljavafx.utils.DatabaseConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.*;

public class ReportDAO {
    private Connection conn;
    public ReportDAO() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Map<String, String> getIncome() {
        Map<String, String> result = new HashMap<>();
        String sql = """
                SELECT
                    strftime('%Y-%m', created_at) AS month,
                    SUM(total_price) AS total_revenue
                FROM orders
                GROUP BY month
                ORDER BY month
                """;
        try (Statement stmt = conn.createStatement()) {
            ResultSet res =  stmt.executeQuery(sql);
            String date = res.getString("month");
            String income = res.getString("total_revenue");
            result.put("date", date);
            result.put("income", income);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
