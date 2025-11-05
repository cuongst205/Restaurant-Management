package com.example.btljavafx.utils.dao;

import com.example.btljavafx.model.Food;
import com.example.btljavafx.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {
    private Connection conn;

    public FoodDAO() {
        try {
            conn = DatabaseConnection.getConnection();
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS food (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL NOT NULL
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFood(Food food) {
        String getMaxIdSql = "SELECT IFNULL(MAX(id), 0) + 1 AS nextId FROM food";
        String insertSql = "INSERT INTO food(id, name, price) VALUES(?, ?, ?)";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(getMaxIdSql);
                PreparedStatement pstmt = conn.prepareStatement(insertSql);
        ) {
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt("nextId");
            }

            pstmt.setInt(1, nextId);
            pstmt.setString(2, food.getName());
            pstmt.setDouble(3, food.getPrice());
            pstmt.executeUpdate();

            System.out.println("✅ Đã thêm món mới ID=" + nextId + ", tên=" + food.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Food> getAllFood() {
        List<Food> list = new ArrayList<>();
        String sql = "SELECT * FROM food";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Food f = new Food(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void deleteFood(int id) {
        String deleteSql = "DELETE FROM food WHERE id = ?";
        String shiftSql = "UPDATE food SET id = id - 1 WHERE id > ?";

        try (
                PreparedStatement pstmtDelete = conn.prepareStatement(deleteSql);
                PreparedStatement pstmtShift = conn.prepareStatement(shiftSql);
                Statement stmtReset = conn.createStatement();
        ) {
            pstmtDelete.setInt(1, id);
            pstmtDelete.executeUpdate();
            pstmtShift.setInt(1, id);
            pstmtShift.executeUpdate();
//            stmtReset.execute(resetSql);

            System.out.println("✅ Đã xóa food id=" + id + " và cập nhật lại ID các hàng sau đó.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateFood(Food food) {
        String sql = "UPDATE food SET name = ?, price = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, food.getName());
            pstmt.setDouble(2, food.getPrice());
            pstmt.setInt(3, food.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
