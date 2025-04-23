package pos.android.based.app.transactions;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import pos.android.based.app.product.ProductService;

public class RefundTransaction extends Transactions implements Payable {

    public RefundTransaction(int originalTransactionId, List<TransactionItem> itemsToRefund) {
        super(originalTransactionId, new Date(System.currentTimeMillis()), itemsToRefund, BigDecimal.ZERO, "refunded");
    }

    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionItem item : getItems()) {
            total = total.add(item.getSubTotal());
        }
        setTotalPrice(total);
        return total;
    }

    @Override
    public void processTransaction() {
        BigDecimal total = calculateTotal();
        System.out.println("Total refund yang dikembalikan: " + total);

        // Tambahkan kembali stok produk
        for (TransactionItem item : getItems()) {
            ProductService.updateStock(item.getProductId(), item.getQuantity());
        }

        setStatus("refunded");
        serializeTransaction();
    }

    @Override
    public void serializeTransaction() {
        boolean success = new TransactionDAO().addTransaction(this);
        if (success) {
            System.out.println("✅ Transaksi refund berhasil disimpan ke database.");
        } else {
            System.out.println("❌ Gagal menyimpan refund ke database.");
        }
    }
}
