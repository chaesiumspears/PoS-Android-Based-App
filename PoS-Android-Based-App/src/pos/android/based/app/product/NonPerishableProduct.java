/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.product;

/**
 *
 * @author Desi
 */


public class NonPerishableProduct extends Product {
    public NonPerishableProduct(String id, String name, double price, int stock) {
        super(id, name, stock, price, "non");
    }
}


