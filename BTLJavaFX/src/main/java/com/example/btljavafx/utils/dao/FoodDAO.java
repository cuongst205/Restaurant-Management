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
        String sql = "INSERT INTO food(name, price) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, food.getName());
            pstmt.setDouble(2, food.getPrice());
            pstmt.executeUpdate();
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
        String sql = "DELETE FROM food WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
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
