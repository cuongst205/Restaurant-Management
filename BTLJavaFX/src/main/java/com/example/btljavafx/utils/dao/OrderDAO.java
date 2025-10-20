package com.example.btljavafx.utils.dao;

import com.example.btljavafx.model.OrderItem;
import com.example.btljavafx.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class OrderDAO {
    private Connection conn;

    public OrderDAO() {
        try {
            conn = DatabaseConnection.getConnection();
            createTablesIfNotExist();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTablesIfNotExist() {
        String sqlOrders = """
        CREATE TABLE IF NOT EXISTS orders (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            total_price REAL DEFAULT 0
        )
        """;

        String sqlOrderItems = """
        CREATE TABLE IF NOT EXISTS order_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            order_id INTEGER,
            food_id INTEGER,
            quantity INTEGER,
            FOREIGN KEY(order_id) REFERENCES orders(id),
            FOREIGN KEY(food_id) REFERENCES food(id)
        )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlOrders);
            stmt.execute(sqlOrderItems);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveOrder(List<OrderItem> items) {
        if (items.isEmpty()) return;

        // Tính tổng giá trị order
        double total = items.stream().mapToDouble(OrderItem::getTotalPrice).sum();

        String sqlOrder = "INSERT INTO orders(total_price) VALUES(?)";
        String sqlItem = "INSERT INTO order_items(order_id, food_id, quantity) VALUES(?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            int orderId;
            // Lưu order và lấy order_id
            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setDouble(1, total);
                psOrder.executeUpdate();
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Không lấy được order_id");
                    }
                }
            }

            // Lưu các order item
            try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                for (OrderItem item : items) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getFood().getId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
