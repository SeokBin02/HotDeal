package challenge18.hotdeal.domain.purchase.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPurchase is a Querydsl query type for Purchase
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPurchase extends EntityPathBase<Purchase> {

    private static final long serialVersionUID = -1896205840L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPurchase purchase = new QPurchase("purchase");

    public final challenge18.hotdeal.common.util.QTimeStamped _super = new challenge18.hotdeal.common.util.QTimeStamped(this);

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final challenge18.hotdeal.domain.limited.entity.QLimitedProduct limitedProduct;

    public final challenge18.hotdeal.domain.product.entity.QProduct product;

    public final StringPath purchaseDate = createString("purchaseDate");

    public final challenge18.hotdeal.domain.user.entity.QUser user;

    public QPurchase(String variable) {
        this(Purchase.class, forVariable(variable), INITS);
    }

    public QPurchase(Path<? extends Purchase> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPurchase(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPurchase(PathMetadata metadata, PathInits inits) {
        this(Purchase.class, metadata, inits);
    }

    public QPurchase(Class<? extends Purchase> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.limitedProduct = inits.isInitialized("limitedProduct") ? new challenge18.hotdeal.domain.limited.entity.QLimitedProduct(forProperty("limitedProduct")) : null;
        this.product = inits.isInitialized("product") ? new challenge18.hotdeal.domain.product.entity.QProduct(forProperty("product")) : null;
        this.user = inits.isInitialized("user") ? new challenge18.hotdeal.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

