package com.example.btljavafx.controller;

import com.example.btljavafx.app.Main;
import com.example.btljavafx.model.*;
import com.example.btljavafx.utils.ActivityLogger;
import com.example.btljavafx.utils.dao.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;

public class AdminController {

    private final NhanVienDAO NhanVienDAO = new NhanVienDAO();
    @FXML private TableView<NhanVien> tvNhanVien;
    @FXML private TableView<LogEntry> tableLogs;
    @FXML private TableColumn<LogEntry, String> colTime;
    @FXML private TableColumn<LogEntry, String> colUser;
    @FXML private TableColumn<LogEntry, String> colAction;

    private static final String LOG_FILE = "log.xlsx";
    private static String currentAdminName;

    public static void setCurrentAdmin(String name) {
        currentAdminName = name;
    }

    private ObservableList<NhanVien> DSNhanVien;

    @FXML
    public void initialize() {
        initNhanVien();

        colTime.setCellValueFactory(data -> data.getValue().timeProperty());
        colUser.setCellValueFactory(data -> data.getValue().userProperty());
        colAction.setCellValueFactory(data -> data.getValue().actionProperty());
        loadLogs();
    }

    @FXML
    private void handleRefreshLogs() {
        ActivityLogger.writeLog(currentAdminName, "Làm mới nhật ký hoạt động");
        loadLogs();
    }

    private void loadLogs() {
        ObservableList<LogEntry> logs = FXCollections.observableArrayList();
        try {
            Path path = Paths.get(LOG_FILE);
            if (!Files.exists(path)) {
                tableLogs.setItems(logs);
                return;
            }
            try (InputStream in = Files.newInputStream(path);
                 Workbook workbook = new XSSFWorkbook(in)) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    String time = row.getCell(0).getStringCellValue();
                    String user = row.getCell(1).getStringCellValue();
                    String action = row.getCell(2).getStringCellValue();
                    logs.add(new LogEntry(time, user, action));
                }
            }
            tableLogs.setItems(logs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogs() {
        loadLogs();
    }

    private void initNhanVien() {
        DSNhanVien = NhanVienDAO.getAll();
        tvNhanVien.getColumns().clear();

        TableColumn<NhanVien, String> IDCol = new TableColumn<>("ID");
        IDCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getid_Nhan_Vien_PK()));
        IDCol.setPrefWidth(100);

        TableColumn<NhanVien, String> nameCol = new TableColumn<>("Họ tên");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoTen()));
        nameCol.setPrefWidth(200);

        TableColumn<NhanVien, String> roleCol = new TableColumn<>("Chức vụ");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getChucVu()));

        tvNhanVien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tvNhanVien.getColumns().addAll(IDCol, nameCol, roleCol);
        tvNhanVien.setItems(DSNhanVien);
    }

    @FXML
    public void handleAddEmployee() {
        Dialog<NhanVien> dlg = new Dialog<>();
        dlg.setTitle("Thêm nhân viên");

        TextField tfName = new TextField();
        TextField tfUsername = new TextField();
        PasswordField tfPass = new PasswordField();
        ComboBox<String> cbRole = new ComboBox<>(FXCollections.observableArrayList("Staff", "Manager"));
        cbRole.getSelectionModel().select(0);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(1, new Label("Tên:"), tfName);
        grid.addRow(3, new Label("Mật khẩu:"), tfPass);
        grid.addRow(4, new Label("Chức vụ:"), cbRole);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new NhanVien(tfName.getText(), cbRole.getValue(), tfPass.getText());
            }
            return null;
        });

        dlg.showAndWait().ifPresent(emp -> {
            NhanVienDAO.insert(emp);
            DSNhanVien.add(emp);
            ActivityLogger.writeLog(currentAdminName, "Thêm nhân viên: " + emp.getHoTen() + " (" + emp.getChucVu() + ")");
        });
    }

    @FXML
    public void handleEditEmployee() {
        NhanVien e = tvNhanVien.getSelectionModel().getSelectedItem();
        if (e == null) return;

        TextInputDialog dlg = new TextInputDialog(e.getMatKhau());
        dlg.setHeaderText("Đổi mật khẩu cho " + e.getHoTen());
        dlg.showAndWait().ifPresent(pw -> {
            e.setMatKhau(pw);
            ActivityLogger.writeLog(currentAdminName, "Thay đổi mật khẩu cho " + e.getHoTen());
        });
    }

    @FXML
    public void handleDeleteEmployee() {
        NhanVien emp = tvNhanVien.getSelectionModel().getSelectedItem();
        if (emp == null) return;
        DSNhanVien.remove(emp);
        NhanVienDAO.delete(emp.getid_Nhan_Vien_PK());

        ActivityLogger.writeLog(currentAdminName, "Quản lý xóa nhân viên: " + emp.getHoTen());
    }

    @FXML
    public void handleLogout() {
        ActivityLogger.writeLog(currentAdminName, "Đăng xuất");
        try {
            Main.switchScene("/com/example/btljavafx/view/LoginView.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
