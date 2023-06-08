package challenge18.hotdeal.domain.product.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="products")
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column
    private String productName;

    @Column
    private int price;

    @Column
    private String categoryA;

    @Column
    private String categoryB;


    @Column
    private int amount;

    public Product(String productName, int price, String categoryA, String categoryB, int amount) {
        this.productName = productName;
        this.price = price;
        this.categoryA = categoryA;
        this.categoryB = categoryB;
        this.amount = amount;
    }

    public void buy(int quantity) {
        this.amount -= quantity;
    }
}
