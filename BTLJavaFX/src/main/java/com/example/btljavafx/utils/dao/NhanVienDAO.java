package com.example.btljavafx.utils.dao;

import com.example.btljavafx.model.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Optional;

public class NhanVienDAO extends GenericDAO<NhanVien> {

    @Override
    public void insert(NhanVien e) {
        String sql = "INSERT INTO \"Nhân Viên\" VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getid_Nhan_Vien_PK());
            stmt.setString(2, e.getHoTen());
            stmt.setString(3, e.getChucVu());
            stmt.setString(4, e.getMatKhau());
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean update(NhanVien e) {
        String sql = "UPDATE \"Nhân Viên\" SET \"Họ tên\"=?, \"Chức vụ\"=?, \"Mật khẩu\"=? WHERE id_Nhan_Vien_PK=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, e.getHoTen());
            stmt.setString(2, e.getChucVu());
            stmt.setString(3, e.getMatKhau());
            stmt.setString(4, e.getid_Nhan_Vien_PK());
            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(String id_Nhan_Vien_PK) {
        String sql = "DELETE FROM \"Nhân Viên\" WHERE id_Nhan_Vien_PK=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id_Nhan_Vien_PK);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ObservableList<NhanVien> getAll() {
        ObservableList<NhanVien> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM \"Nhân Viên\"";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new NhanVien(
                        rs.getString("id_Nhan_Vien_PK"),
                        rs.getString("Họ tên"),
                        rs.getString("Chức vụ"),
                        rs.getString("Mật khẩu")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public NhanVien getById(String id) {
        String sql = "SELECT * FROM \"Nhân Viên\" WHERE id_Nhan_Vien_PK=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new NhanVien(
                        rs.getString("id_Nhan_Vien_PK"),
                        rs.getString("Họ tên"),
                        rs.getString("Chức vụ"),
                        rs.getString("Mật khẩu")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Optional<NhanVien> findByCredentials(String username, String password) {
        String sql = "SELECT * FROM \"Nhân Viên\" WHERE \"id_Nhan_Vien_PK\"=? AND \"Mật khẩu\"=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new NhanVien(
                        rs.getString("id_Nhan_Vien_PK"),
                        rs.getString("Họ tên"),
                        rs.getString("Chức vụ"),
                        rs.getString("Mật khẩu")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
