/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String USER = "postgres.ilxnjpbevbkpmassrtec";
    private static final String PASSWORD = "poskelompok2"; 

    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver tidak ditemukan.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Koneksi gagal: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}



