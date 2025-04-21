/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.View;

import pos.android.based.app.product.ProductActivityLog;
import pos.android.based.app.product.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductActivityLogUI extends JFrame {
    private JTable logTable;
    private DefaultTableModel tableModel;

    public ProductActivityLogUI() {
        setTitle("Product Activity Log");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadLogData();
    }

    private void initUI() {
        // Create table model
        String[] columnNames = {"Timestamp", "Action", "Product ID", "Product Name", "Details", "Performed By"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        logTable = new JTable(tableModel);
        logTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(logTable);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> actionFilter = new JComboBox<>(new String[]{"All", "input", "update", "sale", "delete"});
        JButton filterButton = new JButton("Filter");
        
        filterButton.addActionListener(e -> {
            String selectedAction = (String) actionFilter.getSelectedItem();
            loadLogData(selectedAction.equals("All") ? null : selectedAction);
        });

        filterPanel.add(new JLabel("Filter by Action:"));
        filterPanel.add(actionFilter);
        filterPanel.add(filterButton);

        // Main layout
        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLogData() {
        loadLogData(null);
    }

    private void loadLogData(String actionType) {
        tableModel.setRowCount(0); // Clear existing data
        
        List<ProductActivityLog> logs = ProductService.getProductActivityLogs();
        for (ProductActivityLog log : logs) {
            if (actionType == null || log.getActionType().equals(actionType)) {
                tableModel.addRow(new Object[]{
                    log.getPerformedAt().toString(),
                    log.getActionType(),
                    log.getProductId(),
                    log.getProductName(),
                    log.getActionDetails(),
                    log.getPerformedBy()
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductActivityLogUI ui = new ProductActivityLogUI();
            ui.setVisible(true);
        });
    }
}