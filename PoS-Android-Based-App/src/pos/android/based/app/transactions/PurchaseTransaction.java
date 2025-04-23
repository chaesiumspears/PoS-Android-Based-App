package pos.android.based.app.transactions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import pos.android.based.app.product.ProductService;

public class PurchaseTransaction extends Transactions implements Payable {
    private static final Scanner scanner = new Scanner(System.in);
    private Transactions transaction;

    public PurchaseTransaction(List<TransactionItem> items) {
        super(0, new java.sql.Date(System.currentTimeMillis()), items, BigDecimal.ZERO, "unpaid");
    }
    
    public void setTransaction(Transactions transaction) {
        this.transaction = transaction;
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
        System.out.println("Total yang harus dibayar: " + total);

        System.out.print("Masukkan jumlah uang dari pelanggan: ");
        BigDecimal amountGiven;
        try {
            amountGiven = new BigDecimal(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid. Transaksi dibatalkan.");
            return;
        }

        if (amountGiven.compareTo(total) < 0) {
            System.out.println("Uang tidak cukup. Transaksi dibatalkan.");
            return;
        }

        BigDecimal change = amountGiven.subtract(total);
        System.out.println("Kembalian: " + change);

        // Kurangi stok produk
        for (TransactionItem item : getItems()) {
            ProductService.updateStock(item.getProductId(), -item.getQuantity());
        }

        setStatus("paid");
        serializeTransaction();
    }
    
    public boolean processPayment(BigDecimal amount) {
    if (amount.compareTo(getTotalPrice()) < 0) {
        return false;
    }
    
    // Update stock and process payment
    for (TransactionItem item : getItems()) {
        ProductService.updateStock(item.getProductId(), -item.getQuantity());
    }
    
    setStatus(STATUS_PAID);
    return TransactionDAO.updateTransactionStatus(getTransactionId(), STATUS_PAID);
}

    @Override
    public void serializeTransaction() {
        boolean success = new TransactionDAO().addTransaction(this);
        if (success) {
            System.out.println("Transaksi berhasil disimpan ke database.");
        } else {
            System.out.println("Gagal menyimpan transaksi ke database.");
        }
    }
}
