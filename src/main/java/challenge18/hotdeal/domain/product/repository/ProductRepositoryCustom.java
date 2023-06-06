package challenge18.hotdeal.domain.product.repository;


import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface ProductRepositoryCustom {

//    Page<AllProductResponseDto> findAllByPriceAndCategory(ProductSearchCondition condition, Pageable pageable);
    AllProductResponseDto findAllByPriceAndCategory(ProductSearchCondition condition);
//    List<AllProductResponseDto> findAllByPriceAndCategory(ProductSearchCondition condition, Pageable pageable);

}
