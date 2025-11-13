package com.example.btljavafx.controller;
import com.example.btljavafx.app.Main;
import com.example.btljavafx.model.Bill;
import com.example.btljavafx.model.Food;
import com.example.btljavafx.model.OrderItem;
import com.example.btljavafx.services.GetVietQR;
import com.example.btljavafx.utils.dao.FoodDAO;
import com.example.btljavafx.utils.dao.OrderDAO;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class FoodController {

    @FXML private TableView<Food> tableFood;
    @FXML private TableColumn<Food, Integer> colId;
    @FXML private TableColumn<Food, String> colName;
    @FXML private TableColumn<Food, Double> colPrice;

    @FXML private TableView<OrderItem> tableOrder;
    @FXML private TableColumn<OrderItem, String> colOrderName;
    @FXML private TableColumn<OrderItem, Double> colOrderPrice;
    @FXML private TableColumn<OrderItem, Integer> colOrderQuantity;
    @FXML private TableColumn<OrderItem, Double> colOrderTotal;

    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private Label lblTotal;

    @FXML private GridPane gridTables;
    @FXML private Label lblCurrentTable;
    @FXML private Button btnPay;

    private FoodDAO foodDAO;
    private OrderDAO orderDAO;
    private ObservableList<Food> foodList;

    // Quản lý order theo bàn
    private final Map<Integer, ObservableList<OrderItem>> tableOrders = new HashMap<>();
    private final Map<Integer, Button> tableButtons = new HashMap<>();
    private int currentTable = -1;

    @FXML
    public void initialize() {
        foodDAO = new FoodDAO();
        orderDAO = new OrderDAO();

        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());

        foodList = FXCollections.observableArrayList(foodDAO.getAllFood());
        tableFood.setItems(foodList);
        tableFood.setPlaceholder(new Label("Chưa có món ăn"));

        setCurrentTable(1);
        buildTableButtons(12);

        colOrderName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFood().getName()));
        colOrderPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getFood().getPrice()).asObject());
        colOrderQuantity.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        colOrderTotal.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalPrice()).asObject());

        updateTotal();
    }

    private void buildTableButtons(int tableCount) {
        gridTables.getChildren().clear();
        tableButtons.clear();
        int cols = 3;
        for (int i = 0; i < tableCount; i++) {
            int tableNo = i + 1;
            Button btn = new Button("Bàn " + tableNo);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(40);
            btn.getStyleClass().add("table-free");
            btn.setOnAction(e -> handleTableClick(tableNo));
            tableButtons.put(tableNo, btn);
            gridTables.add(btn, i % cols, i / cols);
        }
        for (int i = 1; i <= tableCount; i++) {
            tableOrders.put(i, FXCollections.observableArrayList());
        }
        refreshTableStyles();
    }

    private void handleTableClick(int tableNo) {
        setCurrentTable(tableNo);
    }

    private void setCurrentTable(int tableNo) {
        this.currentTable = tableNo;
        lblCurrentTable.setText(String.valueOf(tableNo));
        ObservableList<OrderItem> list = tableOrders.computeIfAbsent(tableNo, k -> FXCollections.observableArrayList());
        tableOrder.setItems(list);
        updateTotal();
        refreshTableStyles();
    }

    private void refreshTableStyles() {
        for (Map.Entry<Integer, Button> entry : tableButtons.entrySet()) {
            int t = entry.getKey();
            Button btn = entry.getValue();
            btn.getStyleClass().removeAll("table-occupied", "table-free", "table-current");
            boolean occupied = !tableOrders.getOrDefault(t, FXCollections.observableArrayList()).isEmpty();
            if (t == currentTable) {
                btn.getStyleClass().add("table-current");
            } else if (occupied) {
                btn.getStyleClass().add("table-occupied");
            } else {
                btn.getStyleClass().add("table-free");
            }
        }
    }

    private void addFoodToTable(int tableNo, Food food, int qty) {
        ObservableList<OrderItem> list = tableOrders.computeIfAbsent(tableNo, k -> FXCollections.observableArrayList());
        OrderItem existing = null;
        for (OrderItem item : list) {
            if (item.getFood().getId() == food.getId()) {
                existing = item;
                break;
            }
        }
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
        } else {
            list.add(new OrderItem(food, qty));
        }
        if (tableNo == currentTable) {
            tableOrder.refresh();
            updateTotal();
        }
        refreshTableStyles();
    }

    @FXML
    public void addFood() {
        String name = txtName.getText() == null ? "" : txtName.getText().trim();
        if (name.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Tên món không được để trống").showAndWait();
            return;
        }
        double price;
        try { price = Double.parseDouble(txtPrice.getText().trim()); }
        catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Giá phải là số hợp lệ!").showAndWait();
            return;
        }
        if (price < 0) {
            new Alert(Alert.AlertType.WARNING, "Giá không được âm").showAndWait();
            return;
        }

        Food f = new Food(0, name, price);
        foodDAO.addFood(f);
        foodList.setAll(foodDAO.getAllFood());
        tableFood.refresh();
        txtName.clear();
        txtPrice.clear();
    }

    @FXML
    public void deleteFood() {
        Food selected = tableFood.getSelectionModel().getSelectedItem();
        if (selected != null) {
            foodDAO.deleteFood(selected.getId());
            foodList.setAll(foodDAO.getAllFood());
        }
    }

    @FXML
    public void addToOrder() {
        if (currentTable <= 0) return;
        Food selectedFood = tableFood.getSelectionModel().getSelectedItem();
        if (selectedFood == null) return;
        addFoodToTable(currentTable, selectedFood, 1);
    }
    @FXML
    public void getQRCode() {

    }
