package challenge18.hotdeal.domain.product.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Document(indexName = "products")
@Mapping(mappingPath = "elastic/products-mapping.json")
@Setting(settingPath = "elastic/products-setting.json")
public class ProductDocument {
    @Id
    private Long product_id;

    private String categorya;

    private String categoryb;

    private int price;

    private String product_name;

    public ProductDocument(Long product_id, String categorya, String categoryb, int price, String product_name) {
        this.product_id = product_id;
        this.categorya = categorya;
        this.categoryb = categoryb;
        this.price = price;
        this.product_name = product_name;
    }
}
