package challenge18.hotdeal.domain.product.service;

import challenge18.hotdeal.common.Enum.UserRole;
import challenge18.hotdeal.common.facade.RedissonLockFacade;
import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import challenge18.hotdeal.domain.product.entity.Product;
import challenge18.hotdeal.domain.product.repository.ProductRepository;
import challenge18.hotdeal.domain.purchase.repository.PurchaseRepository;
import challenge18.hotdeal.domain.user.entity.User;
import challenge18.hotdeal.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 라이프 사이클을 Class 단위로 지정함에 따라 @BeforeAll 어노테이션이 static 이 아니어도 됌(원래는 테스트 단위)
@SpringBootTest(properties = "spring.config.location = classpath:application-test.yml") // 테스트 환경에서 사용할 설정 파일 지정
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RedissonLockFacade facadeService;

    @BeforeAll
    void setUp() {
        Product product1 = new Product("테스트 상품1", 10000, "테스트 카테고리 A", "테스트 카테고리 B", 100);
        Product product2 = new Product("테스트 상품2", 10000, "테스트 카테고리 A", "테스트 카테고리 B", 10);
        Product product3 = new Product("테스트 상품3", 10000, "테스트 카테고리 A", "테스트 카테고리 B", 0);
        Product product4 = new Product("테스트 상품4", 10000, "테스트 카테고리 A", "테스트 카테고리 B", 100);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);

        userRepository.save(new User("testUser", "testPassword", UserRole.ROLE_USER));
    }

    @AfterAll
    void clear() {
        purchaseRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getPopularProducts() { // No filter -> 인기 상품, Yes filter -> 필터링 된 결과물
        // 어떤 결과로 테스트의 통과 여부를 결정해야할지 모르겠음.
    }

    @Test
    @DisplayName("상품 상세 조회")
    @Order(1)
    void getProductDetail() {
        SelectProductResponseDto responseDto = productService.getProductDetail(1L);
        assertEquals( "테스트 상품1", responseDto.getProductName());
        assertEquals(10000,responseDto.getPrice());
        assertEquals("테스트 카테고리 A", responseDto.getCategoryA());
        assertEquals("테스트 카테고리 B", responseDto.getCategoryB());
        assertEquals(100, responseDto.getAmount());
    }

    @Test
    @DisplayName("상품 구매")
    @Order(2)
    void buyProduct() {
        // given
        Long productId = 1L;
        int quantity = 10;
        User user = userRepository.findById("testUser").orElseThrow();

        // when
        ResponseEntity<Message> result = productService.buyProduct(productId, quantity, user);

        // then
        Product product = productRepository.findById(productId).orElseThrow();
        assertEquals("상품 구매 성공", result.getBody().getMsg());
        assertEquals(90, product.getAmount());
    }

    @Test
    @DisplayName("상품 구매 - 동시성 문제 테스트")
    @Order(3)
    void buyProductConcurrencyTest() throws InterruptedException {
        // given
        int count = 100;

        List<User> testUsers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            User user = new User("testUser" + i, "password", UserRole.ROLE_USER);
            userRepository.saveAndFlush(user);
            testUsers.add(user);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);

        // when
        for (User user : testUsers) {
            executorService.submit(() -> {
                try {
                    facadeService.buy("product", 4L, 1, user);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Product testProduct = productRepository.findById(4L).orElseThrow();
        assertEquals(0, testProduct.getAmount());
    }

    @Nested
    class Fail {
        @Test
        @DisplayName("상품 상세 조회 - 존재하지 않는 상품")
        @Order(1)
        void getProductDetailByWrongId() {
            // given
            Long productId = 0L;

            // when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.getProductDetail(productId));
            assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("상품 구매 - 비로그인")
        @Order(2)
        void buyProductWithOutLogin() {
            // given
            Long productId = 1L;
            int quantity = 10;
            User user = null;

            // when
            ResponseEntity<Message> result = productService.buyProduct(productId, quantity, user);

            // then
            assertEquals("로그인이 필요합니다.", result.getBody().getMsg());
        }

        @Test
        @DisplayName("상품 구매 - 존재하지 않는 상품")
        @Order(3)
        void buyProductByWrongId() {
            // given
            Long productId = 0L;
            int quantity = 10;
            User user = userRepository.findById("testUser").orElseThrow();

            // when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.buyProduct(productId, quantity, user));
            assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("상품 구매 - 재고가 없는 상품")
        @Order(4)
        void buyZeroAmountProduct() {
            // given
            Long productId = 3L;
            int quantity = 10;
            User user = userRepository.findById("testUser").orElseThrow();

            // when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.buyProduct(productId, quantity, user));
            assertEquals("상품 재고가 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("상품 구매 - 재고가 있지만 부족한 상품")
        @Order(5)
        void buyProductOverStock() {
            // given
            Long productId = 2L;
            int quantity = 1000;
            User user = userRepository.findById("testUser").orElseThrow();
            Product product = productRepository.findById(productId).orElseThrow();

            // when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.buyProduct(productId, quantity, user));
            assertEquals("주문하신 상품의 재고가 " + product.getAmount() + "개 남았습니다.", exception.getMessage());
        }
    }
}
