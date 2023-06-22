package challenge18.hotdeal.domain.limited.service;

import challenge18.hotdeal.common.Enum.UserRole;
import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.limited.dto.LimitedProductRequestDto;
import challenge18.hotdeal.domain.limited.entity.LimitedProduct;
import challenge18.hotdeal.domain.limited.repository.LimitedProductRepository;
import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import challenge18.hotdeal.domain.purchase.entity.Purchase;
import challenge18.hotdeal.domain.purchase.repository.PurchaseRepository;
import challenge18.hotdeal.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LimitedProductService {

    private final LimitedProductRepository limitedProductRepository;
    private final PurchaseRepository purchaseRepository;

    // 한정판 상품 등록
    @Transactional(readOnly = false)
    public ResponseEntity<Message> registerLimitedProduct(LimitedProductRequestDto requestDto, User user) {
        if (user.getRole() == UserRole.ROLE_USER) {
            return new ResponseEntity<>(new Message("관리자가 아닙니다."), HttpStatus.BAD_REQUEST);
        }
        limitedProductRepository.save(new LimitedProduct(requestDto));
        return new ResponseEntity<>(new Message("한정판 상품 등록 성공"), HttpStatus.OK);
    }

    // 한정판 상품 전체 조회
    public AllProductResponseDto getLimitedProducts(ProductSearchCondition condition) {
        // 조건이 없을 경우 모든 한정판 상품 조회
        if (condition.isEmpty()) {
            return limitedProductRepository.customFindAll(condition);
        }

        return limitedProductRepository.findAllByCondition(condition);
    }

    // 한정판 상품 상세 조회
    public SelectProductResponseDto getLimitedProductDetail(Long limitedProductId) {
        LimitedProduct limitedProduct = findLimitedProductById(limitedProductId);
        return new SelectProductResponseDto(limitedProduct);
    }

    // 한정판 상품 구매
    @Transactional(readOnly = false)
    public ResponseEntity<Message> buyLimitedProduct(Long limitedProductId, User user) {
        if (user == null) {
            return new ResponseEntity<>(new Message("로그인이 필요합니다."), HttpStatus.BAD_REQUEST);
        }

        LimitedProduct limitedProduct = findLimitedProductById(limitedProductId);

        if (limitedProduct.getAmount() <= 0) {
            throw new IllegalArgumentException("상품 재고가 없습니다.");
        }

        limitedProduct.buy();
        purchaseRepository.save(new Purchase(1, user, limitedProduct));
        return new ResponseEntity<>(new Message("한정판 상품 구매 성공"), HttpStatus.OK);
    }

    public LimitedProduct findLimitedProductById(Long limitedProductId) {
        return limitedProductRepository.findById(limitedProductId).orElseThrow(
                () -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    }
}
