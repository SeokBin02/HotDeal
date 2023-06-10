package challenge18.hotdeal.domain.product.repository;


import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;

public interface ProductRepositoryCustom {

//    Page<AllProductResponseDto> findAllByPriceAndCategory(ProductSearchCondition condition, Pageable pageable);
    AllProductResponseDto findAllbyCondition(ProductSearchCondition condition);
//    List<AllProductResponseDto> findAllByPriceAndCategory(ProductSearchCondition condition, Pageable pageable);

}
