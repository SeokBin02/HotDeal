package challenge18.hotdeal.domain.limited.service;

import challenge18.hotdeal.common.Enum.UserRole;
import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.limited.dto.LimitedProductRequestDto;
import challenge18.hotdeal.domain.limited.entity.LimitedProduct;
import challenge18.hotdeal.domain.limited.repository.LimitedProductRepository;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import challenge18.hotdeal.domain.product.entity.Product;
import challenge18.hotdeal.domain.product.repository.ProductRepository;
import challenge18.hotdeal.domain.purchase.repository.PurchaseRepository;
import challenge18.hotdeal.domain.user.entity.User;
import challenge18.hotdeal.domain.user.repository.UserRepository;
import org.apache.coyote.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location = classpath:application-test.yml")
class LimitedProductServiceTest {

    @Autowired
    LimitedProductService limitedProductService;
    @Autowired
    LimitedProductRepository limitedProductRepository;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeAll
    void beforeAll(){
        LimitedProduct limitedProduct1 = new LimitedProduct(new LimitedProductRequestDto("테스트 한정상품1", 1000, "테스트 카테고리 A", "테스트 카테고리 B", 100));
        LimitedProduct limitedProduct2 = new LimitedProduct(new LimitedProductRequestDto("테스트 한정상품2", 1500, "테스트 카테고리 A", "테스트 카테고리 B", 10));
        LimitedProduct limitedProduct3 = new LimitedProduct(new LimitedProductRequestDto("테스트 한정상품3", 1700, "테스트 카테고리 A", "테스트 카테고리 B", 0));

        limitedProductRepository.save(limitedProduct1);
        limitedProductRepository.save(limitedProduct2);
        limitedProductRepository.save(limitedProduct3);

        userRepository.save(new User("testUser", "1234", UserRole.ROLE_USER));
    }

    @AfterAll
    void afterAll(){
        purchaseRepository.deleteAll();
        limitedProductRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Disabled
    @DisplayName("한정판 상품 등록")
    void registerLimitedProduct() {
        userRepository.save(new User("adminUser", "1234", UserRole.ROLE_ADMIN));
        User user = userRepository.findById("adminUser").orElseThrow();
        ResponseEntity<Message> registerLimitedProduct = limitedProductService.registerLimitedProduct(new LimitedProductRequestDto("관리자 테스트 한정상품4", 1000, "테스트 카테고리 A", "테스트 카테고리 B", 100), user);

        assertEquals("한정판 상품 등록 성공", registerLimitedProduct.getBody().getMsg());
    }

    @Test
    @Disabled
    @DisplayName("한정판 상품 전체 조회")
    void getLimitedProducts() {

    }

    @Test
    @DisplayName("한정판 상품 상세 조회")
    void getLimitedProductDetail() {
        SelectProductResponseDto selectProductResponseDto = limitedProductService.getLimitedProductDetail(1L);
        assertEquals("테스트 한정상품1", selectProductResponseDto.getProductName());
        assertEquals(1000, selectProductResponseDto.getPrice());
        assertEquals("테스트 카테고리 A", selectProductResponseDto.getCategoryA());
        assertEquals("테스트 카테고리 B", selectProductResponseDto.getCategoryB());
        assertEquals(100, selectProductResponseDto.getAmount());
    }

    @Test
    @DisplayName("한정판 상품 구매")
    void buyLimitedProduct() {
        Long limitedProductId = 1L;
        User user = userRepository.findById("testUser").orElseThrow();

        ResponseEntity<Message> result = limitedProductService.buyLimitedProduct(limitedProductId, user);

        LimitedProduct limitedProduct = limitedProductRepository.findById(limitedProductId).orElseThrow();
        assertEquals("한정판 상품 구매 성공", result.getBody().getMsg());
        assertEquals(99, limitedProduct.getAmount());
    }

    @Nested
    class Exception{

        @Test
        @DisplayName("한정 상품 구매 - 비로그인")
        void buyLimitedProductNotLogin(){
            Long limitedProductId = 1L;
            User user = null;

            ResponseEntity<Message> result = limitedProductService.buyLimitedProduct(limitedProductId, user);

            assertEquals("로그인이 필요합니다.", result.getBody().getMsg());
        }

        @Test
        @DisplayName("한정 상품 구매 - 존재하지 않는 한정 상품")
        void buyLimitedProductNotExist(){
            Long limitedProductId = 0L;
            User user = userRepository.findById("testUser").orElseThrow();

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> limitedProductService.buyLimitedProduct(limitedProductId, user));
            assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("한정 상품 구매 - 재고가 없는 한정 상품")
        void buyLimitedProductNotAmount(){
            Long limitedProductId = 3L;
            User user = userRepository.findById("testUser").orElseThrow();

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> limitedProductService.buyLimitedProduct(limitedProductId, user));
            assertEquals("상품 재고가 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("한정 상품 등록 - 관리자가 아닌 유저가 한정 상품 등록")
        void registerLimitedProductNotAdmin(){
            User user = userRepository.findById("testUser").orElseThrow();

            ResponseEntity<Message> result = limitedProductService.registerLimitedProduct(new LimitedProductRequestDto("관리자 테스트 한정상품4", 1000, "테스트 카테고리 A", "테스트 카테고리 B", 100), user);
            assertEquals("관리자가 아닙니다.", result.getBody().getMsg());
        }
    }
}