package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VendingMachineApp {
    private static final String STAFF_FILE_PATH = "staff.txt";
    private static final String DRINKS_FILE_PATH = "drinks.txt";

    private static final String ORDERS_FILE_PATH = "orders.txt";

    public static void main(String[] args) {
        // Check if staff.txt exists, and create it if not
        File staffFile = new File(STAFF_FILE_PATH);
        if (!staffFile.exists()) {
            try {
                staffFile.createNewFile();
                System.out.println("staff.txt created successfully.");
            } catch (IOException e) {
                System.out.println("Error creating staff.txt: " + e.getMessage());
            }
        }

        // Check if drinks.txt exists, and create it if not
        File drinksFile = new File(DRINKS_FILE_PATH);
        if (!drinksFile.exists()) {
            try {
                drinksFile.createNewFile();
                System.out.println("drinks.txt created successfully.");
            } catch (IOException e) {
                System.out.println("Error creating drinks.txt: " + e.getMessage());
            }
        }

        File ordersFile = new File(ORDERS_FILE_PATH);
        if (!ordersFile.exists()) {
            try {
                ordersFile.createNewFile();
                System.out.println("orders.txt created successfully.");
            } catch (IOException e) {
                System.out.println("Error creating orders.txt: " + e.getMessage());
            }
        }

        List<Staff> staffAccounts = loadStaffAccounts(STAFF_FILE_PATH);
        List<Drink> drinks = loadDrinks(DRINKS_FILE_PATH);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Vending Machine!");
        boolean loggedIn = false;
        Staff loggedInStaff = null;

        while (!loggedIn) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            for (Staff staff : staffAccounts) {
                if (staff.getUsername().equals(username) && staff.getPassword().equals(password)) {
                    loggedIn = true;
                    loggedInStaff = staff;
                    System.out.println("Login successful!");
                    break;
                }
            }

            if (!loggedIn) {
                System.out.println("Invalid username or password. Please try again.");
                System.out.print("Do you want to create a new account? (y/n): ");
                String createAccountChoice = scanner.nextLine();

                if (createAccountChoice.equalsIgnoreCase("y")) {
                    System.out.print("Enter your desired username: ");
                    String newUsername = scanner.nextLine();

                    System.out.print("Enter your desired password: ");
                    String newPassword = scanner.nextLine();

                    Staff newStaff = new Staff(newUsername, newPassword);
                    staffAccounts.add(newStaff);
                    System.out.println("Account created successfully!");
                }
            }
        }

        boolean isStaff = false;
        boolean isCustomer = false;

        // Prompt for staff or customer login
        System.out.print("Are you a staff or customer? (staff/customer): ");
        String loginChoice = scanner.nextLine();

        if (loginChoice.equalsIgnoreCase("staff")) {
            // Staff login
            isStaff = true;
            if (loggedInStaff.isAdmin()) {
                // Admin menu
                while (isStaff) {
                    System.out.println("\n===== Admin Menu =====");
                    System.out.println("1. View Drinks");
                    System.out.println("2. Add Drink");
                    System.out.println("3. Modify Drink");
                    System.out.println("4. View All Orders");
                    System.out.println("5. Generate Reports");
                    System.out.println("6. Logout");

                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    switch (choice) {
                        case 1:
                            viewDrinks(drinks);
                            break;
                        case 2:
                            addDrink(drinks, scanner);
                            break;
                        case 3:
                            modifyDrink(drinks, scanner);
                            break;
                        case 4:
                            viewAllOrders();
                            break;
                        case 5:
                            generateReports(drinks);
                            break;
                        case 6:
                            isStaff = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } else {
                // Staff menu
                while (isStaff) {
                    System.out.println("\n===== Staff Menu =====");
                    System.out.println("1. View Drinks");
                    System.out.println("2. View All Orders");
                    System.out.println("3. Logout");

                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    switch (choice) {
                        case 1:
                            viewDrinks(drinks);
                            break;
                        case 2:
                            viewAllOrders();
                            break;
                        case 3:
                            isStaff = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        } else if (loginChoice.equalsIgnoreCase("customer")) {
            // Customer login
            isCustomer = true;

            System.out.print("Enter your name as a customer: ");
            String customerName = scanner.nextLine();
            Customer customer = new Customer(customerName);
            System.out.println("Welcome, " + customer.getName() + "!");

            // Customer menu
            while (isCustomer) {
                System.out.println("\n===== Customer Menu =====");
                System.out.println("1. View Drinks");
                System.out.println("2. Buy Drink");
                System.out.println("3. Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        viewDrinks(drinks);
                        break;
                    case 2:
                        buyDrink(drinks, customer, scanner);
                        break;
                    case 3:
                        isCustomer = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } else {
            System.out.println("Invalid choice. Please try again.");
        }
        saveStaffAccounts(staffAccounts, STAFF_FILE_PATH);
        saveDrinks(drinks, DRINKS_FILE_PATH);

        System.out.println("Logged out. Goodbye!");
    }

    // Helper methods for staff actions
    // Load staff accounts from the text file
    private static List<Order> loadOrders(String filePath) {
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

    private static void saveOrders(List<Order> orders, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Order order : orders) {
                bw.write(order.getCustomerName() + "," + order.getDrinkName() + "," + order.getQuantity() + "," + order.getTotalAmount());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving orders file: " + e.getMessage());
        }
    }

    private static List<Staff> loadStaffAccounts(String filePath) {
        List<Staff> staffAccounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    boolean isAdmin = Boolean.parseBoolean(parts[2]);
                    Staff staff = new Staff(username, password, isAdmin);
                    staffAccounts.add(staff);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading staff file: " + e.getMessage());
        }
        return staffAccounts;
    }

    // Save staff accounts to the text file
    private static void saveStaffAccounts(List<Staff> staffAccounts, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Staff staff : staffAccounts) {
                bw.write(staff.getUsername() + "," + staff.getPassword() + "," + staff.isAdmin());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving staff file: " + e.getMessage());
        }
    }

    // Load drinks from the text file
    private static List<Drink> loadDrinks(String filePath) {
        List<Drink> drinks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    Drink drink = new Drink(name, price, quantity);
                    drinks.add(drink);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading drinks file: " + e.getMessage());
        }
        return drinks;
    }

    // Save drinks to the text file
    private static void saveDrinks(List<Drink> drinks, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Drink drink : drinks) {
                bw.write(drink.getName() + "," + drink.getPrice() + "," + drink.getQuantity());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving drinks file: " + e.getMessage());
        }
    }

    private static void addDrink(List<Drink> drinks, Scanner scanner) {
        System.out.print("Enter the name of the drink: ");
        String name = scanner.nextLine();

        System.out.print("Enter the price of the drink: ");
        double price = scanner.nextDouble();

        System.out.print("Enter the quantity of the drink: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Drink newDrink = new Drink(name, price, quantity);
        drinks.add(newDrink);
        System.out.println("Drink added successfully!");
    }

    private static void modifyDrink(List<Drink> drinks, Scanner scanner) {
        System.out.println("===== Available Drinks =====");
        for (int i = 0; i < drinks.size(); i++) {
            System.out.println((i + 1) + ". " + drinks.get(i).getName());
        }

        System.out.print("Enter the number of the drink to modify: ");
        int drinkNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (drinkNumber >= 1 && drinkNumber <= drinks.size()) {
            Drink selectedDrink = drinks.get(drinkNumber - 1);

            System.out.print("Enter the new name of the drink: ");
            String newName = scanner.nextLine();

            System.out.print("Enter the new price of the drink: ");
            double newPrice = scanner.nextDouble();

            System.out.print("Enter the new quantity of the drink: ");
            int newQuantity = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            selectedDrink.setName(newName);
            selectedDrink.setPrice(newPrice);
            selectedDrink.setQuantity(newQuantity);
            System.out.println("Drink modified successfully!");
        } else {
            System.out.println("Invalid drink number. Please try again.");
        }
    }

    private static void viewDrinks(List<Drink> drinks) {
        System.out.println("===== Available Drinks =====");
        for (Drink drink : drinks) {
            System.out.println("Name: " + drink.getName());
            System.out.println("Price: " + drink.getPrice());
            System.out.println("Quantity: " + drink.getQuantity());
            System.out.println("-------------------------");
        }
    }

    private static void viewAllOrders() {
        List<Order> orders = loadOrders(ORDERS_FILE_PATH);

        if (orders.isEmpty()) {
            System.out.println("No orders available.");
        } else {
            System.out.println("===== All Orders =====");
            for (Order order : orders) {
                System.out.println("Customer: " + order.getCustomerName());
                System.out.println("Drink: " + order.getDrinkName());
                System.out.println("Quantity: " + order.getQuantity());
                System.out.println("Total Amount: $" + order.getTotalAmount());
                System.out.println("-------------------------");
            }
        }
    }

    private static void generateReports(List<Drink> drinks) {
        List<Order> orders = loadOrders(ORDERS_FILE_PATH);

        if (orders.isEmpty()) {
            System.out.println("No orders available to generate reports.");
        } else {
            System.out.println("===== Reports =====");
            // Most Popular Drinks (top 3)
            drinks.sort((d1, d2) -> Integer.compare(d2.getQuantity(), d1.getQuantity()));
            System.out.println("Most Popular Drinks:");
            int count = 0;
            for (Drink drink : drinks) {
                if (count >= 3) {
                    break;
                }
                System.out.println(drink.getName() + " - Quantity Sold: " + drink.getQuantity());
                count++;
            }

            // Highest Amount Per Sale
            drinks.sort((d1, d2) -> Double.compare(d2.getPrice(), d1.getPrice()));
            Drink highestAmountDrink = drinks.get(0);
            System.out.println("Drink with Highest Amount Per Sale:");
            System.out.println(highestAmountDrink.getName() + " - Price: $" + highestAmountDrink.getPrice());

            // Other useful reports can be added here.

            System.out.println("-------------------------");
        }
    }

    private static void buyDrink(List<Drink> drinks, Customer customer, Scanner scanner) {
        boolean buying = true;
        double totalAmount = 0.0;
        List<String> selectedDrinkNames = new ArrayList<>();
        List<Order> orders = loadOrders(ORDERS_FILE_PATH); // Load existing orders

        while (buying) {
            System.out.println("===== Available Drinks =====");
            for (int i = 0; i < drinks.size(); i++) {
                System.out.println((i + 1) + ". " + drinks.get(i).getName());
            }

            System.out.print("Enter the number of the drink to buy (0 to finish): ");
            int drinkNumber = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (drinkNumber == 0) {
                buying = false;
                continue;
            }

            if (drinkNumber >= 1 && drinkNumber <= drinks.size()) {
                Drink selectedDrink = drinks.get(drinkNumber - 1);

                System.out.print("Enter the quantity to buy: ");
                int quantityToBuy = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                if (quantityToBuy <= 0) {
                    System.out.println("Invalid quantity. Please try again.");
                    continue;
                }

                if (selectedDrink.getQuantity() < quantityToBuy) {
                    System.out.println("Insufficient quantity available. Please try again.");
                    continue;
                }

                // Calculate the total amount and update the drink quantity
                double drinkTotalAmount = selectedDrink.getPrice() * quantityToBuy;
                totalAmount += drinkTotalAmount;
                selectedDrink.setQuantity(selectedDrink.getQuantity() - quantityToBuy);

                // Add the drink name to the selectedDrinkNames list
                for (int i = 0; i < quantityToBuy; i++) {
                    selectedDrinkNames.add(selectedDrink.getName());
                }

                // Print the selected drinks and their total amount so far
                System.out.println("\n===== Selected Drinks =====");
                for (String drinkName : selectedDrinkNames) {
                    System.out.println(drinkName);
                }
                System.out.println("Total Amount: $" + totalAmount);
                System.out.println("==========================");

                // Save the order in the orders list
                Order order = new Order(customer.getName(), selectedDrink.getName(), quantityToBuy, drinkTotalAmount);
                orders.add(order);
            } else {
                System.out.println("Invalid drink number. Please try again.");
            }
        }

        // Generate and print the receipt
        System.out.println("\n===== Receipt =====");
        System.out.println("Customer: " + customer.getName());
        for (String drinkName : selectedDrinkNames) {
            System.out.println("Drink: " + drinkName);
        }
        System.out.println("Total Amount: $" + totalAmount);
        System.out.println("====================");

        // Save the orders to the orders.txt file
        saveOrders(orders, ORDERS_FILE_PATH);
    }
}

    class Staff {
    private String username;
    private String password;
    private boolean isAdmin;

    public Staff(String username, String password) {
        this.username = username;
        this.password = password;
        this.isAdmin = false; // Set isAdmin to false by default
    }

    public Staff(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getters and setters for username, password, and isAdmin

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}


class Drink {
    private String name;
    private double price;
    private int quantity;

    public Drink(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and setters for name, price, and quantity

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
