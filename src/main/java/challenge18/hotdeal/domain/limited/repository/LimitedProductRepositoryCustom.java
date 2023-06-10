package challenge18.hotdeal.domain.limited.repository;

import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;

public interface LimitedProductRepositoryCustom {
    AllProductResponseDto customFindAll(ProductSearchCondition condition);
    AllProductResponseDto findAllByCondition(ProductSearchCondition condition);
}
