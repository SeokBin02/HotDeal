package challenge18.hotdeal.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ProductSearchCondition {
    private Long minPrice;
    private Long maxPrice;
    private String mainCategory;
    private String subCategory;
    private String keyword;
    private Long queryIndex=0L;
    private Integer queryLimit=30;

    public ProductSearchCondition(Long minPrice, Long maxPrice, String mainCategory, String subCategory, String keyword, Long queryIndex, Integer queryLimit) {
        if (minPrice != null) {
            if (minPrice < 0) {
                minPrice = 0L;
            } else if (minPrice > 1000000000) {
                minPrice = 999999999L;
            }
        }
        
        if (maxPrice != null) {
            if (maxPrice < 0) {
                maxPrice = 0L;
            } else if (maxPrice > 1000000000) {
                maxPrice = 999999999L;
            }
        }
        
        if (minPrice != null && maxPrice != null){
            if (minPrice > maxPrice){
                long temp = minPrice;
                minPrice = maxPrice;
                maxPrice = temp;
            }
        }

        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.keyword = keyword;
        this.queryIndex = queryIndex;
        this.queryLimit = queryLimit;
    }

    public boolean isEmpty() {
        if (mainCategory == null) {
            mainCategory = "";
        }

        if (subCategory == null) {
            subCategory = "";
        }

        if (keyword == null) {
            keyword = "";
        }

        if (minPrice == null && maxPrice == null && mainCategory.equals("") && subCategory.equals("") && keyword.equals("")) {
            return true;
        }

        return false;
    }
}
