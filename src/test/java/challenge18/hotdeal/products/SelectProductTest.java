//package challenge18.hotdeal.products;
//
//import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
//import challenge18.hotdeal.domain.product.entity.Product;
//import challenge18.hotdeal.domain.product.repository.ProductRepository;
//import challenge18.hotdeal.domain.product.service.ProductService;
//import challenge18.hotdeal.domain.purchase.repository.PurchaseRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Random;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest(properties = "spring.config.location = classpath:application-test.yml")
//public class SelectProductTest {
//    @Autowired
//    ProductService productService;
//    @Autowired
//    ProductRepository productRepository;
//    @Autowired
//    PurchaseRepository purchaseRepository;
//
//    @Test
//    @DisplayName("동작 테스트")
//    void test1() {
//        // given
//        init();
//        long productId = 753;
//
//        // when
//        SelectProductResponseDto responseDto = productService.getProductDetail(productId);
//
//        // then
//        assertEquals("상의", responseDto.getCategoryA());
//        assertEquals("반소매 티셔츠", responseDto.getCategoryB());
//    }
//
//    @Test
//    @DisplayName("Exception Test")
//    void test2() {
//        // given
//        init();
//        long productId = 5555;
//
//        // when
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.findProductById(productId));
//
//        // then
//        assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
//    }
//
//    private void init() {
//        Random rand = new Random(System.currentTimeMillis());
//
//        String productName = "하우스 메이드 포켓 반팔티 블랙";
//        String name_index;
//        final String categoryA = "상의";
//        final String categoryB = "반소매 티셔츠";
//        int amount;
//        int price;
//
//        for (int i = 1; i <= 1000; i++) {
//            name_index = Integer.toString(i);
//            price = (rand.nextInt(35) + 1) * 1000;
//            amount = rand.nextInt(1000) + 1;
//            productRepository.saveAndFlush(new Product(productName + name_index, price, categoryA, categoryB, amount));
//        }
//    }
//}
