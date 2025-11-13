package com.example.btljavafx.model;

import com.example.btljavafx.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class NhanVien {
    private String id_Nhan_Vien_PK;
    private String HoTen;
    private String ChucVu;
    private String MatKhau;
    private String numAccount;
    private String bankName;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    private String createID(String s) throws SQLException {
        if (s == null || s.trim().isEmpty()) return "";

        // Chuẩn hóa bỏ dấu tiếng Việt
        String normalized = s.toLowerCase()
                .replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[íìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("đ", "d");

        String[] parts = normalized.trim().split("\\s+");
        if (parts.length == 0) return "";

        StringBuilder sb = new StringBuilder(parts[parts.length - 1]);
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(parts[i].charAt(0));
        }

        String baseId = sb.toString();
        String newId = baseId;
        int suffix = 2;

        try (Connection connection = DatabaseConnection.getConnection()) {
            while (true) {
                String sql = "SELECT COUNT(*) FROM employee WHERE id_employee = ?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, newId);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) break;
                    newId = baseId + suffix;
                    suffix++;
                    rs.close();
                }
            }
        }

        return newId;
    }

    public NhanVien(String id_Nhan_Vien_PK, String HoTen, String ChucVu, String MatKhau, String bankName, String numAccount) {
        this.id_Nhan_Vien_PK = id_Nhan_Vien_PK;
        this.HoTen = HoTen;
        this.ChucVu = ChucVu;
        this.MatKhau = MatKhau;
        this.bankName = bankName;
        this.numAccount = numAccount;
    }

    public NhanVien(String HoTen, String ChucVu, String MatKhau, String bankName, String numAccount) {
        try {
            this.id_Nhan_Vien_PK = createID(HoTen);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.HoTen = HoTen;
        this.ChucVu = ChucVu;
        this.MatKhau = MatKhau;
        this.numAccount = numAccount;
        this.bankName = bankName;
    }

    public void login() {
        this.loginTime = LocalDateTime.now();
    }

    public void logout() {
        this.logoutTime = LocalDateTime.now();
    }

    public double calculateWorkHours() {
        if (loginTime == null || logoutTime == null) return 0;
        return java.time.Duration.between(loginTime, logoutTime).toMinutes() / 60.0;
    }

    public boolean isAdmin() {
        return "Manager".equals(this.ChucVu);
    }

    public String getid_Nhan_Vien_PK() {
        return id_Nhan_Vien_PK;
    }

    public String getHoTen() {
        return HoTen;
    }

    public String getChucVu() {
        return ChucVu;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String MatKhau) {
        this.MatKhau = MatKhau;
        String sql = "UPDATE employee SET password = ? WHERE id_employee = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, MatKhau);
            stmt.setString(2, id_Nhan_Vien_PK);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getNumAccount() {
        return numAccount;
    }
    public String getBankName() {
        return bankName;
    }
}
