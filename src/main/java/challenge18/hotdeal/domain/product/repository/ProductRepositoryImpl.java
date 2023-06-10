package challenge18.hotdeal.domain.product.repository;

import challenge18.hotdeal.domain.product.dto.*;
import challenge18.hotdeal.domain.product.entity.QProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static challenge18.hotdeal.domain.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public AllProductResponseDto findAllbyCondition(ProductSearchCondition condition) {

        List<SelectProductResponseDto> content = queryFactory
                .select(Projections.constructor(SelectProductResponseDto.class,
                        product.id,
                        product.productName,
                        product.price
                ))
                .from(product)
                .where(
                        goeProductId(condition.getQueryIndex()),
                        eqMainCategory(condition.getMainCategory()),
                        eqSubCategory(condition.getSubCategory()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice()),
                        matchKeyword(condition.getKeyword())
                )
                .orderBy(product.id.asc())
                .limit(condition.getQueryLimit() + 1) // 페이지 사이즈
                .fetch();
        boolean next; // 다음 페이지 유(true)/무(false)

        // 다음 페이지가 있으면
        if (content.size() == (condition.getQueryLimit() + 1)) {
            next = true;
            content.remove(content.size() - 1);
        } else {
            next = false;
        }
        return new AllProductResponseDto(content, next);
    }

    // no-offset, product_id 인덱싱
    private BooleanExpression goeProductId(Long id) {
        return id == 0 ? null : product.id.goe(id);
    }

    // 대분류 검색
    private BooleanExpression eqMainCategory(String searchMainCategory) {
        return searchMainCategory.equals("") ? null : product.categoryA.eq(searchMainCategory);
    }

    // 중분류 검색
    private BooleanExpression eqSubCategory(String searchSubCategory) {
        return searchSubCategory.equals("") ? null : product.categoryB.eq(searchSubCategory);
    }

    //  최저가 검색
    private BooleanExpression goeMinPrice(Long minPrice) {
        return minPrice == null ? null : product.price.goe(minPrice);
    }

    // 최고가 검색
    private BooleanExpression loeMaxPrice(Long maxPrice) {
        return maxPrice == null ? null : product.price.loe(maxPrice);
    }

    // 키워드 검색
    private BooleanExpression matchKeyword(String keyword) {
        if (keyword.equals("")) {
            return null;
        }
        NumberTemplate booleanTemplate = Expressions.numberTemplate(Double.class,
                "function('match', {0}, {1})", product.productName, "+" + keyword + "*");
        return booleanTemplate.gt(0);
    }
}
