package challenge18.hotdeal.domain.purchase.repository;

import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static challenge18.hotdeal.common.config.Redis.RedisCacheKey.PRODUCT;
import static challenge18.hotdeal.domain.product.entity.QProduct.product;
import static challenge18.hotdeal.domain.purchase.entity.QPurchase.purchase;

@Repository
@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRespositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    @Cacheable(value = PRODUCT, cacheManager = "redisCacheManager")
    public AllProductResponseDto findTopN(int queryLimit) {
        List<SelectProductResponseDto> content = queryFactory
                .select(Projections.constructor(SelectProductResponseDto.class,
                        product.productName,
                        product.price))
                .from(purchase)
                .leftJoin(purchase.product, product)
                .where(purchase.product.isNotNull())
                .groupBy(purchase.product)
                .orderBy(purchase.amount.sum().desc())
                .offset(0) // 페이지 번호
                .limit(queryLimit)
                .fetch();
        return new AllProductResponseDto(content, false);
    }
}



