package pos.android.based.app.transactions;

import java.math.BigDecimal;

public class TransactionItem {
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;

    // Constructor
    public TransactionItem(String productId, int quantity, double unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = BigDecimal.valueOf(unitPrice);
        calculateSubTotal(); // hitung langsung saat dibuat
    }

    // Hitung ulang subtotal
    public void calculateSubTotal() {
        this.subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getter & Setter
    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubTotal(); // update subtotal otomatis saat quantity berubah
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubTotal(); // update subtotal otomatis saat harga berubah
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    // Representasi string (untuk debug atau UI)
    @Override
    public String toString() {
        return productId + " x" + quantity + " = Rp" + subTotal;
    }
}
