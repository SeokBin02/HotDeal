package challenge18.hotdeal.domain.limited.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Document(indexName = "limited_products")
@Mapping(mappingPath = "elastic/products-mapping.json")
@Setting(settingPath = "elastic/products-setting.json")
public class LimitedProductDocument {
    @Id
    private Long product_id;

    private String categorya;

    private String categoryb;

    private int price;

    private String product_name;

    public LimitedProductDocument(Long product_id, String categorya, String categoryb, int price, String product_name) {
        this.product_id = product_id;
        this.categorya = categorya;
        this.categoryb = categoryb;
        this.price = price;
        this.product_name = product_name;
    }
}
