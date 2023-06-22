package challenge18.hotdeal.domain.purchase.entity;

import challenge18.hotdeal.common.util.TimeStamped;
import challenge18.hotdeal.domain.limited.entity.LimitedProduct;
import challenge18.hotdeal.domain.user.entity.User;
import challenge18.hotdeal.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "purchase")
public class Purchase extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    @ManyToOne
    private LimitedProduct limitedProduct;

    private String purchaseDate;

    public Purchase(int amount, User user, Product product) {
        this.amount = amount;
        this.user = user;
        this.product = product;
    }

    public Purchase(int amount, User user, LimitedProduct limitedProduct) {
        this.amount = amount;
        this.user = user;
        this.limitedProduct = limitedProduct;
    }

    @PostPersist
    private void setDate(){
        LocalDateTime createAt = super.getCreateAt();
        if( createAt != null ){
            this.purchaseDate = createAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
    }
}
