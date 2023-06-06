package challenge18.hotdeal.domain.product.dto;

import challenge18.hotdeal.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class SelectProductResponseDto {
    private String productName;
    private int price;
    private int amount;
    private String categoryA;
    private String categoryB;

    public SelectProductResponseDto(Product product) {
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.amount = product.getAmount();
        this.categoryA = product.getCategoryA();
        this.categoryB = product.getCategoryB();
    }

    public SelectProductResponseDto(String productName, int price) {
        this.productName = productName;
        this.price = price;
    }
}
