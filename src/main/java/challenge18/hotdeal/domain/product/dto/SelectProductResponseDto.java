package challenge18.hotdeal.domain.product.dto;

import challenge18.hotdeal.domain.limited.entity.LimitedProduct;
import challenge18.hotdeal.domain.product.entity.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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
        this.categoryA = product.getCategoryA();
        this.categoryB = product.getCategoryB();
        this.amount = product.getAmount();

    }

    public SelectProductResponseDto(LimitedProduct limitedProduct) {
        this.id = limitedProduct.getId();
        this.productName = limitedProduct.getProductName();
        this.price = limitedProduct.getPrice();
        this.amount = limitedProduct.getAmount();
        this.categoryA = limitedProduct.getCategoryA();
        this.categoryB = limitedProduct.getCategoryB();
    }

    public SelectProductResponseDto(Long id, String productName, int price) {
        this.id = id;
        this.productName = productName;
        this.price = price;
    }

    public SelectProductResponseDto(String productName, int price) {
        this.productName = productName;
        this.price = price;
    }

    public SelectProductResponseDto(Long id, String productName, int price, String categoryA, String categoryB, int amount) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.categoryA = categoryA;
        this.categoryB = categoryB;
        this.amount = amount;
    }
}
