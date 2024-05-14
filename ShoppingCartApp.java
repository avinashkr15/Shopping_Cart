
package agami;


import java.sql.*;
import java.util.Scanner;

public class ShoppingCartApp {
  
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/agami";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Avinash";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            createTablesIfNotExists(conn);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Wel Come To Shoping Cart");

            while (true) {
                System.out.println("1. Add Product");
                System.out.println("2. Buy Product");
                System.out.println("3. View Cart");
                System.out.println("4. Checkout");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        addProduct(conn, scanner);
                        break;
                    case 2:
                        buyProduct(conn, scanner);
                        break;
                    case 3:
                        viewCart(conn);
                        break;
                    case 4:
                        checkout(conn);
                        break;
                    case 5:
                        conn.close();
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExists(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), price DOUBLE, quantity INT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS orders (id INT AUTO_INCREMENT PRIMARY KEY, product_id INT, quantity INT)");
        }
    }

    private static void addProduct(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter product name: ");
        String name = scanner.next();
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter product quantity: ");
        int quantity = scanner.nextInt();

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
            System.out.println("Product added successfully.");
        }
    }

    private static void buyProduct(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter product ID: ");
        int productId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE id = " + productId);
            if (rs.next()) {
                int availableQuantity = rs.getInt("quantity");
                if (availableQuantity >= quantity) {
                    try (PreparedStatement buyStmt = conn.prepareStatement("INSERT INTO orders (product_id, quantity) VALUES (?, ?)")) {
                        buyStmt.setInt(1, productId);
                        buyStmt.setInt(2, quantity);
                        buyStmt.executeUpdate();
                        System.out.println("Product bought successfully.");
                    }
                } else {
                    System.out.println("Insufficient quantity available.");
                }
            } else {
                System.out.println("Product not found.");
            }
        }
    }

    private static void viewCart(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT p.name, p.price, o.quantity FROM products p JOIN orders o ON p.id = o.product_id");
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                System.out.println(name + " Price " + price + "  " + quantity + "   Total_Price"+"="+price*quantity);
               
               
            }
        }
    }

    private static void checkout(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM orders");
            System.out.println("Checkout successful.");
        }
    }
}
