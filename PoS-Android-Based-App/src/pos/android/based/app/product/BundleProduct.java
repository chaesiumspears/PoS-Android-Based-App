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
    // List Bundle terdiri dari daftar objek Product
    private List<Product> items;

    //Konstruktor untuk BundleProduct,Memanggil konstruktor superclass dengan tipe bundle, dan menginisialisasi daftar item produk.
    public BundleProduct(String id, String name, double price, int stock, List<Product> items) {
        super(id, name,stock, price, "bundle");
        this.items = items;
    }
    //getter
    public List<Product> getItems() {
        return items;
    }
    //total diskon: harga semua produk - harga bundle 
    public double getDiscountedPrice() {
        double total = 0;
        for (Product p : items) total += p.price;
        return total - this.price;
    }
}