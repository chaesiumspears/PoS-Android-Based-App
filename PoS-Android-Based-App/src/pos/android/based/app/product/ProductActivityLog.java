/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author HP
 */
package pos.android.based.app.product;

// ppp

import java.time.LocalDateTime;

public class ProductActivityLog {
    private int logId;
    private String productId;
    private String productName;
    private String actionType;
    private String actionDetails;
    private String performedBy;
    private LocalDateTime performedAt;

    public ProductActivityLog(int logId, String productId, String productName, 
                            String actionType, String actionDetails, 
                            String performedBy, LocalDateTime performedAt) {
        this.logId = logId;
        this.productId = productId;
        this.productName = productName;
        this.actionType = actionType;
        this.actionDetails = actionDetails;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
    }

    // Getter methods
    public int getLogId() { return logId; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getActionType() { return actionType; }
    public String getActionDetails() { return actionDetails; }
    public String getPerformedBy() { return performedBy; }
    public LocalDateTime getPerformedAt() { return performedAt; }
}