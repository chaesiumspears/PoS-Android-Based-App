package pos.android.based.app.product;

import pos.android.based.app.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import static pos.android.based.app.product.ProductService.isProductExists;

public class ProductActivityLogService {

    // Mengambil data log aktivitas produk (dengan filter opsional)
    public static List<ProductActivityLog> getActivityLogs(String filterAction) {
        List<ProductActivityLog> logs = new ArrayList<>();
        String query = "SELECT * FROM product_activity_log";
        
        if (filterAction != null && !filterAction.equals("All")) {
            query += " WHERE action_type = ?";
        }
        query += " ORDER BY performed_at DESC";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            if (filterAction != null && !filterAction.equals("All")) {
                stmt.setString(1, filterAction);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("performed_at");

                logs.add(new ProductActivityLog(
                    rs.getInt("log_id"),
                    rs.getString("product_id"),
                    rs.getString("action_type"),
                    rs.getString("action_details"),
                    rs.getString("performed_by"),
                    timestamp != null ? timestamp.toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching activity logs: " + e.getMessage());
        }
        return logs;
    }

    // Mencatat log baru ke tabel product_activity_log
   public static void logAction(String productId, String action, String details, String performedBy) {
    if (!isProductExists(productId)) {
        System.err.println("Gagal mencatat log: Produk ID " + productId + " tidak ditemukan.");
        return;
    }

    String sql = "INSERT INTO product_activity_log(product_id, action_type, action_details, performed_by) VALUES (?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, productId);
        stmt.setString(2, action);
        stmt.setString(3, details);
        stmt.setString(4, performedBy);
        stmt.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error logging product activity: " + e.getMessage());
    }
}
}
