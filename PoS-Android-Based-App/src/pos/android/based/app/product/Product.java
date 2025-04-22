package pos.android.based.app.product;

public class Product {
    private String id;
    private String name;
    double price;
    private Integer stock;
    private String type;

    // Constructor
    public Product(String id, String name, Integer stock, double price) {
        this(id, name, stock, price, "non");
    }

    public Product(String id, String name, Integer stock, double price, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.type = type;
    }

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
        return (stock !=null)?stock:0;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " - Rp" + price;
    }
}
