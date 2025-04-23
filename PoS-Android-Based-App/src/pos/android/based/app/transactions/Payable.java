package pos.android.based.app.transactions;

import java.math.BigDecimal;

public interface Payable {
    void processTransaction();
    void serializeTransaction();
    BigDecimal calculateTotal(); // Tambahkan ini!
}

