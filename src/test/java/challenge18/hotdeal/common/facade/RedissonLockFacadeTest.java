//package challenge18.hotdeal.common.facade;
//
//import challenge18.hotdeal.common.Enum.UserRole;
//import challenge18.hotdeal.domain.limited.dto.LimitedProductRequestDto;
//import challenge18.hotdeal.domain.limited.entity.LimitedProduct;
//import challenge18.hotdeal.domain.limited.repository.LimitedProductRepository;
//import challenge18.hotdeal.domain.product.entity.Product;
//import challenge18.hotdeal.domain.product.repository.ProductRepository;
//import challenge18.hotdeal.domain.product.service.ProductService;
//import challenge18.hotdeal.domain.user.repository.UserRepository;
//import challenge18.hotdeal.domain.user.entity.User;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(properties = "spring.config.location = classpath:application-test.yml")
//class RedissonLockFacadeTest {
//
//    @Autowired
//    private RedissonLockFacade redissonLockFacade;
//    @Autowired
//    private LimitedProductRepository limitedProductRepository;
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    @DisplayName("한정판 상품 구매 동시성 문제 테스트")
//    void concurrencyTest() throws InterruptedException {
//        // given
//        int count = 1960;
//        LimitedProduct limitedProduct = new LimitedProduct(
//                new LimitedProductRequestDto("에어조던1 시카고 OG", 280000, "신발", "스니커즈", count)
//        );
//        limitedProductRepository.saveAndFlush(limitedProduct);
//
//        List<User> testUsers = new ArrayList<>();
//        for (int i = 1; i <= count; i++) {
//            User user = new User("testUser" + i, "password", UserRole.ROLE_USER);
//            userRepository.saveAndFlush(user);
//            testUsers.add(user);
//        }
//
//        ExecutorService executorService = Executors.newFixedThreadPool(count);
//        CountDownLatch latch = new CountDownLatch(count);
//
//        // when
//        for (User user : testUsers) {
//            executorService.submit(() -> {
//                try {
//                    redissonLockFacade.buy("limitedProduct", 1L, 1, user);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        // then
//        LimitedProduct product = limitedProductRepository.findById(1L).orElseThrow();
//        assertEquals(0, product.getAmount());
//    }
//
//    @Test
//    @DisplayName("일반 상품 구매 동시성 문제 테스트")
//    void concurrencyTest2() throws InterruptedException {
//        // given
//        int count = 1960;
//        Product product = new Product("테스트 상품1", 20000, "테스트 카테고리", "테스트 서브 카테고리", count);
//        productRepository.saveAndFlush(product);
//
//        List<User> testUsers = new ArrayList<>();
//        for (int i = 1; i <= count; i++) {
//            User user = new User("testUser" + i, "password", UserRole.ROLE_USER);
//            userRepository.saveAndFlush(user);
//            testUsers.add(user);
//        }
//
//        ExecutorService executorService = Executors.newFixedThreadPool(count);
//        CountDownLatch latch = new CountDownLatch(count);
//
//        // when
//        for (User user : testUsers) {
//            executorService.submit(() -> {
//                try {
//                    redissonLockFacade.buy("product", 1L, 1, user);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        // then
//        Product testProduct = productRepository.findById(1L).orElseThrow();
//        assertEquals(0, testProduct.getAmount());
//    }
//}