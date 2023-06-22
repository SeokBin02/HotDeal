package challenge18.hotdeal.domain.product.repository;

import challenge18.hotdeal.domain.product.document.ProductDocument;
import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductElasticSearchRepository {

    private final ElasticsearchOperations operations;

    public Page<SelectProductResponseDto> findByCondition(ProductSearchCondition condition, Pageable pageable) {
        CriteriaQuery query = createConditionCriteriaQuery(condition).setPageable(pageable);

        SearchHits<ProductDocument> search = operations.search(query, ProductDocument.class);

        List<SelectProductResponseDto> content = search.stream()
                .map(SearchHit::getContent)
                .map(SelectProductResponseDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, search.getTotalHits());
    }

    private CriteriaQuery createConditionCriteriaQuery(ProductSearchCondition condition) {
        CriteriaQuery query = new CriteriaQuery(new Criteria());

        // 키워드 검색
        if (StringUtils.hasText(condition.getKeyword())) {
            query.addCriteria(Criteria.where("product_name").matches(condition.getKeyword()));
        }

        // 대분류 검색
        if (StringUtils.hasText(condition.getMainCategory())) {
            query.addCriteria(Criteria.where("categorya").is(condition.getMainCategory()));
        }

        // 중분류 검색
        if (StringUtils.hasText(condition.getSubCategory())) {
            query.addCriteria(Criteria.where("categoryb").is(condition.getSubCategory()));
        }

        // 최저가 검색
        if (condition.getMinPrice() != null) {
            query.addCriteria(Criteria.where("price").greaterThanEqual(condition.getMinPrice()));
        }

        // 최고가 검색
        if (condition.getMaxPrice() != null) {
            query.addCriteria(Criteria.where("price").lessThanEqual(condition.getMaxPrice()));
        }

        return query;
    }

}
