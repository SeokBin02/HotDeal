package challenge18.hotdeal.domain.limited.repository;

import challenge18.hotdeal.domain.limited.entity.LimitedProduct;
import challenge18.hotdeal.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LimitedProductRepository extends JpaRepository<LimitedProduct, Long>, LimitedProductRepositoryCustom {

    Optional<LimitedProduct> findById(Long limitedProductId);

    @Query(value = "select * from limited_products where id = :id for update",nativeQuery = true)
    Optional<LimitedProduct> findWithId(@Param("id") Long id);
}
