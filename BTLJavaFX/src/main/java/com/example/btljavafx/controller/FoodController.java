package com.example.btljavafx.controller;

import com.example.btljavafx.model.Food;
import com.example.btljavafx.utils.dao.FoodDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FoodController {

    @FXML
    private TableView<Food> tableFood;

    @FXML
    private TableColumn<Food, Integer> colId;

    @FXML
    private TableColumn<Food, String> colName;

    @FXML
    private TableColumn<Food, Double> colPrice;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPrice;

    private FoodDAO foodDAO;
    private ObservableList<Food> foodList;

    @FXML
    public void initialize() {
        foodDAO = new FoodDAO();
        foodList = FXCollections.observableArrayList(foodDAO.getAllFood());
        tableFood.setItems(foodList);

        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
    }

    @FXML
    public void addFood() {
        String name = txtName.getText();
        double price;
        try {
            price = Double.parseDouble(txtPrice.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Price phải là số!", ButtonType.OK);
            alert.showAndWait();
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
}
