package com.example.btljavafx.model;
import java.io.*;
import java.util.*;

public class Bill {
    String pathDefault = "src/main/resources/com/example/btljavafx/view/assets/bills/bill.txt";
    List<OrderItem> items = new ArrayList<>();
    double totalPrice;
    public void setPath(String path) {
        this.pathDefault = path;
    }
    public String getPath() {
        return pathDefault;
    }
    public void setItems(List<OrderItem> items, double totalPrice) {
        for (OrderItem item : items) {
            this.items.add(item);
        }
        this.totalPrice = totalPrice;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public void printBill() {
        try {
            File file = new File(pathDefault);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter(file);
            for (OrderItem item : items) {
                writer.println(item.getFood().getName() + " - " + item.getQuantity() + " x " + item.getFood().getPrice() + " = " + item.getTotalPrice());
            }
            writer.println("-------------------------------");
            writer.println("Total price: " + totalPrice);
            writer.println("VAT 10%: " + 0.1 * totalPrice);
            writer.println("-------------------------------");
            writer.println("Total bill: " + (totalPrice + 0.1 * totalPrice));
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
