package me.sup2is.product.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.Product;
import me.sup2is.product.web.dto.PriceQueryDto;
import me.sup2is.product.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductCategoryService productCategoryService;

    public List<Product> findAllByQuery(PageRequest pageRequest, Map<ProductSearchKey, Object> query) {
        return productRepository.findAll(createSpecification(query), pageRequest).getContent();
    }

    private Specification<Product> createSpecification(Map<ProductSearchKey, Object> queryMap) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> queries = createQueries(queryMap, root, criteriaBuilder);
            return criteriaBuilder.and(queries.toArray(new Predicate[0]));
        };
    }

    private List<Predicate> createQueries(Map<ProductSearchKey, Object> queryMap,
                                          Root<Product> root,
                                          CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<ProductSearchKey, Object> querySet : queryMap.entrySet()) {
            ProductSearchKey key = querySet.getKey();
            switch (key) {
                case CODE:
                case NAME:
                case BRAND_NAME:
                    predicates.add(
                            builder.like(root.get(key.getName()).as(String.class),
                                    "%" + querySet.getValue() + "%")
                    );
                    break;
                case PRICE:
                    PriceQueryDto pq = (PriceQueryDto) querySet.getValue();
                    if(pq.getOperation().equals("<")) {
                        predicates.add(builder.greaterThanOrEqualTo(root.get(key.getName()), pq.getValue()));
                    } else if (pq.getOperation().equals(">")) {
                        predicates.add(builder.lessThanOrEqualTo(root.get(key.getName()), pq.getValue()));
                    }
                    break;
                case CATEGORY:
                    List<Long> productCategories = getProductIdsByCategories(querySet);
                    predicates.add(root.get("id").in(productCategories));
                    break;
            }
        }
        return predicates;
    }

    private List<Long> getProductIdsByCategories(Map.Entry<ProductSearchKey, Object> querySet) {
        return productCategoryService.findAllByCategoryNameIn((List<String>) querySet.getValue())
                .stream()
                .map(pc -> pc.getProduct().getId())
                .distinct()
                .collect(toList());
    }

}
