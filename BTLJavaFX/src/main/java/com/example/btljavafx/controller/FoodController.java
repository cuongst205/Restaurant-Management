package com.example.btljavafx.controller;

import com.example.btljavafx.model.Food;
import com.example.btljavafx.model.OrderItem;
import com.example.btljavafx.utils.dao.FoodDAO;
import com.example.btljavafx.utils.dao.OrderDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    private FoodDAO foodDAO;
    private OrderDAO orderDAO;
    private ObservableList<Food> foodList;
    private ObservableList<OrderItem> orderList;

    @FXML
    public void initialize() {
        foodDAO = new FoodDAO();
        orderDAO = new OrderDAO();

        foodList = FXCollections.observableArrayList(foodDAO.getAllFood());
        tableFood.setItems(foodList);

        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());

        orderList = FXCollections.observableArrayList();
        tableOrder.setItems(orderList);

        colOrderName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFood().getName()));
        colOrderPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getFood().getPrice()).asObject());
        colOrderQuantity.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        colOrderTotal.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalPrice()).asObject());

        updateTotal();
    }

    @FXML
    public void addFood() {
        String name = txtName.getText();
        double price;
        try { price = Double.parseDouble(txtPrice.getText()); }
        catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Price phải là số!").showAndWait();
            return;
        }

        Food f = new Food(0, name, price);
        foodDAO.addFood(f);
        foodList.setAll(foodDAO.getAllFood());
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
        Food selectedFood = tableFood.getSelectionModel().getSelectedItem();
        if (selectedFood == null) return;

        OrderItem existing = null;
        for (OrderItem item : orderList) {
            if (item.getFood().getId() == selectedFood.getId()) {
                existing = item;
                break;
            }
        }

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            orderList.add(new OrderItem(selectedFood, 1));
        }

        tableOrder.refresh();
        updateTotal();
    }

    @FXML
    public void saveOrder() {
        if (orderList.isEmpty()) return;
        orderDAO.saveOrder(orderList);
        orderList.clear();
        tableOrder.refresh();
        updateTotal();
    }

    private void updateTotal() {
        double total = orderList.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        lblTotal.setText(String.valueOf(total));
    }
}
