package com.example.btljavafx.controller;

import com.example.btljavafx.app.Main;
import com.example.btljavafx.model.*;
import com.example.btljavafx.utils.ActivityLogger;
import com.example.btljavafx.utils.dao.*;
import java.util.ArrayList;
import java.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.*;
import java.io.*;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

public class AdminController {

    private final NhanVienDAO NhanVienDAO = new NhanVienDAO();
    private final BankDAO bankDAO = new BankDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();

    @FXML private TableView<NhanVien> tvNhanVien;
//    @FXML private TableView<Payment> stkNH;
    @FXML private TableView<LogEntry> tableLogs;
    @FXML private TableColumn<LogEntry, String> colTime;
    @FXML private TableColumn<LogEntry, String> colUser;
    @FXML private TableColumn<LogEntry, String> colAction;

    @FXML private TextField txtAccountNumber;
    @FXML private TextField txtAccountName;
    @FXML private ComboBox<String> cbBank;
    @FXML private TextField wage;

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

        if (cbBank != null) {
            ObservableList<String> banks = FXCollections.observableArrayList();
            for (String[] row : bankDAO.getAll()) {
                banks.add(row[0]);
            }
            cbBank.setItems(banks);

            String savedBank = settingsDAO.get("PAYMENT_BANK");
            if (savedBank != null && !savedBank.isEmpty()) {
                cbBank.getSelectionModel().select(savedBank);
            }
        }

        if (txtAccountNumber != null) {
            String savedAcc = settingsDAO.get("PAYMENT_ACCOUNT");
            if (savedAcc != null) txtAccountNumber.setText(savedAcc);
        }
        if (txtAccountName != null) {
            String savedName = settingsDAO.get("PAYMENT_ACCOUNT_NAME");
            if (savedName != null) txtAccountName.setText(savedName);
        }
        if (wage != null) {
            String savedWage = settingsDAO.get("PAYMENT_WAGE");
            if (savedWage != null) wage.setText(savedWage);
        }
        txtAccountNumber.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
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

    @FXML
    public void handleSavePayment() {
        if (txtAccountNumber == null || cbBank == null) return;
        String acc = txtAccountNumber.getText() == null ? "" : txtAccountNumber.getText().trim();
        String name = txtAccountName == null || txtAccountName.getText() == null ? "" : txtAccountName.getText().trim();
        String bank = cbBank.getSelectionModel() == null ? "" : String.valueOf(cbBank.getSelectionModel().getSelectedItem());

        if (acc.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập số tài khoản").showAndWait();
            return;
        }
        if (bank == null || bank.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ngân hàng").showAndWait();
            return;
        }

        settingsDAO.set("PAYMENT_ACCOUNT", acc);
        settingsDAO.set("PAYMENT_BANK", bank);
        settingsDAO.set("PAYMENT_WAGE", wage.getText() == null ? "" : wage.getText().trim());
        settingsDAO.set("PAYMENT_ACCOUNT_NAME", name);

        ActivityLogger.writeLog(currentAdminName,
                "Thiết lập thanh toán: " + acc + " - " + bank + (name.isEmpty() ? "" : (" (" + name + ")")));
        new Alert(Alert.AlertType.INFORMATION, "Đã lưu thiết lập thanh toán").showAndWait();
    }

    private void initNhanVien() {
        DSNhanVien = NhanVienDAO.getAll();
        tvNhanVien.getColumns().clear();
//        stkNH.getColumns().clear();
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
        BankDAO bankDAO = new BankDAO();
        List<String> bank = bankDAO.getBankNames();
//        cbBank.addAll(bankDAO.getBankNames());
        dlg.setTitle("Thêm nhân viên");

        TextField tfName = new TextField();
        TextField numAccount = new TextField();
        PasswordField tfPass = new PasswordField();
        ComboBox<String> cbRole = new ComboBox<>(FXCollections.observableArrayList("Staff", "Manager"));
        cbRole.getSelectionModel().select(0);
        ComboBox<String> cbBank = new ComboBox<>(FXCollections.observableArrayList(bank));
        cbBank.getSelectionModel().select(0);
        numAccount.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Họ tên:"), tfName);
        grid.addRow(1, new Label("Mật khẩu:"), tfPass);
        grid.addRow(2, new Label("Chức vụ:"), cbRole);
        grid.addRow(3, new Label("Ngân hàng:"), cbBank);
        grid.addRow(4, new Label("Số tài khoản:"), numAccount);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String hoTen = tfName.getText().trim();
                if (hoTen.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Vui lòng nhập Họ Tên").showAndWait();
                    return null;
                };
                String pass = tfPass.getText().trim();
                if (tfPass.getText().trim().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Vui lòng nhập mật khẩu nhân viên").showAndWait();
                    return null;
                };
                String role = cbRole.getValue();
                String bankName = cbBank.getValue();
                String accountNumber = numAccount.getText().trim();
                return new NhanVien(hoTen, role, pass, bankName, accountNumber);
            }
            return null;
        });

        dlg.showAndWait().ifPresent(emp -> {
            NhanVienDAO.insert(emp);
            DSNhanVien.add(emp);
            ActivityLogger.writeLog(currentAdminName,
                    "Thêm nhân viên: " + emp.getHoTen() +
                            " (" + emp.getChucVu() + "), ID = " + emp.getid_Nhan_Vien_PK());
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
        if (emp.getChucVu().equalsIgnoreCase("Manager")) {
            long soManager = DSNhanVien.stream()
                    .filter(e -> e.getChucVu().equalsIgnoreCase("Manager"))
                    .count();

            if (soManager == 1) {
                new Alert(Alert.AlertType.WARNING, "Đây là Admin cuối cùng, không thể xoá!").showAndWait();
                return;
            }
        }

        DSNhanVien.remove(emp);
        NhanVienDAO.delete(emp.getid_Nhan_Vien_PK());


        ActivityLogger.writeLog(currentAdminName, "Xóa nhân viên: " + emp.getHoTen());
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
//    public static String getAbsPath = null;
    @FXML
    public void exportReport(javafx.event.ActionEvent event) {
        try {
            SettingsDAO settingsDAO = new SettingsDAO();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn nơi lưu báo cáo");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.xlsx")
            );
            String fileName = String.format("report_thang_%d-%d.xlsx", Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
            fileChooser.setInitialFileName(fileName);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                System.out.println("Người dùng chọn lưu tại: " + file.getAbsolutePath());
//                getAbsPath = file.getAbsolutePath();
                settingsDAO.set("REPORT_PATH", file.getAbsolutePath());
                Map<String, Pair<String, Double>> emp = NhanVienDAO.exportTimeSheet();
                ExportExcel exportExcel = new ExportExcel(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1), emp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




}
