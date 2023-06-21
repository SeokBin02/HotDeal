package challenge18.hotdeal.domain.limited.controller;

import challenge18.hotdeal.common.facade.RedissonLockFacade;
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
    private final RedissonLockFacade redissonLockFacade;

    // 한정판 상품 등록
//    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Message> registerLimitedProduct(@RequestBody LimitedProductRequestDto requestDto,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return limitedProductService.registerLimitedProduct(requestDto, userDetails.getUser());
    }

    // 한정판 상품 목록 조회
    @GetMapping
    public AllProductResponseDto getLimitedProducts(ProductSearchCondition condition) {
        return limitedProductService.getLimitedProducts(condition);
    }

    // 한정판 상품 상세 조회
    @GetMapping("/{limitedProductId}")
    public SelectProductResponseDto getLimitedProductDetail(@PathVariable Long limitedProductId) {
        return limitedProductService.getLimitedProductDetail(limitedProductId);
    }

    // 한정판 상품 구매
    @PostMapping("/{limitedProductId}")
    public ResponseEntity<Message> buyLimitedProduct(@PathVariable Long limitedProductId,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return redissonLockFacade.buy("limitedProduct", limitedProductId, 1, userDetails.getUser());
    }

}
