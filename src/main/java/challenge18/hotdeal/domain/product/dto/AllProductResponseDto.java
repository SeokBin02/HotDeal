package challenge18.hotdeal.domain.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AllProductResponseDto {
    private List<SelectProductResponseDto> content;
    private boolean next;

    public AllProductResponseDto(List<SelectProductResponseDto> content, boolean next) {
        this.content = content;
        this.next = next;
    }
}
