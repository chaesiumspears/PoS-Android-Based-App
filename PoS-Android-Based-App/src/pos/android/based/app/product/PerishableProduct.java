/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.product;

/**
 *
 * @author Desi
 */
import java.time.LocalDate;

public class PerishableProduct extends Product {
    //atribut untuk produk kadaluarsa
    private LocalDate expiryDate;
    //konstruktor unruk inisialisasi produk perishable, memanggil konstruktor superclass dengan tipe perishable
    public PerishableProduct(String id, String name, int stock, double price, LocalDate expiryDate) {
        super(id, name, stock, price, "perishable"); 
        this.expiryDate = expiryDate;
    }
    //Getter untuk mengambil expire date
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    @Override
    public String toString() {
        return super.toString() + " | Exp: " + expiryDate;
    }
}