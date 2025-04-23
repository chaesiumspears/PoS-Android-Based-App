package pos.android.based.app.transactions;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Transactions {
    private int transactionId;
    Date transactionDate;
    List<TransactionItem> items;
    private BigDecimal totalPrice;
    private String status;
    private LocalDate date;

    public static final String STATUS_NOT_PAID = "unpaid";
    public static final String STATUS_PAID = "paid";
    public static final String STATUS_CANCELLED = "refunded";

    public Transactions(int transactionId) {
        this.transactionId = transactionId;
        this.items = new ArrayList<>();
        this.transactionDate = Date.valueOf(LocalDate.now());
        this.status = STATUS_NOT_PAID;
        this.totalPrice = BigDecimal.ZERO;
    }

    public Transactions(int transactionId, Date transactionDate, List<TransactionItem> items, BigDecimal totalPrice, String status) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = (status != null) ? status : STATUS_NOT_PAID;
    }
    
    public Transactions(int transactionId, List<TransactionItem> items, LocalDate localDate, String status) {
    this.transactionId = transactionId;
    this.items = items;
    this.transactionDate = Date.valueOf(localDate); // konversi ke java.sql.Date
    this.status = (status != null) ? status : STATUS_NOT_PAID;
    this.totalPrice = calculateTotalPrice(); // otomatis hitung total
}

    public BigDecimal calculateTotalPrice() {
        BigDecimal calculatedTotal = BigDecimal.ZERO;
        for (TransactionItem item : items) {
            calculatedTotal = calculatedTotal.add(item.getSubTotal());
        }
        return calculatedTotal;
    }
    
    public void addItem(TransactionItem item){
        if (items == null) {
        items = new ArrayList<>();
        }
        items.add(item);
        totalPrice = totalPrice.add(item.getSubTotal());
    }
    
    public LocalDate getDate(){
        return date;
    }
    
    public void setDate(LocalDate date){
        this.date = date;
    }
    
    public String getStatus() {
    return status;
}

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public List<TransactionItem> getItems() {
        return items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = java.sql.Date.valueOf(transactionDate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction ID: ").append(transactionId).append("\n")
          .append("Date: ").append(transactionDate).append("\n")
          .append("Status: ").append(status).append("\n")
          .append("Total Price: ").append(totalPrice).append("\n")
          .append("Items:\n");

        for (TransactionItem item : items) {
            sb.append("  ").append(item.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transactions that = (Transactions) obj;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(transactionId);
    }
}
