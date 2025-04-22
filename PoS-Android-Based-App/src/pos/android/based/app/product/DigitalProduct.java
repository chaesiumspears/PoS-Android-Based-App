/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.product;

/**
 *
 * @author Desi
 */
import java.net.URL;

public class DigitalProduct extends Product {
    //atribut produk digital
    private URL url;
    private String vendorName;
    //Konstruktor lengkap untuk produk digital Memanggil konstruktor superclass dengan tipe "digital"
    public DigitalProduct(String id, String name, double price,int stock, URL url, String vendorName) {
        super(id, name, stock, price, "digital");
        this.url = url;
        this.vendorName = vendorName;
    }
    // Getter method untuk mengambil nilai URL produk
    public URL getUrl() {
        return url;
    }
    // Getter method untuk mengambil nama vendor produk
    public String getVendorName() {
        return vendorName;
    }
}