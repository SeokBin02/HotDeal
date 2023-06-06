package challenge18.hotdeal.domain.product.dto;

import challenge18.hotdeal.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class SelectProductResponseDto {
    private Long id;
    private String productName;
    private int price;
    private int amount;
    private String categoryA;
    private String categoryB;

    public SelectProductResponseDto(Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.amount = product.getAmount();
        this.categoryA = product.getCategoryA();
        this.categoryB = product.getCategoryB();
    }

    public SelectProductResponseDto(Long id, String productName, int price) {
        this.id = id;
        this.productName = productName;
        this.price = price;
    }
}
