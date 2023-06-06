package challenge18.hotdeal;

import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.repository.ProductRepository;
import challenge18.hotdeal.domain.product.service.ProductService;
import challenge18.hotdeal.domain.user.dto.LoginRequest;
import challenge18.hotdeal.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class HotDealFilterSearchTest {
    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;

//    @Test
//    @DisplayName("로그인 처리시간 테스트")
//    void test1(){
//        //given
//        LoginRequest loginRequest = new LoginRequest("00008fa2-883d-4ed1-aefe-b53266eee847", "cf424df4-8c96-40e8-b4ab-ea81488bea26");
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        userRepository.findByUserId(loginRequest.getUserId());
//        stopWatch.stop();
//
//        System.out.println(stopWatch.prettyPrint());
//    }

//    @Test
//    @DisplayName("응답시간 테스트")
//    void test2(){
//        List<AllProductResponseDto> productList = new ArrayList<>();
//        StopWatch stopWatch = new StopWatch();
//        ProductSearchCondition condition = new ProductSearchCondition(null, 1000l, "상의", "반소매 티셔츠");
//        Pageable pageable = Pageable.ofSize(10);
//
//
//        stopWatch.start();
//        productRepository.findAllByPriceAndCategory(condition, pageable);
//        stopWatch.stop();
//
//        System.out.println(stopWatch.prettyPrint());
//    }


}
