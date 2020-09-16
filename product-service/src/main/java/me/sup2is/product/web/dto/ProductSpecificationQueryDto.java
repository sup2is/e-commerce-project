package me.sup2is.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.sup2is.product.service.ProductSearchKey;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@AllArgsConstructor
@Getter
@Setter
public class ProductSpecificationQueryDto {

    private String name;

    private String code;

    private String brandName;

    private Long minPrice;

    private Long maxPrice;

    private List<String> categories;

    public Map<ProductSearchKey, Object> createQueryMap() {
        Map<ProductSearchKey, Object> queryMap = new HashMap<>();
        if(validate(this.name)) queryMap.put(ProductSearchKey.NAME, this.getName());
        if(validate(this.code)) queryMap.put(ProductSearchKey.CODE, this.getCode());
        if(validate(this.brandName)) queryMap.put(ProductSearchKey.BRAND_NAME, this.getBrandName());
        if(validate(this.maxPrice)) queryMap.put(ProductSearchKey.MAX_PRICE, this.getMaxPrice());
        if(validate(this.minPrice)) queryMap.put(ProductSearchKey.MIN_PRICE, this.getMinPrice());
        if(validate(this.categories)) queryMap.put(ProductSearchKey.CATEGORY, this.getCategories());
        return queryMap;
    }

    private <T> boolean validate(T obj) {
        if(obj == null){
            return false;
        }

        if(obj instanceof String) {
            return !StringUtils.isEmpty((String) obj);
        }
        if(obj instanceof Long) {
            return ((Long) obj).longValue() != 0;
        }
        if(obj instanceof Collection<?>) {
            return !((Collection) obj).isEmpty();
        }

        return false;
    }

}
