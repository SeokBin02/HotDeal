package challenge18.hotdeal.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchCondition {
    private Long minPrice;
    private Long maxPrice;
    private String mainCategory;
    private String subCategory;
    private String keyword;
    private Long queryIndex;
    private Integer queryLimit;

    public ProductSearchCondition(Long minPrice, Long maxPrice, String mainCategory, String subCategory, String keyword, Long queryIndex, Integer queryLimit) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.keyword = keyword;
        this.queryIndex = queryIndex;
        this.queryLimit = queryLimit;

    }

    public void setCondition(ProductSearchCondition condition){
        this.minPrice = condition.getMinPrice();
        this.maxPrice = condition.getMaxPrice();
        this.mainCategory = condition.getMainCategory();
        this.subCategory = condition.getSubCategory();
        this.keyword = condition.getKeyword();
    }
}
