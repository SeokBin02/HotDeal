package challenge18.hotdeal.domain.product.service;

import challenge18.hotdeal.common.util.ConditionValidate;
import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.product.dto.*;
import challenge18.hotdeal.domain.product.entity.Product;
import challenge18.hotdeal.domain.product.repository.ProductRepository;
import challenge18.hotdeal.domain.purchase.entity.Purchase;
import challenge18.hotdeal.domain.purchase.repository.PurchaseRepository;
import challenge18.hotdeal.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService extends ConditionValidate {

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;

    // 상품 전체 조회 (필터링)
    public AllProductResponseDto allProduct(ProductSearchCondition condition) {
        condition.setCondition(validateInput(condition));

        // 조건이 없을 경우 전날 판매 실적 기준 TopN
        if (checkConditionNull(condition)) {
            Date now = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String today = simpleDateFormat.format(now);

            int year = now.getYear();
            int month = now.getMonth();
            int day = now.getDay()-1;
            // 전날 기준
            LocalDateTime start = LocalDateTime.of(year, month, day, 0, 0, 0);
            LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59, 59);

            // test용
//            LocalDateTime start = LocalDateTime.of(2023, 6, 8, 0, 0, 0);
//            LocalDateTime end = LocalDateTime.of(2023, 6, 8, 23, 59, 59);

            return new AllProductResponseDto(purchaseRepository.findTopN(today, start, end), false);

        }
        // 조건 필터링
        return productRepository.findAllbyCondition(condition);
    }

    //상품 상세 조회
    public SelectProductResponseDto selectProduct(Long productId){
        Product product = checkExistProduct(productId);
        return new SelectProductResponseDto(product);
    }

    // 상품 구매
    @Transactional(readOnly = false)
    public ResponseEntity<Message> buyProduct(Long productId, int quantity, User user) {
        if (user == null) {
            return new ResponseEntity<>(new Message("로그인이 필요합니다."), HttpStatus.BAD_REQUEST);
        }

        Product product = checkExistProduct(productId);

        if (product.getAmount() <= 0) {
            throw new IllegalArgumentException("상품 재고가 없습니다.");
        } else if ((product.getAmount() > 0) && (product.getAmount() < quantity)) {
            throw new IllegalArgumentException("주문하신 상품의 재고가 " + product.getAmount() + "개 남았습니다.");
        }

        product.buy(quantity);
        purchaseRepository.save(new Purchase(quantity, user, product, null));
        return new ResponseEntity<>(new Message("상품 구매 성공"), HttpStatus.OK);
    }

    public Product checkExistProduct(Long productId){
        return productRepository.findById(productId).orElseThrow((
                () -> new IllegalArgumentException("상품이 존재하지 않습니다.")
                ));
    }
}
