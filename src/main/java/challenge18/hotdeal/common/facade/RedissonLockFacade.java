package challenge18.hotdeal.common.facade;

import challenge18.hotdeal.common.util.Message;
import challenge18.hotdeal.domain.limited.service.LimitedProductService;
import challenge18.hotdeal.domain.product.service.ProductService;
import challenge18.hotdeal.domain.user.entity.User;
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

    public ResponseEntity<Message> buy(String prefix, Long key, int quantity, User user){
        RLock lock = redissonClient.getLock(prefix + "::" + key.toString());
        try{
            boolean available = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if(!available){
                System.out.println("lock 획득 실패"); // ExceptionHandler에서 처리할 수 있는 예외 던지기
                return null;
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
}
