package challenge18.hotdeal.domain.purchase.repository;

import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseRespositoryCustom {
    AllProductResponseDto findTopN(int queryLimit);
//    List<AllProductResponseDto> findTop90(Pageable pageable);
//    List<AllProductResponseDto> findTop90();
}
