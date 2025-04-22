

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app;

import java.sql.Timestamp;

public class UserAuthLog {
    private int id;
    private String username;
    private Timestamp loginTime;
    private String status;
    
    public UserAuthLog(int id, String username, Timestamp loginTime, String status) {
        this.id = id;
        this.username = username;
        this.loginTime = loginTime;
        this.status = status;
    }
    
    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public Timestamp getLoginTime() { return loginTime; }
    public String getStatus() { return status; }
    
    // For table display
    public Object[] toTableRow() {
        return new Object[]{
            id,
            username,
            loginTime,
            status
        };
    }
}