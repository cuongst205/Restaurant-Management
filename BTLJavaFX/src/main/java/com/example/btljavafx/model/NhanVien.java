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
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    private String createID(String s) throws SQLException {
        if (s == null) {
            return "";
        }

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

        String res = parts[parts.length - 1];
        for (int i = 1; i < parts.length; i++) res += parts[i].charAt(0);
        res = res.trim();

        int suffix = 1;
        Connection connection = DatabaseConnection.getConnection();
        String sql = "SELECT COUNT(*) FROM \"Nhân Viên\" WHERE id_Nhan_Vien_PK = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs;
        while (true) {
            ps.setString(1, res);
            rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) break; // chưa tồn tại
            res = res + suffix; // thêm hậu tố số
            suffix++;
        }
        rs.close();
        ps.close();

        return res;
    }

    public NhanVien(String id_Nhan_Vien_PK, String HoTen, String ChucVu, String MatKhau) {
        this.id_Nhan_Vien_PK = id_Nhan_Vien_PK;
        this.HoTen = HoTen;
        this.ChucVu = ChucVu;
        this.MatKhau = MatKhau;
    }

    public NhanVien(String HoTen, String ChucVu, String MatKhau) {
        try {
            this.id_Nhan_Vien_PK = createID(HoTen);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.HoTen = HoTen;
        this.ChucVu = ChucVu;
        this.MatKhau = MatKhau;
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

    public String getid_Nhan_Vien_PK() { return id_Nhan_Vien_PK; }
    public String getHoTen() { return HoTen; }
    public String getChucVu() { return ChucVu; }
    public String getMatKhau() { return MatKhau; }

    public void setMatKhau(String MatKhau) { this.MatKhau = MatKhau; }
}
