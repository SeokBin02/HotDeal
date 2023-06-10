package challenge18.hotdeal.domain.purchase.repository;

import challenge18.hotdeal.domain.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, PurchaseRespositoryCustom{

}
