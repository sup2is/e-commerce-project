package me.sup2is.product.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.product.domain.Category;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryRequestDto {

    @NotEmpty
    private String name;

    public Category toEntity() {
        return Category.createCategory(name);
    }
}
