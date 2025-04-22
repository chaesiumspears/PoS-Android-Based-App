/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.product;

/**
 *
 * @author Desi
 */
import java.util.List;

public class BundleProduct extends Product {
    private List<Product> items;

    public BundleProduct(String id, String name, double price, int stock, List<Product> items) {
        super(id, name,stock, price, "bundle");
        this.items = items;
    }

    public List<Product> getItems() {
        return items;
    }

    public double getDiscountedPrice() {
        double total = 0;
        for (Product p : items) total += p.price;
        return total - this.price;
    }
}