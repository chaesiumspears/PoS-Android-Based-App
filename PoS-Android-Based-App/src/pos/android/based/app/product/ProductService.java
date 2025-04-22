package pos.android.based.app.product;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pos.android.based.app.DatabaseConnection;

public class ProductService {

    public static String generateProductID(Connection conn, String prefix) throws SQLException {
        String lastID = prefix + "0000";
        String query = "SELECT id FROM products WHERE id Like ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastID = rs.getString("id");
            }
        }
        int num = Integer.parseInt(lastID.substring(1)) + 1;
        return String.format(prefix + "%04d", num);
    }

    //tambah produk
   public static boolean addProduct(Product p) {
    if (p instanceof BundleProduct bundle) {
        return addBundle(bundle);
    }

    String query = "INSERT INTO products(id, name, price, stock, type, expiry_date, url, vendor_name) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.connect()) {
        conn.setAutoCommit(false);

        String id = generateProductID(conn, "P");
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setString(2, p.getName());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, p.getStock() != null ? p.getStock() : 0);
            stmt.setString(5, p.getType());

            if (p instanceof PerishableProduct perish) {
                stmt.setDate(6, java.sql.Date.valueOf(perish.getExpiryDate()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            if (p instanceof DigitalProduct digital) {
                stmt.setString(7, digital.getUrl().toString());
                stmt.setString(8, digital.getVendorName());
            } else {
                stmt.setNull(7, java.sql.Types.VARCHAR);
                stmt.setNull(8, java.sql.Types.VARCHAR);
            }
            stmt.executeUpdate();
        }
        conn.commit();
        return true;
    } catch (Exception e) {
        System.out.println("Add product error: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
    }
   
   //bundle
    private static boolean addBundle(BundleProduct p) {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            String bundleID = generateProductID(conn, "B");
            double normalPrice = p.getItems().stream().mapToDouble(Product::getPrice).sum();
            int productCount = p.getItems().size();
            String itemIds = p.getItems().stream()
                    .map(Product::getId)
                    .collect(Collectors.joining(","));

            String insertBundleProduct = "INSERT INTO products(id, name, price, stock, type) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertBundleProduct)) {
                stmt.setString(1, bundleID);
                stmt.setString(2, p.getName());
                stmt.setDouble(3, p.getPrice());
                stmt.setInt(4, p.getStock() != null ? p.getStock() : 0);
                stmt.setString(5, "bundle");
                stmt.executeUpdate();
            }

            String insertItem = "INSERT INTO bundle_items(bundle_id, item_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertItem)) {
                Map<String, Long> itemCountMap = p.getItems().stream()
                        .collect(Collectors.groupingBy(Product::getId, Collectors.counting()));
                for (Map.Entry<String, Long> entry : itemCountMap.entrySet()) {
                    stmt.setString(1, bundleID);
                    stmt.setString(2, entry.getKey());
                    stmt.setInt(3, entry.getValue().intValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            String insertSummary = "INSERT INTO bundle_summary(bundle_id, bundle_name, product_count, item_ids, normal_price, bundle_price) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSummary)) {
                stmt.setString(1, bundleID);
                stmt.setString(2, p.getName());
                stmt.setInt(3, productCount);
                stmt.setString(4, itemIds);
                stmt.setDouble(5, normalPrice);
                stmt.setDouble(6, p.getPrice());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Error saat menambahkan bundle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //update
        public boolean updateProduct(Product p) {
    String sql = "UPDATE products SET name = ?, price = ?, stock = ?, type = ?, expiry_date = ?, url = ?, vendor_name = ? WHERE id = ?";

    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, p.getName());
        stmt.setDouble(2, p.getPrice());
        stmt.setInt(3, p.getStock());
        stmt.setString(4, p.getType());

        if (p instanceof PerishableProduct perish) {
            stmt.setDate(5, java.sql.Date.valueOf(perish.getExpiryDate()));
        } else {
            stmt.setNull(5, java.sql.Types.DATE);
        }

        if (p instanceof DigitalProduct digital) {
            stmt.setString(6, digital.getUrl().toString());
            stmt.setString(7, digital.getVendorName());
        } else {
            stmt.setNull(6, java.sql.Types.VARCHAR);
            stmt.setNull(7, java.sql.Types.VARCHAR);
        }

        stmt.setString(8, p.getId());
        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    //delete
    public static boolean deleteProduct(String id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
         
            stmt.setString(1, id);
            int affectedRows = stmt.executeUpdate(); // hanya sekali
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static List<Product> getItemsForBundle(String bundleId) throws SQLException, MalformedURLException {
        List<Product> items = new ArrayList<>();
        String query = "SELECT p.*, b.quantity FROM products p " +
                       "JOIN bundle_items b ON p.id = b.item_id " +
                       "WHERE b.bundle_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bundleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int qty = rs.getInt("quantity");
                for (int i = 0; i < qty; i++) {
                    items.add(new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getDouble("price"),
                        rs.getString("type")
                    ));
                }
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

                Product p = switch (type.toLowerCase()) {
                    case "perishable" -> new PerishableProduct(id, name, stock, price, LocalDate.parse(rs.getString("expiry_date")));
                    case "digital" -> new DigitalProduct(id, name, price, stock, new URL(rs.getString("url")), rs.getString("vendor_name"));
                    case "bundle" -> new BundleProduct(id, name, price,stock, getItemsForBundle(id));
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

                return switch (type.toLowerCase()) {
                    case "perishable" -> new PerishableProduct(id, name, stock, price, LocalDate.parse(rs.getString("expiry_date")));
                    case "digital" -> new DigitalProduct(id, name, price, stock, new URL(rs.getString("url")), rs.getString("vendor_name"));
                    case "bundle" -> new BundleProduct(id, name, price,stock,getItemsForBundle(id));
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
            stmt.setInt(1, quantityChange); // jumlah perubahan stok (positif atau negatif)
            stmt.setString(2, productId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }
    

    public static double getProductPrice(String productId) {
        String query = "SELECT price FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            } else {
                System.out.println("Product ID not found.");
                return -1;  // Mengembalikan -1 jika produk tidak ditemukan
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product price: " + e.getMessage());
        }
        return -1;  // Mengembalikan -1 jika terjadi error
    }
}
