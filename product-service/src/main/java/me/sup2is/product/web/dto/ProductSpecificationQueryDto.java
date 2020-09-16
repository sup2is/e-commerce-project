package me.sup2is.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.product.service.ProductSearchKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ProductSpecificationQueryDto {

    private String name;

    private String code;

    private String brandName;

    private PriceQueryDto priceQuery;

    private List<String> categories;

    public Map<ProductSearchKey, Object> createQueryMap() {
        Map<ProductSearchKey, Object> queryMap = new HashMap<>();
        if(!this.getName().isEmpty()) queryMap.put(ProductSearchKey.NAME, this.getName());
        if(!this.getCode().isEmpty()) queryMap.put(ProductSearchKey.CODE, this.getCode());
        if(!this.getBrandName().isEmpty()) queryMap.put(ProductSearchKey.BRAND_NAME, this.getBrandName());
        if(this.getPriceQuery() != null) queryMap.put(ProductSearchKey.PRICE, this.getPriceQuery());
        if(this.getCategories() != null && !this.getCategories().isEmpty())
            queryMap.put(ProductSearchKey.CATEGORY, this.getCategories());
        return queryMap;
    }

}
