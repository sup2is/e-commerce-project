package me.sup2is.product.service;

import me.sup2is.product.config.QueryDSLConfiguration;
import me.sup2is.product.domain.Category;
import me.sup2is.product.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({CategoryService.class, QueryDSLConfiguration.class})
@EnableJpaAuditing
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void find_all_not_exist_category() {
        //given
        Category category1 = Category.createCategory("의류");
        Category category2 = Category.createCategory("청바지");

        categoryService.add(category1);
        categoryService.add(category2);

        //when
        categoryService.findAllByNamesAndInsertNotExist(Arrays.asList("의류", "청바지", "pant"));

        //then
        List<Category> pant = categoryRepository.findAllByNameIn(Arrays.asList("pant"));
        assertEquals(pant.size(), 1);
        assertEquals(pant.get(0).getName(), "pant");

    }

}