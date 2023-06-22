package challenge18.hotdeal.domain.purchase.repository;

import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static challenge18.hotdeal.common.config.Redis.RedisCacheKey.POPULAR_ITEM;
import static challenge18.hotdeal.common.config.Redis.RedisCacheKey.USER;
import static challenge18.hotdeal.domain.product.entity.QProduct.product;
import static challenge18.hotdeal.domain.purchase.entity.QPurchase.purchase;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRespositoryCustom {
    @Autowired
    EntityManagerFactory emf;

    @Override
    @Cacheable(cacheNames = POPULAR_ITEM, cacheManager = "redisCacheManager", key = "#today")
    public List<SelectProductResponseDto> findTopN(LocalDate today) {
        String yesterday = today.minusDays(1L).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        EntityManager em = emf.createEntityManager();
        String sql = "SELECT p.product_id as product_id, p.product_name as productName, p.price, " +
                "p.amount as amount, p.categorya as categorya, p.categoryb as categoryb  " +
                "FROM purchase pr " +
                "LEFT JOIN products p on p.product_id = pr.product_product_id " +
                "WHERE pr.product_product_id IS NOT NULL " +
                "AND pr.purchase_date = :yesterday " +
                "GROUP BY p.product_id " +
                "ORDER BY SUM(pr.amount) DESC " +
                "LIMIT 20";

        Query nativeQuery = em.createNativeQuery(sql, "SelectProductResponseMapping");
        nativeQuery.setParameter("yesterday", yesterday);
        List<SelectProductResponseDto> productList = nativeQuery.getResultList();
        return productList;
    }
}



