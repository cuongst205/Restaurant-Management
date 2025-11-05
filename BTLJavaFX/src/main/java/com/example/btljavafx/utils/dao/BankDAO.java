package com.example.btljavafx.utils.dao;

import com.example.btljavafx.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDAO {
    private final Connection conn;

    public BankDAO() {
        try {
            this.conn = DatabaseConnection.getConnection();
            createTableIfNotExists();
            seedIfEmpty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS bank_info (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    short_name TEXT NOT NULL UNIQUE,
                    bin_code TEXT NOT NULL
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }
    public List<String> getBankNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT short_name FROM bank_info";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private boolean isEmpty() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bank_info";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private void seedIfEmpty() throws SQLException {
        if (!isEmpty()) return;

        String[][] data = new String[][]{
                {"Agribank", "970499"},
                {"Vietinbank", "970489"},
                {"DongABank", "970406"},
                {"Saigonbank", "161087"},
                {"BIDV", "970488"},
                {"SeABank", "970468"},
                {"GP.Bank", "970408"},
                {"PG Bank", "970430"},
                {"PVcomBank", "970412"},
                {"Kienlongbank", "970452"},
                {"Vietcapital Bank", "970454"},
                {"VietBank", "970433"},
                {"OceanBank", "970414"},
                {"Sacombank", "970403"},
                {"ABBank", "970459"},
                {"Ngân hàng Liên doanh Việt Nga", "970421"},
                {"VCB", "686868"},
                {"ACB", "970416"},
                {"Eximbank", "452999"},
                {"TPBank", "970423"},
                {"SHB", "970443"},
                {"HDBank", "970437"},
                {"MBBank", "970422"},
                {"VPBank", "981957"},
                {"VIB", "180906"},
                {"Việt Á", "166888"},
                {"Techcombank", "888899"},
                {"OCB", "970448"},
                {"NCB", "818188"},
                {"HLBVN", "970442"},
                {"LienVietPostBank", "970449"},
                {"BacABank", "970409"},
                {"BVB", "970438"},
                {"ShinhanVN", "970424"},
                {"VID Public", "970439"},
                {"SCB", "157979"},
                {"MaritimeBank", "970426"},
                {"Nam Á", "970428"},
                {"Indovina", "970434"},
                {"Woori Việt Nam", "970457"},
                {"IBK", "970455"},
                {"Co-op Bank", "970446"},
                {"CIMB", "422589"},
                {"UOB", "970458"}
        };

        String sql = "INSERT OR IGNORE INTO bank_info(short_name, bin_code) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] row : data) {
                ps.setString(1, row[0]);
                ps.setString(2, row[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<String[]> getAll() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT short_name, bin_code FROM bank_info ORDER BY short_name";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{rs.getString(1), rs.getString(2)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String findBinByShortName(String shortName) {
        String sql = "SELECT bin_code FROM bank_info WHERE short_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shortName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
