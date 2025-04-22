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

    //Generate ID Produk secara otomatis berdasarkan prefix dan nomor urut terakhir.
    public static String generateProductID(Connection conn, String prefix) throws SQLException {
        String lastID = prefix + "0000";
        String query = "SELECT id FROM products WHERE id Like ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) { //prepared statement untuk keamanan dan efisien
            // Mengatur parameter prefix ("P%" untuk produk biasa, "B%" untuk bundle)
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
             // Ambil ID terakhir jika ada hasil
            if (rs.next()) {
                lastID = rs.getString("id");
            }
        }
        // Ambil bagian angka dari ID  k
        int num = Integer.parseInt(lastID.substring(1)) + 1;
        // Format ID baru dengan prefix dan angka 4 digit 
        return String.format(prefix + "%04d", num);
    }

  //menambahkan produk ke database 
public static boolean addProduct(Product p, String performedBy) {
    if (p instanceof BundleProduct bundle) {
        return addBundle(bundle, performedBy);
    }

    String query = "INSERT INTO products(id, name, price, stock, type, expiry_date, url, vendor_name) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.connect()) {
        conn.setAutoCommit(false);

        // 1. Generate ID dan set ke objek
        String id = generateProductID(conn, "P");
        p.setId(id);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // 2. Field dasar semua produk
            stmt.setString(1, id);
            stmt.setString(2, p.getName());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, (p.getStock() != null) ? p.getStock() : 0);
            stmt.setString(5, p.getType());

            // 3. Field khusus: expiry, url, vendor
            setProductSpecificFields(stmt, p);

            stmt.executeUpdate();
        }

        conn.commit();

        // 4. Logging
        ProductActivityLogService.logAction(id, "input", "Added new product: " + p.getName(), performedBy);
        return true;

    } catch (Exception e) {
        System.out.println("Add product error: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

private static void setProductSpecificFields(PreparedStatement stmt, Product p) throws SQLException {
    // Expiry (kolom ke-6)
    if (p instanceof PerishableProduct perish) {
        stmt.setDate(6, java.sql.Date.valueOf(perish.getExpiryDate()));
    } else {
        stmt.setNull(6, java.sql.Types.DATE);
    }

    // URL dan Vendor (kolom ke-7 dan 8)
    if (p instanceof DigitalProduct digital) {
        stmt.setString(7, digital.getUrl().toString());
        stmt.setString(8, digital.getVendorName());
    } else {
        stmt.setNull(7, java.sql.Types.VARCHAR);
        stmt.setNull(8, java.sql.Types.VARCHAR);
    }
}

   
   //Menambahkan produk bundle dan komponennya ke dalam database.
    private static boolean addBundle(BundleProduct p, String performedBy) {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            String bundleID = generateProductID(conn, "B");
            p.setId(bundleID);
            //hitung total harga normal
            double normalPrice = p.getItems().stream().mapToDouble(Product::getPrice).sum();
            //hitung jumlah total item
            int productCount = p.getItems().size();
            //memisahkan id dengan koma
            String itemIds = p.getItems().stream()
                    .map(Product::getId)
                    .collect(Collectors.joining(","));
            //Insert bundle ke tabel products
            String insertBundleProduct = "INSERT INTO products(id, name, price, stock, type) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertBundleProduct)) {
                stmt.setString(1, bundleID);
                stmt.setString(2, p.getName());
                stmt.setDouble(3, p.getPrice());
                stmt.setInt(4, p.getStock() != null ? p.getStock() : 0);
                stmt.setString(5, "bundle");
                stmt.executeUpdate();
            }
            //Insert relasi item bundle items
            String insertItem = "INSERT INTO bundle_items(bundle_id, item_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertItem)) {
                //hitung jumlah tiap item
                Map<String, Long> itemCountMap = p.getItems().stream()
                        .collect(Collectors.groupingBy(Product::getId, Collectors.counting()));
                for (Map.Entry<String, Long> entry : itemCountMap.entrySet()) {
                    stmt.setString(1, bundleID);
                    stmt.setString(2, entry.getKey());
                    stmt.setInt(3, entry.getValue().intValue());
                    stmt.addBatch();
                }
                stmt.executeBatch(); // Kirim semua insert sekaligus
            }
            //Insert ke bundle_summary
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
            
            // log
            ProductActivityLogService.logAction(bundleID, "input", "Added new bundle: " + p.getName(), performedBy);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error saat menambahkan bundle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //update data produk berdasarkan ID
    public boolean updateProduct(Product p, String performedBy) {
    String sql = "UPDATE products SET name = ?, price = ?, stock = ?, type = ?, expiry_date = ?, url = ?, vendor_name = ? WHERE id = ?"; //SQL
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        //Umum
        stmt.setString(1, p.getName());
        stmt.setDouble(2, p.getPrice());
        stmt.setInt(3, p.getStock());
        stmt.setString(4, p.getType());
        //perishible
        if (p instanceof PerishableProduct perish) {
            stmt.setDate(5, java.sql.Date.valueOf(perish.getExpiryDate()));
        } else {
            stmt.setNull(5, java.sql.Types.DATE); // null untuk produk selain perishable
        }
        //Digital
        if (p instanceof DigitalProduct digital) {
            stmt.setString(6, digital.getUrl().toString());
            stmt.setString(7, digital.getVendorName());
        } else {
            stmt.setNull(6, java.sql.Types.VARCHAR);
            stmt.setNull(7, java.sql.Types.VARCHAR);
        }
        //ID produk sebagai acuan update (WHERE id = ?)
        stmt.setString(8, p.getId());
        boolean updated = stmt.executeUpdate() > 0; //// true jika ada baris yang berhasil diupdate
        if (updated) {
            ProductActivityLogService.logAction(p.getId(), "update", "Updated product: " + p.getName(), performedBy);
        }
        return updated;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public static boolean deleteProduct(String id, String performedBy) {
    try (Connection conn = DatabaseConnection.connect()) {
        conn.setAutoCommit(false);

        // 1. Log aktivitas delete (catat bahwa produk dihapus)
        ProductActivityLogService.logAction(id, "delete", "Deleted product with ID: " + id, performedBy);

        // 2. (Opsional) Arsipkan log lama ke tabel arsip (boleh diaktifkan kalau memang ingin backup log lama)
        PreparedStatement archiveLogs = conn.prepareStatement("""
            INSERT INTO product_activity_log_archive
            SELECT * FROM product_activity_log WHERE product_id = ?
        """);
        archiveLogs.setString(1, id);
        archiveLogs.executeUpdate();

        // 3. Jangan hapus dari product_activity_log (kita ingin menyimpan riwayatnya)

        // 4. Hapus relasi bundle
        PreparedStatement clearBundleItems = conn.prepareStatement("DELETE FROM bundle_items WHERE bundle_id = ? OR item_id = ?");
        clearBundleItems.setString(1, id);
        clearBundleItems.setString(2, id);
        clearBundleItems.executeUpdate();

        PreparedStatement clearBundleSummary = conn.prepareStatement("DELETE FROM bundle_summary WHERE bundle_id = ?");
        clearBundleSummary.setString(1, id);
        clearBundleSummary.executeUpdate();

        // 5. Hapus produk dari tabel products
        PreparedStatement deleteProduct = conn.prepareStatement("DELETE FROM products WHERE id = ?");
        deleteProduct.setString(1, id);
        int affected = deleteProduct.executeUpdate();

        conn.commit();
        return affected > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}



    
    //Mendapatkan semua item dalam satu bundle, Membuat ulang objek produk dari database.
    private static List<Product> getItemsForBundle(String bundleId) throws SQLException, MalformedURLException {
        List<Product> items = new ArrayList<>();
        String query = "SELECT p.*, b.quantity FROM products p " +
                       "JOIN bundle_items b ON p.id = b.item_id " +
                       "WHERE b.bundle_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bundleId);
            ResultSet rs = stmt.executeQuery(); //eksekusi query dan hasilnya disimpan di ResultSet
            while (rs.next()) {
                int qty = rs.getInt("quantity"); //ambil jumlah produk untuk item yang dipilih
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

    // Mengambil semua produk dari database, Polymorphism digunakan untuk membuat objek sesuai tipenya.
    public static List<Product> getAllProducts() throws MalformedURLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY id";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            //mengambil field dasar
            while (rs.next()) { 
                String id = rs.getString("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String type = rs.getString("type");
                //Membuat objek sesuai dengan tipe produk yg dibaca dari db
                Product p = switch (type.toLowerCase()) {
                    case "perishable" -> new PerishableProduct(id, name, stock, price, LocalDate.parse(rs.getString("expiry_date")));
                    case "digital" -> new DigitalProduct(id, name, price, stock, new URL(rs.getString("url")), rs.getString("vendor_name"));
                    case "bundle" -> new BundleProduct(id, name, price,stock, getItemsForBundle(id));
                    default -> new NonPerishableProduct(id, name, price, stock);
                };
                //Menambah objek produk yg berhasil dibuat ke dalam list hasil
                if (p != null) products.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Get products error: " + e.getMessage());
        }
        return products;
    }

    //Mengambil satu produk berdasarkan ID-nya.
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
    
    //Update jumlah stok produk berdasarkan perubahan tertentu.
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
    
    //Mengambil harga dari produk tertentu berdasarkan ID-nya.
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
    
    public static boolean isProductExists(String productId) {
    String query = "SELECT 1 FROM products WHERE id = ?";
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, productId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();  // true jika produk ditemukan
    } catch (SQLException e) {
        e.printStackTrace(); // debug error
        return false;
    }
}

}