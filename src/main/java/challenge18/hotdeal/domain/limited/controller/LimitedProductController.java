package challenge18.hotdeal.domain.limited.controller;

import challenge18.hotdeal.common.security.UserDetailsImpl;
import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.limited.dto.LimitedProductRequestDto;
import challenge18.hotdeal.domain.limited.dto.LimitedProductResponseDto;
import challenge18.hotdeal.domain.limited.service.LimitedProductService;
import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import challenge18.hotdeal.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/limited-products")
public class LimitedProductController {

    private final LimitedProductService limitedProductService;

    // 한정판 상품 등록
//    @Secured("ROLE_ADMIN")
    @PostMapping("")
    public ResponseEntity<Message> registrationLimitedProduct(@RequestBody LimitedProductRequestDto requestDto,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return limitedProductService.registrationLimitedProduct(requestDto, userDetails.getUser());
    }

    // 한정판 상품 목록 조회
    @GetMapping
    public AllProductResponseDto allLimitedProduct(ProductSearchCondition condition) {
        return limitedProductService.allLimitedProduct(condition);
    }

    // 한정판 상품 상세 조회
    @GetMapping("/{limitedProductId}")
    public SelectProductResponseDto selectLimitedProduct(@PathVariable Long limitedProductId) {
        return limitedProductService.selectLimitedProduct(limitedProductId);
    }

    // 한정판 상품 구매
    @PostMapping("/{limitedProductId}")
    public ResponseEntity<Message> buyLimitedProduct(@PathVariable Long limitedProductId,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return limitedProductService.buyLimitedProduct(limitedProductId, userDetails.getUser());
    }

}
