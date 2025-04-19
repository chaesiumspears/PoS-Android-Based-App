/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.product;

/**
 *
 * @author Desi
 */
public class Product {
    public String id;
    public String name;
    public double price;  // Mengubah akses menjadi public
    public String type;

    public Product(String id, String name, double price) {
        this(id, name, price, "non");
    }

    public Product(String id, String name, double price, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }
}
