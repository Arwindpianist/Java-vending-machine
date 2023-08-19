package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String customerName;
    private String drinkName;
    private int quantity;
    private double totalAmount;

    public Order(String customerName, String drinkName, int quantity, double totalAmount) {
        this.customerName = customerName;
        this.drinkName = drinkName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Getters and setters for customerName, drinkName, quantity, and totalAmount

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Load orders from the text file
    public static List<Order> loadOrders(String filePath) {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String customerName = parts[0];
                    String drinkName = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    double totalAmount = Double.parseDouble(parts[3]);
                    Order order = new Order(customerName, drinkName, quantity, totalAmount);
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading orders file: " + e.getMessage());
        }
        return orders;
    }

    // Save orders to the text file
    public static void saveOrders(List<Order> orders, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Order order : orders) {
                bw.write(order.getCustomerName() + "," + order.getDrinkName() + ","
                        + order.getQuantity() + "," + order.getTotalAmount());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving orders file: " + e.getMessage());
        }
    }
}
