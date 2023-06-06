package challenge18.hotdeal.domain.product.repository;

import challenge18.hotdeal.domain.product.dto.AllProductResponseDto;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import challenge18.hotdeal.domain.product.entity.Product;
import challenge18.hotdeal.domain.product.entity.QProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static challenge18.hotdeal.domain.product.entity.QProduct.product;
import static challenge18.hotdeal.domain.purchase.entity.QPurchase.purchase;
import static java.lang.Long.valueOf;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QProduct subProduct = new QProduct("subProduct");

    @Override
    public AllProductResponseDto findAllByPriceAndCategory(ProductSearchCondition condition) {
        return getContent1(condition);

        // 석빈 쿼리 튜닝
//        return sb(condition, pageable);
    }

    //석빈 쿼리 튜닝
    private Page<AllProductResponseDto> sb(ProductSearchCondition condition, Pageable pageable) {
        List<AllProductResponseDto> content = new ArrayList<>();

        List<Long> content_id = queryFactory
                .select(product.id)
                .from(product)
                .where(
                        searchPriceCategory(condition.getMinPrice(), condition.getMaxPrice()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice()),
                        eqMainCategory(condition.getMainCategory()),
                        eqSubCategory(condition.getSubCategory())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if(CollectionUtils.isEmpty(content_id)){
//            return new PageImpl(content, pageable, content.size());
            return null;
        }

        content =  queryFactory
                .select(Projections.constructor(AllProductResponseDto.class,
                        product.productName,
                        product.price))
                .from(product)
                .where(product.id.in(content_id))
                .orderBy(product.id.desc())
                .fetch();

        return new PageImpl<>(content, pageable, content.size());
    }

    private List<AllProductResponseDto> getContent(ProductSearchCondition condition, Pageable pageable) {
        /* subQuery 사용하지 않을 경우
        SELECT product_name, price FROM products
        WHERE price_category = 1
        AND categorya = '상의'
        AND categoryb = '반소매 티셔츠'
        AND price >= 10000
        AND price <= 11000
        AND MATCH(product_name) against('여성' in boolean mode);
         */
        return queryFactory
                .select(Projections.constructor(AllProductResponseDto.class,
                        product.productName,
                        product.price))
                .from(product)
                .where(
                        searchPriceCategory(condition.getMinPrice(), condition.getMaxPrice()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice()),
                        eqMainCategory(condition.getMainCategory()),
                        eqSubCategory(condition.getSubCategory()),
                        matchKeyword(condition.getKeyword())
                )
                .offset(pageable.getOffset()) // 페이지 번호
                .limit(pageable.getPageSize()) // 페이지 사이즈
                .fetch();
    }

    private Long getTotal(ProductSearchCondition condition) {
        return queryFactory
                .select(product.id.count())
                .from(product)
                .where(
                        searchPriceCategory(condition.getMinPrice(), condition.getMaxPrice()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice()),
                        eqMainCategory(condition.getMainCategory()),
                        eqSubCategory(condition.getSubCategory()),
                        matchKeyword(condition.getKeyword())
                )
                .fetchOne();
    }

    private AllProductResponseDto getContent1(ProductSearchCondition condition) {
        /* subQuery 사용할 경우
        SELECT product_name, price FROM products
        WHERE product_id IN (
            SELECT product_id FROM products
            WHERE price_category = 1
            AND categorya = '상의'
            AND categoryb = '반소매 티셔츠'
        )
        AND MATCH(product_name) against('여성' in boolean mode);
        AND price >= 10000
        AND price <= 11000
         */
        List<SelectProductResponseDto> content = queryFactory
                .select(Projections.constructor(SelectProductResponseDto.class,
                        product.productName,
                        product.price))
                .from(product)
                .where(product.id.in(
                                JPAExpressions
                                        .select(subProduct.id)
                                        .from(subProduct)
                                        .where(
                                                searchPriceCategory(condition.getMinPrice(), condition.getMaxPrice()),
                                                eqMainCategory(condition.getMainCategory()),
                                                eqSubCategory(condition.getSubCategory())
                                        )
                        ),
                        matchKeyword(condition.getKeyword()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice())
                )
                .offset(condition.getQueryOffset()) // 조회를 시작하는 행 번호
                .limit(condition.getQueryLimit() + 1) // 조회할 행의 개수
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

    private Long getTotal1(ProductSearchCondition condition) {
        return queryFactory
                .select(product.id.count())
                .from(product)
                .where(product.id.in(
                                JPAExpressions
                                        .select(subProduct.id)
                                        .from(subProduct)
                                        .where(
                                                searchPriceCategory(condition.getMinPrice(), condition.getMaxPrice()),
                                                eqMainCategory(condition.getMainCategory()),
                                                eqSubCategory(condition.getSubCategory())
                                        )
                        ),
                        matchKeyword(condition.getKeyword()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice())
                )
                .fetchOne();
    }

    // 대분류 검색
    private BooleanExpression eqMainCategory(String searchMainCategory) {
        return searchMainCategory.equals("") ? null : product.categoryA.eq(searchMainCategory);
    }

    // 중분류 검색
    private BooleanExpression eqSubCategory(String searchSubCategory) {
        return searchSubCategory.equals("") ? null : product.categoryB.eq(searchSubCategory);
    }

    // 가격대 구간 검색 (priceCategory)
    private BooleanExpression searchPriceCategory(Long minPrice, Long maxPrice) {
        // 최고가, 최저가가 주어지지 않았을 때
        if (minPrice == null && maxPrice == null) {
            return null;
        }

        // 최고가가 주어졌는데
        if (maxPrice != null) {
            int maxPriceCategory = maxPrice.intValue() / 10000;
            // 10000원 미만일 경우
            if (maxPriceCategory == 0) {
                // where price_category in (0)
                return product.priceCategory.eq(0);
            }

            // 최저가도 주어진 경우
            if (minPrice != null) {
                int minPriceCategory = minPrice.intValue() / 10000;
                // 최고가와 최저가의 priceCategory가 같을 경우
                System.out.println("minPriceCategory = " + minPriceCategory);
                System.out.println("maxPriceCategory = " + maxPriceCategory);
                if (maxPriceCategory == minPriceCategory) {
                    // where price_category in (10)
                    return product.priceCategory.eq(minPriceCategory);
                } else {
                    // where price_category >= 0
                    // and price_category <= 10
                    return product.priceCategory.goe(minPriceCategory).and(product.priceCategory.loe(maxPriceCategory));
                }
            }

            // 최저가가 주어지지 않을 경우, 최고가만 주어진 경우
            else {
                return product.priceCategory.loe(maxPriceCategory);
            }
        }

        // 최고가가 주어지지 않은 경우, 최저가만 주어진 경우
        else {
            int minPriceCategory = minPrice.intValue() / 10000;
            return product.priceCategory.goe(minPriceCategory);
        }
    }

    //  최저가 검색
    private BooleanExpression goeMinPrice(Long minPrice) {
        return minPrice == null ? null : product.price.goe(minPrice);
    }

    // 최고가 검색
    private BooleanExpression loeMaxPrice(Long maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
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