//        @FXML
//    public void logOut() {
//        try {
//            Main.switchScene("/com/example/btljavafx/view/LoginView.fxml");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    public void saveOrder() {
        if (currentTable <= 0) return;
        ObservableList<OrderItem> list = tableOrders.getOrDefault(currentTable, FXCollections.observableArrayList());
        if (list.isEmpty()) return;

        List<OrderItem> snapshot = new ArrayList<>(list);
        double total = snapshot.stream().mapToDouble(OrderItem::getTotalPrice).sum();

        orderDAO.saveOrder(list);

        list.clear();
        tableOrder.refresh();
        updateTotal();
        refreshTableStyles();

        showPaymentPopup(snapshot, total);
    }

    private void showPaymentPopup(List<OrderItem> items, double total) {
        Stage dialog = new Stage();
        if (btnPay != null && btnPay.getScene() != null && btnPay.getScene().getWindow() != null) {
            dialog.initOwner(btnPay.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
        } else {
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
        dialog.setTitle("Thanh toán bàn");

        Label lblTitle = new Label("Quét mã để thanh toán");
        lblTitle.getStyleClass().add("subtitle");
        Label lblCountdown = new Label("180s");
        lblCountdown.setStyle("-fx-font-size: 14px; -fx-text-fill: #d9534f; -fx-font-weight: bold;");
        HBox header = new HBox(10, lblTitle, new Region(), lblCountdown);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        TableView<OrderItem> tv = new TableView<>();
        tv.setItems(FXCollections.observableArrayList(items));
        tv.setPrefHeight(450);
        TableColumn<OrderItem, String> cName = new TableColumn<>("Món");
        cName.setPrefWidth(280);
        cName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFood().getName()));
        TableColumn<OrderItem, Integer> cQty = new TableColumn<>("SL");
        cQty.setPrefWidth(50);
        cQty.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        TableColumn<OrderItem, Double> cPrice = new TableColumn<>("Giá");
        cPrice.setPrefWidth(80);
        cPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getFood().getPrice()).asObject());
        TableColumn<OrderItem, Double> cLine = new TableColumn<>("Thành tiền");
        cLine.setPrefWidth(100);
        cLine.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalPrice()).asObject());
        tv.getColumns().addAll(cName, cQty, cPrice, cLine);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        Label lblTotal = new Label("Tổng: " + total);
        lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox left = new VBox(8, tv, lblTotal);
        left.setAlignment(Pos.TOP_LEFT);

        File qrFile = GetVietQR.generate(total, "Thanh toán bàn " + currentTable);
        Node qrNode;
        int sizeOfQR = 250;
        if (qrFile != null && qrFile.exists()) {
            ImageView qrView = new ImageView(new Image(qrFile.toURI().toString()));
            qrView.setSmooth(true);
            qrView.setPreserveRatio(true);
            qrView.setFitWidth(450);
            qrView.setFitHeight(450);
            qrView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 3, 3);");
            qrNode = qrView;

        } else {
            Label qrPlaceholder = new Label("QR\n(không khả dụng)");
            qrPlaceholder.setPrefSize(sizeOfQR, sizeOfQR);
            qrPlaceholder.setMaxSize(sizeOfQR, sizeOfQR);
            qrPlaceholder.setStyle("-fx-border-color: #888; -fx-border-width: 2; -fx-background-color: white; -fx-alignment: center; -fx-text-alignment: center;");
            qrNode = qrPlaceholder;
        }
        VBox right = new VBox(qrNode);
        right.setAlignment(Pos.TOP_CENTER);

        HBox center = new HBox(16, left, right);
        center.setAlignment(Pos.TOP_LEFT);

        Button btnDone = new Button("Đã thanh toán xong");
        Bill bill = new Bill();
        bill.setItems(items, total);
        bill.printBill();


        btnDone.getStyleClass().add("primary-btn");

        VBox root = new VBox(12, header, center, btnDone);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root, 880, 620);
        dialog.setScene(scene);
        dialog.setResizable(false);

        final int[] seconds = {180};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            seconds[0]--;
            if (seconds[0] <= 0) {
                lblCountdown.setText("0s");
                dialog.close();
            } else {
                lblCountdown.setText(seconds[0] + "s");
            }
        }));
        timeline.setCycleCount(180);

        btnDone.setOnAction(e -> {
            timeline.stop();
            dialog.close();
        });

        dialog.show();
        timeline.playFromStart();
    }

    private void updateTotal() {
        ObservableList<OrderItem> list = tableOrders.getOrDefault(currentTable, FXCollections.observableArrayList());
        double total = list.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        lblTotal.setText(String.valueOf(total));
        if (btnPay != null) {
            btnPay.setDisable(list.isEmpty());
        }
    }

















}
