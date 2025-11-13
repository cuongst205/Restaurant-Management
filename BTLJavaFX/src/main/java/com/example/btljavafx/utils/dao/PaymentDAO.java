package com.example.btljavafx.utils.dao;

import com.example.btljavafx.model.Payment;
import com.example.btljavafx.utils.DatabaseConnection;

import java.sql.*;

public class PaymentDAO {
    private final Connection conn;

    public PaymentDAO() {
        try {
            conn = DatabaseConnection.getConnection();
            createTableIfNotExists();
            ensureSingleRow();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS payment_info (
                    id TEXT PRIMARY KEY,
                    account_number TEXT,
                    bank_name TEXT
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    // Ensure there is a single row with id=1 so we can update easily
    private void ensureSingleRow() throws SQLException {
        String check = "SELECT COUNT(*) FROM payment_info WHERE id = ?";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(check)) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO payment_info(id, account_number, bank_name) VALUES(1, '', '')")) {
                    ps.executeUpdate();
                }
            }
        }
    }

//    public Payment getInfo() {
//        String sql = "SELECT account_number, bank_name FROM payment_info WHERE id = 1";
//        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) {
//                String acc = rs.getString(1);
//                String bank = rs.getString(2);
//                return new Payment(acc == null ? "" : acc, bank == null ? "" : bank);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return new Payment("", "");
//    }

    public void save(String id, String accountNumber, String bankName) {
        String sql = "UPDATE payment_info SET account_number = ?, bank_name = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setString(2, bankName);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insert(String id, String accountNumber, String bankName) {
        String sql = "INSERT INTO payment_info(id, account_number, bank_name) VALUES(?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(2, accountNumber);
                ps.setString(3, bankName);
                ps.setString(1, id);
                ps.executeUpdate();
        } catch (SQLException e) {
                e.printStackTrace();
        }

    }
}
