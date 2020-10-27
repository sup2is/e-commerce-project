package me.sup2is.product.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.Product;
import me.sup2is.product.service.ProductSearchKey;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.sup2is.product.domain.QCategory.category;
import static me.sup2is.product.domain.QProduct.product;
import static me.sup2is.product.domain.QProductCategory.productCategory;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Product> searchAll(PageRequest pageRequest, Map<ProductSearchKey, Object> query) {
        return jpaQueryFactory.selectFrom(product)
                .where(createQueries(query))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    private Predicate[] createQueries(Map<ProductSearchKey, Object> queryMap) {
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<ProductSearchKey, Object> querySet : queryMap.entrySet()) {
            ProductSearchKey key = querySet.getKey();
            switch (key) {
                case CODE:
                    predicates.add(product.code.contains(String.valueOf(querySet.getValue())));
                    break;
                case NAME:
                    predicates.add(product.name.contains(String.valueOf(querySet.getValue())));
                    break;
                case BRAND_NAME:
                    predicates.add(product.brandName.contains(String.valueOf(querySet.getValue())));
                    break;
                case MAX_PRICE:
                    predicates.add(product.price.lt((Long)querySet.getValue() + 1));
                    break;
                case MIN_PRICE:
                    predicates.add(product.price.gt((Long)querySet.getValue() - 1));
                    break;
                case CATEGORY:
                    List<String> value = (List<String>) querySet.getValue();
                    List<Long> ids = jpaQueryFactory.from(product)
                            .join(product.categories, productCategory)
                            .join(productCategory.category, category)
                            .where(category.name.in(value))
                            .select(product.id)
                            .distinct()
                            .fetch();
                    predicates.add(product.id.in(ids));
                    break;
            }
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }

}
