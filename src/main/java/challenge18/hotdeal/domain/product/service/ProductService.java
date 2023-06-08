package challenge18.hotdeal.domain.product.service;

import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import challenge18.hotdeal.domain.product.entity.Product;
import challenge18.hotdeal.domain.product.repository.ProductRepository;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;

    // 상품 전체 조회 (필터링)
    public AllProductResponseDto allProduct(ProductSearchCondition condition) {
        condition.setCondition(validateInput(condition));

        // 조건이 없을 경우 전날 판매 실적 기준 Top90위
        if (checkConditionNull(condition)) {
//            return purchaseRepository.findTopN(condition.getQueryLimit());
//            return purchaseRepository.findTop90();
            return null;
        }

        // 조건 필터링
        return productRepository.findAllByPriceAndCategory(condition);
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
            throw new IllegalArgumentException("주문하신 상품의 재고가 " + quantity + "개 남았습니다.");
        }

        product.buy(quantity);
        purchaseRepository.save(new Purchase(quantity, user, product, null));
        return new ResponseEntity<>(new Message("상품 구매 성공"), HttpStatus.OK);
    }

    public boolean checkConditionNull(ProductSearchCondition condition){
        if (condition.getMainCategory() == null || condition.getMainCategory().equals("")) {
            condition.setMainCategory("");
        }

        if (condition.getSubCategory() == null || condition.getSubCategory().equals("")) {
            condition.setSubCategory("");
        }

        if (condition.getKeyword() == null || condition.getKeyword().equals("")) {
            condition.setKeyword("");
        }

        if (condition.getMaxPrice() == null && condition.getMaxPrice() == null &&
                condition.getMainCategory().equals("") && condition.getSubCategory().equals("") &&
                condition.getKeyword().equals("")) {
            return true;
        }
        return false;
    }

    // 입력된 값 유효성 검사
    public ProductSearchCondition validateInput(ProductSearchCondition condition) {
        ProductSearchCondition fixedCondition = new ProductSearchCondition();
        fixedCondition.setCondition(condition);

        // minPrice가 음수일때
        if(condition.getMinPrice() != null && condition.getMinPrice() < 0){
            fixedCondition.setMinPrice(0l);
        }

        // maxPrice가 음수일때
        if(condition.getMaxPrice() != null && condition.getMaxPrice() < 0){
            fixedCondition.setMaxPrice(0l);
        }

        // minPrice가 10억 이상일 때
        if(condition.getMaxPrice() != null && condition.getMaxPrice() > 1000000000){
            fixedCondition.setMaxPrice(999999999l);
        }

        // maxPrice가 10억 이상일 때
        if(condition.getMinPrice() != null && condition.getMinPrice() > 1000000000){
            fixedCondition.setMinPrice(999999999l);
        }

        // minPrice가 maxPrice보다 클 때
        if(condition.getMinPrice() != null && condition.getMaxPrice() != null){
            if(condition.getMinPrice() > condition.getMaxPrice()){
                long temp = fixedCondition.getMinPrice();
                fixedCondition.setMinPrice(fixedCondition.getMaxPrice());
                fixedCondition.setMaxPrice(temp);
            }
        }
        return fixedCondition;
    }

    public Product checkExistProduct(Long productId){
        return productRepository.findById(productId).orElseThrow((
                () -> new IllegalArgumentException("상품이 존재하지 않습니다.")
                ));
    }
}
