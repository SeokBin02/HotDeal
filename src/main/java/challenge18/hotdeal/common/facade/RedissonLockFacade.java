package challenge18.hotdeal.common.facade;

import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.limited.service.LimitedProductService;
import challenge18.hotdeal.domain.product.service.ProductService;
import challenge18.hotdeal.domain.user.dto.SignupRequest;
import challenge18.hotdeal.domain.user.entity.User;
import challenge18.hotdeal.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockFacade {
    private final RedissonClient redissonClient;
    private final LimitedProductService limitedProductService;
    private final ProductService productService;
    private final UserService userService;

    public ResponseEntity<Message> buy(String prefix, Long key, int quantity, User user){
        RLock lock = redissonClient.getLock(prefix + "::" + key.toString());
        try{
            boolean available = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if(!available){
                throw new RuntimeException("lock 획득 실패");
            }

            if (prefix.equals("limitedProduct")){
                return limitedProductService.buyLimitedProduct(key, user);
            } else{
                return productService.buyProduct(key, quantity, user);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e); // ExceptionHandler에서 처리할 수 있는 예외던지기
        } finally {
            lock.unlock();
        }
    }
    
    public ResponseEntity<Message> signup(SignupRequest request) {
        RLock lock = redissonClient.getLock(request.getUserId());
        try{
            boolean available = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if(!available){
                throw new RuntimeException("lock 획득 실패");
            }

            return userService.signup(request);
        } catch (Exception e) {
            throw new RuntimeException(e); // ExceptionHandler 에서 처리할 수 있는 예외던지기
        } finally {
            lock.unlock();
        }
    }
}
