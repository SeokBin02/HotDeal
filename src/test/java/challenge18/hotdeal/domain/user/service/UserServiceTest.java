package challenge18.hotdeal.domain.user.service;

import challenge18.hotdeal.common.Enum.UserRole;
import challenge18.hotdeal.common.facade.RedissonLockFacade;
import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.user.dto.LoginRequest;
import challenge18.hotdeal.domain.user.dto.SignupRequest;
import challenge18.hotdeal.domain.user.entity.User;
import challenge18.hotdeal.domain.user.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(properties = "spring.config.location = classpath:application-test.yml")
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Mock
    HttpServletResponse response;
    @Autowired
    RedissonLockFacade facade;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("유저 회원가입")
    void userSignup() {
        // given
        String userId = "testUser";
        String password = "testPassword";
        boolean isAdmin = false;
        String adminToken = "";

        SignupRequest request = new SignupRequest(userId, password, isAdmin, adminToken);

        // when
        ResponseEntity<Message> result = userService.signup(request);

        // then
        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());
    }

    @Test
    @Order(2)
    @DisplayName("관리자 회원가입")
    void adminSignup() {
        // given
        String userId = "testAdmin";
        String password = "testPassword";
        boolean isAdmin = true;
        String adminToken = "testToken";

        SignupRequest request = new SignupRequest(userId, password, isAdmin, adminToken);

        // when
        ResponseEntity<Message> result = userService.signup(request);

        // then
        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());
    }

    @Test
    @Order(3)
    @DisplayName("로그인")
    void login() {
        // given
        String userId = "testUser";
        String password = "testPassword";

        LoginRequest request = new LoginRequest(userId, password);
        userRepository.save(new User(userId, password, UserRole.ROLE_USER));
        // when
        ResponseEntity<Message> result = userService.login(request, response);

        // then
        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
    }

    @Test
    @Order(4)
    @DisplayName("회원 가입 - 동일한 ID로 동시 요청")
    void signupConcurrencyTest() throws InterruptedException { // 해당 테스트만 실행하면 통과, 테스트 클래스를 통해 전체를 실행하여 통과 X
        // given
        String userId = "testUser123";
        String password = "testPassword";
        boolean isAdmin = false;
        String adminToken = "";

        SignupRequest request = new SignupRequest(userId, password, isAdmin, adminToken);

        int count = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);

        List<ResponseEntity<Message>> results = new ArrayList<>();
        // when
        for (int i = 1; i <= count; i++) {
            executorService.execute(() -> {
                try {
                    ResponseEntity<Message> result = facade.signup(request);
                    results.add(result);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertEquals(1, results.size());
    }

    @Nested
    class Fail {
        @Test
        @Order(1)
        @DisplayName("회원가입 - 중복 아이디")
        void signupByDuplicatedId() {
            // given
            String userId = "testUser";
            String password = "testPassword";
            boolean isAdmin = false;
            String adminToken = "";

            SignupRequest request = new SignupRequest(userId, password, isAdmin, adminToken);
            userRepository.save(new User(userId, password, UserRole.ROLE_USER));

            // when - then
            DuplicateRequestException exception = assertThrows(DuplicateRequestException.class, () -> userService.signup(request));
            assertEquals("중복된 회원이 이미 존재합니다.", exception.getMessage());
        }

        @Test
        @Order(2)
        @DisplayName("관리자 회원가입 - 잘못된 어드민 토큰")
        void adminSignupWithWrongToken() {
            // given
            String userId = "testAdmin2";
            String password = "testPassword";
            boolean isAdmin = true;
            String adminToken = "WrongToken";

            SignupRequest request = new SignupRequest(userId, password, isAdmin, adminToken);

            // when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(request));
            assertEquals("ADMINTOKEN이 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @Order(3)
        @DisplayName("로그인 - 존재하지 않는 회원")
        void loginWithWrongId() {
            // given
            String userId = "UnknownUser";
            String password = "testPassword";

            LoginRequest request = new LoginRequest(userId, password);

            // when - then
            NullPointerException exception = assertThrows(NullPointerException.class, () -> userService.login(request, response));
            assertEquals("입력하신 회원정보가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @Order(4)
        @DisplayName("로그인 - 잘못된 비밀번호")
        void loginWithWrongPassword() {
            // given
            String userId = "testUser";
            String password = "WrongPassword";
            LoginRequest request = new LoginRequest(userId, password);
            userRepository.save(new User(userId, "testPassword", UserRole.ROLE_USER));

            // when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.login(request, response));
            assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        }
    }
}
