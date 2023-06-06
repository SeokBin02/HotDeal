package challenge18.hotdeal.domain.product.dto;

import challenge18.hotdeal.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AllProductResponseDto {
    private List<SelectProductResponseDto> content;
    private boolean next;
}
