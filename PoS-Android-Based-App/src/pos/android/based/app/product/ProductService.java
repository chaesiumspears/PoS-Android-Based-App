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
import java.util.stream.Collectors;

import pos.android.based.app.DatabaseConnection;

public class ProductService {
    
    //GENERETE FORMAT ID
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

    //UNTUK MENAMBAH PRODUK TIDAK MEMILIKI KADALUARSA
    public static boolean addNonPerishable(String name, double price, int stock) {
    return addGeneralProduct(name, price, stock, "Non-Perishable", null, null, null);
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
            
            if (expiryDate != null) {
                stmt.setDate(6, java.sql.Date.valueOf(expiryDate));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
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

    //UNTUK MENAMBAH PRODUK YANG MEMILIKI KADALUARSA
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

    //MENAMBAH PRODUK DIGITAL
    public static boolean addDigital(String name, double price, int stock, URL url, String vendor) {
        return addGeneralProduct(name, price, stock, "digital", null, url.toString(), vendor);
    }

   //MENAMBAH BUNDLE PRODUCT
   public static boolean addBundle(String name, double price, int stock, List<Product> items) {
    String bundleID = null;
    try (Connection conn = DatabaseConnection.connect()) {
        conn.setAutoCommit(false);
        bundleID = generateProductID(conn);
        double normalPrice = items.stream().mapToDouble(Product::getPrice).sum();
        int productCount = items.size();
        String itemIds = items.stream()
                .map(Product::getId)
                .collect(Collectors.joining(","));     
        String insertBundleProduct = "INSERT INTO products(id, name, price, stock, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertBundleProduct)) {
            stmt.setString(1, bundleID);
            stmt.setString(2, name);
            stmt.setDouble(3, price); 
            stmt.setInt(4, stock);
            stmt.setString(5, "bundle");
            stmt.executeUpdate();
        }   
        String insertItem = "INSERT INTO bundle_items(bundle_id, item_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertItem)) {
            for (Product item : items) {
                stmt.setString(1, bundleID);
                stmt.setString(2, item.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
        String insertSummary = "INSERT INTO bundle_summary(bundle_id, bundle_name, product_count, item_ids, normal_price, bundle_price) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSummary)) {
            stmt.setString(1, bundleID);
            stmt.setString(2, name);
            stmt.setInt(3, productCount);
            stmt.setString(4, itemIds);
            stmt.setDouble(5, normalPrice);
            stmt.setDouble(6, price);
            stmt.executeUpdate();
        }
        conn.commit();
        return true;
    } catch (SQLException e) {
        System.err.println(" Error saat menambahkan bundle: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}



    
    
    

    public static boolean updateProduct(String id, String name, double price, int stock, String type, LocalDate expiryDate) {
    String sql = "UPDATE products SET name = ?, price = ?, stock = ?, type = ?, expiry_date = ? WHERE id = ?";
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, name);
        stmt.setDouble(2, price);
        stmt.setInt(3, stock);
        stmt.setString(4, type);
        if ("Perishable".equals(type)) {
            stmt.setDate(5, java.sql.Date.valueOf(expiryDate));
        } else {
            stmt.setNull(5, java.sql.Types.DATE);
        }
        stmt.setString(6, id);

        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    //UNTUK MENGHAPUS PRODUK
    public static boolean deleteProduct(String id) {
        String sql = "DELETE FROM products WHERE id = ?";
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, id);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    }
    
    
    private static List<Product> getItemsForBundle(String bundleId) throws SQLException, MalformedURLException {
    List<Product> items = new ArrayList<>();
    String query = "SELECT p.* FROM products p JOIN bundle_items b ON p.id = b.item_id WHERE b.bundle_id = ?";
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, bundleId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            items.add(new Product(
                rs.getString("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("type")
            ));
        }
    }
    return items;
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
                case "bundle" -> new BundleProduct(id, name, price, getItemsForBundle(id)); //
                default -> new NonPerishableProduct(id, name, price, stock);
                };

                if (p != null) products.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Get products error: " + e.getMessage());
        }
        return products;
    }

    //Produk ID
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
                    case "bundle" -> new BundleProduct(id, name, price, getItemsForBundle(id)); //
                    default -> new NonPerishableProduct(id, name, price, stock);
                };
            }
        } catch (Exception e) {
            System.out.println("Get product by ID error: " + e.getMessage());
        }
        return null;
    }
    
    public static boolean updateStock(String productId, int quantityChange) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("UPDATE products SET stock = stock + ? WHERE id = ?")) {
            stmt.setInt(1, quantityChange); 
            stmt.setString(2, productId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }
}