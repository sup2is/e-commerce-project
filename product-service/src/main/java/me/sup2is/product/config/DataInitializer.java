package me.sup2is.product.config;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.Product;
import me.sup2is.product.service.ProductService;
import me.sup2is.web.JsonResult;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Profile({"dev", "dev-integration"})
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductService productService;
    private final List<String> brandNames = Collections.unmodifiableList(new ArrayList<String>() {{
        add("발렌시아가");
        add("오프화이트");
        add("톰브라운");
        add("스톤아일랜드");
        add("우영미");
        add("나이키");
        add("아디다스");
    }});

    private final List<String> categoryNames = Collections.unmodifiableList(new ArrayList<String>() {{
        add("반팔티셔츠");
        add("청바지");
        add("모자");
        add("긴팔티셔츠");
        add("면바지");
        add("트렌치코트");
        add("옥스포드셔츠");
        add("카라티");
        add("구두");
        add("운동화");
    }});

    @EventListener({ContextRefreshedEvent.class})
    public ResponseEntity<JsonResult<?>> init() {

        for (int i = 0; i < 100; i++) {
            String brandName = brandNames.get(getRandomByCollectionSize(brandNames));
            Product product = Product.Builder.builder()
                    .brandName(brandName)
                    .code("AA" + i)
                    .description("description")
                    .name(brandName + ": " + i)
                    .salable(true)
                    .price(Long.valueOf((int) (Math.random() * 10) + 1) * 100000)
                    .stock(1000000)
                    .build()
                    .toEntity();

            productService.register(1L, product, getRandomCategory());
        }
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    private List<String> getRandomCategory() {
        int i = (int) (Math.random() * 2) + 1;
        List<String> l = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            l.add(categoryNames.get(getRandomByCollectionSize(categoryNames)));
        }
        return l;
    }

    public int getRandomByCollectionSize(Collection<?> c) {
        return (int) (Math.random() * c.size());
    }
    
}
