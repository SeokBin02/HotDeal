package challenge18.hotdeal.products;


import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class QueryTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    public void test(){
       //given
        EntityManager em = emf.createEntityManager();
        LocalDateTime start = LocalDateTime.of(2023,6,8,0,0,0);
        LocalDateTime end = LocalDateTime.of(2023,6,8,23,59,59);


        String sql = "SELECT p.product_id as product_id, p.product_name as productName, p.price, " +
                "p.amount as amount, p.categorya as categorya, p.categoryb as categoryb  " +
                "FROM purchase pr " +
                "LEFT JOIN products p on p.product_id = pr.product_product_id " +
                "WHERE pr.product_product_id IS NOT NULL " +
                "AND pr.create_at BETWEEN :start AND :end " +
//                "AND pr.create_at >= :start " +
//                "AND pr.create_at < :end " +
//                "AND DATE_FORMAT(pr.create_at, '%Y-%m-%d') = '2023-06-08' " +
                "GROUP BY p.product_id " +
                "ORDER BY SUM(pr.amount) DESC " +
                "LIMIT 20";
        
       // when
        Query nativeQuery = em.createNativeQuery(sql, "SelectProductResponseMapping");
        nativeQuery.setParameter("start", start);
        nativeQuery.setParameter("end", end);
        List<SelectProductResponseDto> productList = nativeQuery.getResultList();

        // em.close();

        //then
        assertEquals(20, productList.size());
    }
}
