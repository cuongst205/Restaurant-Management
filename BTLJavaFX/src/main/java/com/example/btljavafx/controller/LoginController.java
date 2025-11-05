package com.example.btljavafx.controller;

import com.example.btljavafx.app.Main;
import com.example.btljavafx.utils.ActivityLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.btljavafx.model.NhanVien;
import com.example.btljavafx.utils.dao.NhanVienDAO;

import java.util.*;
import java.util.Date;
import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final NhanVienDAO NhanVienDAO = new NhanVienDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();
        Optional<NhanVien> maybe = NhanVienDAO.findByCredentials(user, pass);
        if (maybe.isPresent()) {
            NhanVien emp = maybe.get();
            emp.login();
            statusLabel.setText("Xin chào, " + emp.getHoTen());
            ActivityLogger.writeLog(emp.getHoTen(),  "Đăng nhập.");

            try {
                if (emp.isAdmin()) {
                    AdminController.setCurrentAdmin(emp.getHoTen());
                    Main.switchScene("/com/example/btljavafx/view/AdminView.fxml");
                } else {
                    Main.switchScene("/com/example/btljavafx/view/food.fxml");
                    NhanVienDAO.writeTimeLogin(emp.getid_Nhan_Vien_PK());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Tài khoản không tồn tại!");
        }
    }
}
