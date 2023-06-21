package challenge18.hotdeal.domain.product.controller;

import challenge18.hotdeal.common.facade.RedissonLockFacade;
import challenge18.hotdeal.common.security.UserDetailsImpl;
import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import challenge18.hotdeal.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final RedissonLockFacade redissonLockFacade;

    // 상품 목록 조회
    @GetMapping
    public AllProductResponseDto getProducts(ProductSearchCondition condition) {
        return productService.getProducts(condition);
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public SelectProductResponseDto getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }

    // 상품 구매
    @PostMapping("/{productId}")
    public ResponseEntity<Message> buyProduct(@PathVariable Long productId,
                                              @RequestBody Map<String, Integer> map,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return redissonLockFacade.buy("product", productId, map.get("quantity"), userDetails.getUser());
    }
}
