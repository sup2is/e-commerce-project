package me.sup2is.product.web.dto;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
public class ProductPageRequestDto {

    private static final int MAX_PAGE_SIZE = 10;

    public static PageRequest createProductPageRequest(int pageNo, int pageSize) {
        return createProductPageRequest(pageNo,pageSize,Sort.by(Sort.Direction.DESC, "createAt"));
    }

    public static PageRequest createProductPageRequest(int pageNo, int pageSize, Sort sort) {
        if(pageNo < 0) {
            pageNo = 0;
        }

        if(pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        return PageRequest.of(pageNo, pageSize, sort);
    }

}
