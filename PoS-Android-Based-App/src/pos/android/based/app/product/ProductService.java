/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.product;

/**
 *
 * @author Desi
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pos.android.based.app.DatabaseConnection;

public class ProductService {

    public static String generateProductID(Connection conn) throws SQLException {
        String lastID = "P0000";
        String query = "SELECT id FROM products ORDER BY id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                lastID = rs.getString("id");
            }
        }
        int num = Integer.parseInt(lastID.substring(1)) + 1;
        return String.format("P%04d", num);
    }

    public static boolean addNonPerishable(String name, double price, int stock) {
        return addGeneralProduct(name, price, stock, "non", null, null, null);
    }

    public static boolean addPerishable(String name, double price, int stock, LocalDate expiryDate) {
    String sql = "INSERT INTO products(id, name, price, stock, type, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.connect()) {
        String id = generateProductID(conn);
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);
        stmt.setString(2, name);
        stmt.setDouble(3, price);
        stmt.setInt(4, stock);
        stmt.setString(5, "perishable");
        stmt.setDate(6, java.sql.Date.valueOf(expiryDate));
        int result = stmt.executeUpdate();
        return result > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    public static boolean addDigital(String name, double price, int stock, URL url, String vendor) {
        return addGeneralProduct(name, price, stock, "digital", null, url.toString(), vendor);
    }

    public static boolean addBundle(String name, double price, int stock, int jumlahProdukSimulasi) {
        // Bundle tidak menyimpan list produk aktual di DB dalam versi ini
        return addGeneralProduct(name, price, stock, "bundle", null, null, null);
    }

    private static boolean addGeneralProduct(String name, double price, int stock, String type,
                                             LocalDate expiryDate, String url, String vendorName) {
        String query = "INSERT INTO products(id, name, price, stock, type, expiry_date, url, vendor_name) " +
         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String id = generateProductID(conn);

            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setDouble(3, price);
            stmt.setInt(4, stock);
            stmt.setString(5, type);
            
            stmt.setString(6, expiryDate != null ? expiryDate.toString() : null);
             stmt.setString(7, url);
             stmt.setString(8, vendorName);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
            System.out.println("Product added to database successfully.");
            } else {
            System.out.println("No rows affected, product not added.");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Add product error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    

    public static boolean updateProduct(String id, String name, double price, int stock) {
        String query = "UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, stock);
            stmt.setString(4, id);
            int rows = stmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
            return false;
        }
    }

 

    public static boolean deleteProduct(String id) {
        String query = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            int rows = stmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
            return false;
        }
    }

    public static List<Product> getAllProducts() throws MalformedURLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY id";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String type = rs.getString("type");

                 Product p = switch (type) {
                case "perishable" -> new PerishableProduct(id, name, stock, price, LocalDate.parse(rs.getString("expiry_date")));
                case "digital" -> new DigitalProduct(id, name, price, new URL(rs.getString("url")), rs.getString("vendor_name"));
                case "bundle" -> new BundleProduct(id, name, price, new ArrayList<>());
                default -> new NonPerishableProduct(id, name, price, stock);
                };

                if (p != null) products.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Get products error: " + e.getMessage());
        }
        return products;
    }

    public static Product getProductById(String id) {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String type = rs.getString("type");

                return switch (type) {
                    case "perishable" -> new PerishableProduct(id, name, stock, price,
                            LocalDate.parse(rs.getString("expiry_date")));
                    case "digital" -> new DigitalProduct(id, name, price,
                            new URL(rs.getString("url")),
                            rs.getString("vendor_name"));
                    case "bundle" -> new BundleProduct(id, name, price, new ArrayList<>());
                    default -> new NonPerishableProduct(id, name, price, stock);
                };
            }
        } catch (Exception e) {
            System.out.println("Get product by ID error: " + e.getMessage());
        }
        return null;
    }
}