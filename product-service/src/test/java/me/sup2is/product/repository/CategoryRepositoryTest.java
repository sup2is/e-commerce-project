package me.sup2is.product.repository;

import me.sup2is.product.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void find_all_by_names() {

        //given
        Category category1 = Category.createCategory("의류");
        categoryRepository.save(category1);
        Category category2 = Category.createCategory("바지");
        categoryRepository.save(category2);
        Category category3 = Category.createCategory("청바지");
        categoryRepository.save(category3);

        List<String> categoryNames = Arrays.asList("의류", "바지", "청바지");

        //when
        List<Category> allByName = categoryRepository.findAllByNameIn(categoryNames);

        //then
        assertEquals(categoryNames.size(), allByName.size());
        assertTrue(allByName.contains(category1));
        assertTrue(allByName.contains(category2));
        assertTrue(allByName.contains(category3));

    }

}