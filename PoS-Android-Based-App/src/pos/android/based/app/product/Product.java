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
    public double price;
    public Integer stock;
    public String type;

    // Constructor default untuk NonPerishable
    public Product(String id, String name, Integer stock, double price) {
        this(id, name, stock, price, "non");
    }

    // Constructor lengkap
    public Product(String id, String name, Integer stock, double price, String type) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.type = type;
    }

    // Constructor jika stock tidak dibutuhkan (misal digital)
    public Product(String id, String name, double price, String type) {
        this(id, name, null, price, type);
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getType() {
        return type;
    }
}
