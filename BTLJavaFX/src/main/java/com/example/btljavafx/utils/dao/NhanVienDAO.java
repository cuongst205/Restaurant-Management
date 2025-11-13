package com.example.btljavafx.utils.dao;

import com.example.btljavafx.model.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.sql.*;
import java.util.*;
import java.util.Optional;

public class NhanVienDAO extends GenericDAO<NhanVien> {

    @Override
    public void insert(NhanVien e) {
        String sql = "INSERT INTO employee VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getid_Nhan_Vien_PK());
            stmt.setString(2, e.getHoTen());
            stmt.setString(3, e.getChucVu());
            stmt.setString(4, e.getMatKhau());
            stmt.setString(5, e.getBankName());
            stmt.setString(6, e.getNumAccount());
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean update(NhanVien e) {
        String sql = "UPDATE employee SET name=?, role=?, password=? WHERE id_employee=?";
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
    public void delete(String id_employee) {
        String sql = "DELETE FROM employee WHERE id_employee=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id_employee);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ObservableList<NhanVien> getAll() {
        ObservableList<NhanVien> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employee";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new NhanVien(
                        rs.getString("id_employee"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("password"),
                        rs.getString("bank_name"),
                        rs.getString("num_account")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public NhanVien getById(String id) {
        String sql = "SELECT * FROM employee WHERE id_employee=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new NhanVien(
                        rs.getString("id_employee"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("password"),
                        rs.getString("bank_name"),
                        rs.getString("num_account")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Optional<NhanVien> findByCredentials(String username, String password) {
        String sql = "SELECT * FROM employee WHERE id_employee =? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new NhanVien(
                        rs.getString("id_employee"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("password"),
                        rs.getString("bank_name"),
                        rs.getString("num_account")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
    public void writeTimeLogin(String id_employee) {
        String sql = "INSERT INTO time_sheet (id_employee) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id_employee);
//            stmt.setString(2, time);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public Map<String, Pair<String, Double>> exportTimeSheet() {
        Map<String, Pair<String, Double>> map = new HashMap<>();
        String sql = """
                SELECT
                    t.id_employee,
                    COUNT(DISTINCT strftime('%Y-%m-%d', t.time_login)) AS workdays_count
                FROM time_sheet AS t join employee AS e ON e.id_employee = t.id_employee
                WHERE strftime('%Y', t.time_login) = strftime('%Y', 'now')
                  AND strftime('%m', t.time_login) = strftime('%m', 'now')
                 and e.role = 'Staff'
                GROUP BY t.id_employee;                
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id_employee");
                double count = rs.getDouble("workdays_count");
                String name = getById(id).getHoTen();
                map.put(id, new Pair<String, Double>(name, count));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }
}
