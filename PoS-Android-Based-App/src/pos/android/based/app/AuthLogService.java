package pos.android.based.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class AuthLogService {

    public static void log(String username, String name, String email, String role, String status) {
        String sql = """
            INSERT INTO login_logs(username, name, email, role, login_time, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, name);  // bisa null
            stmt.setString(3, email); // bisa null
            stmt.setString(4, role);  // bisa null
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setString(6, status);

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to log auth: " + e.getMessage());
        }
    }

    
    public static void log(String username, String status) {
    String sql = """
        INSERT INTO login_logs(username, login_time, status)
        VALUES (?, ?, ?)
    """;

    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        stmt.setString(3, status);

        stmt.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Failed to log logout: " + e.getMessage());
    }
}
}
