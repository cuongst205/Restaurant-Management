package com.example.btljavafx.model;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class NhanVien {
    private int id_Nhan_Vien_PK;
    private String HoTen;
    private String ChucVu;
    private String MatKhau;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    public NhanVien(int id_Nhan_Vien_PK, String HoTen, String ChucVu, String MatKhau) {
        this.id_Nhan_Vien_PK = id_Nhan_Vien_PK;
        this.HoTen = HoTen;
        this.ChucVu = ChucVu;
        this.MatKhau = MatKhau;
    }

    public NhanVien(String HoTen, String ChucVu, String MatKhau) {
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

    public int getid_Nhan_Vien_PK() { return id_Nhan_Vien_PK; }
    public String getHoTen() { return HoTen; }
    public String getChucVu() { return ChucVu; }
    public String getMatKhau() { return MatKhau; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public LocalDateTime getLogoutTime() { return logoutTime; }

    public void setid_Nhan_Vien_PK(int id_Nhan_Vien_PK) { this.id_Nhan_Vien_PK = id_Nhan_Vien_PK; }
    public void setHoTen(String HoTen) { this.HoTen = HoTen; }
    public void setChucVu(String ChucVu) { this.ChucVu = ChucVu; }
    public void setMatKhau(String MatKhau) { this.MatKhau = MatKhau; }
}
