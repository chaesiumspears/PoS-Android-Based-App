/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.View;

import pos.android.based.app.UserAuthLog;
import pos.android.based.app.AuthLogService;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class AuthLogUI extends JFrame {
    private final JTable table;
    private final DefaultTableModel model;
    
    public AuthLogUI() {
        setTitle("Authentication Logs");
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Initialize table model
        String[] columns = {"ID", "Username", "Login Time", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        initUI();
        loadData();
    }

    private void initUI() {
        // Custom cell renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 3) { // Status column
                    String status = (String) value;
                    c.setForeground("success".equals(status) ? new Color(0, 128, 0) : Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        // Clear existing data
        model.setRowCount(0);
        
        // Load new data with lambda expression
        AuthLogService.getAuthLogs().forEach(authLog -> 
            model.addRow(authLog.toTableRow())
        );
    }
}