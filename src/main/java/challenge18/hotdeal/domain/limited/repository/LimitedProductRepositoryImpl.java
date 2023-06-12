package challenge18.hotdeal.domain.limited.repository;

import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static challenge18.hotdeal.domain.limited.entity.QLimitedProduct.limitedProduct;

@Repository
@RequiredArgsConstructor
public class LimitedProductRepositoryImpl implements LimitedProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public AllProductResponseDto customFindAll(ProductSearchCondition condition) {
        List<SelectProductResponseDto> content = queryFactory
                .select(Projections.constructor(SelectProductResponseDto.class,
                        limitedProduct.id,
                        limitedProduct.productName,
                        limitedProduct.price))
                .from(limitedProduct)
                .limit(condition.getQueryLimit() + 1)
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

    @Override
    public AllProductResponseDto findAllByCondition(ProductSearchCondition condition) {
        List<SelectProductResponseDto> content = queryFactory
                .select(Projections.constructor(SelectProductResponseDto.class,
                        limitedProduct.id,
                        limitedProduct.productName,
                        limitedProduct.price))
                .from(limitedProduct)
                .where(
                        goeLimitedProductId(condition.getQueryIndex()),
                        eqMainCategory(condition.getMainCategory()),
                        eqSubCategory(condition.getSubCategory()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice()),
                        matchKeyword(condition.getKeyword())
                )
                .orderBy(limitedProduct.id.asc())
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
    private BooleanExpression goeLimitedProductId(Long id) {
        return id == 0 ? null : limitedProduct.id.goe(id);
    }

    // 대분류 검색
    private BooleanExpression eqMainCategory(String searchMainCategory) {
        return searchMainCategory.equals("") ? null : limitedProduct.categoryA.eq(searchMainCategory);
    }

    // 중분류 검색
    private BooleanExpression eqSubCategory(String searchSubCategory) {
        return searchSubCategory.equals("") ? null : limitedProduct.categoryB.eq(searchSubCategory);
    }

    //  최저가 검색
    private BooleanExpression goeMinPrice(Long minPrice) {
        return minPrice == null ? null : limitedProduct.price.goe(minPrice);
    }

    // 최고가 검색
    private BooleanExpression loeMaxPrice(Long maxPrice) {
        return maxPrice == null ? null : limitedProduct.price.loe(maxPrice);
    }

    // 키워드 검색
    private BooleanExpression matchKeyword(String keyword) {
        if (keyword.equals("")) {
            return null;
        }
        NumberTemplate booleanTemplate = Expressions.numberTemplate(Double.class,
                "function('match', {0}, {1})", limitedProduct.productName, "+" + keyword + "*");
        return booleanTemplate.gt(0);
    }
}
