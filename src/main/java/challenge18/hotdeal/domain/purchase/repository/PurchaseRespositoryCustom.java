package challenge18.hotdeal.domain.purchase.repository;

import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRespositoryCustom {
    List<SelectProductResponseDto> findTopN(String today, String yesterday);
}
