package challenge18.hotdeal;

import challenge18.hotdeal.common.Enum.UserRole;
import challenge18.hotdeal.domain.limited.dto.LimitedProductRequestDto;
import challenge18.hotdeal.domain.limited.entity.LimitedProduct;
import challenge18.hotdeal.domain.limited.repository.LimitedProductRepository;
import challenge18.hotdeal.domain.limited.service.LimitedProductService;
import challenge18.hotdeal.domain.user.dto.SignupRequest;
import challenge18.hotdeal.domain.user.entity.User;
import challenge18.hotdeal.domain.user.repository.UserRepository;
import challenge18.hotdeal.domain.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(properties = "spring.config.location = classpath:application-test.yml")
public class HotDealApplicationTests {

    @Autowired
    LimitedProductRepository limitedProductRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private LimitedProductService limitedProductService;
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }



    @Test
    @DisplayName("회원가입 동시성 테스트")
    void signupConcurrencyTests() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);
        List<Integer> users = new ArrayList<>();

        // when
        for (int i = 0; i < 100; i++) {
            final int j = i;
            SignupRequest requestDto = new SignupRequest("testId", "testPassword" + i, false, "");
            executorService.execute(() -> {
                try {
                    userService.signup(requestDto);
                    users.add(j);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Assertions.assertEquals(1, users.size());
    }
}
