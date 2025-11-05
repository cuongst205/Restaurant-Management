package com.example.btljavafx.utils.dao;

import com.example.btljavafx.utils.DatabaseConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple key-value settings storage in SQLite to persist small configuration
 * like payment account number and selected bank. Replaces payment_info table usage.
 */
public class SettingsDAO {
    private final Connection conn;

    public SettingsDAO() {
        try {
            this.conn = DatabaseConnection.getConnection();
            createTableIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS app_settings (
                    key TEXT PRIMARY KEY,
                    value TEXT
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    public void set(String key, String value) {
        String upsert = "INSERT INTO app_settings(key, value) VALUES(?, ?) " +
                "ON CONFLICT(key) DO UPDATE SET value = excluded.value";
        try (PreparedStatement ps = conn.prepareStatement(upsert)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        String sql = "SELECT value FROM app_settings WHERE key = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getAll() {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT key, value FROM app_settings";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
