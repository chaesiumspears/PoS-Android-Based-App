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
    private URL url;
    private String vendorName;

    public DigitalProduct(String id, String name, double price, URL url, String vendorName) {
        super(id, name, price, "digital");
        this.url = url;
        this.vendorName = vendorName;
    }

    public URL getUrl() {
        return url;
    }

    public String getVendorName() {
        return vendorName;
    }
}