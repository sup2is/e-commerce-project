package me.sup2is.order.domain.dto;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
public class OrderPageRequest {

    private static final int MAX_PAGE_SIZE = 10;

    public static PageRequest createOrderPageRequest(int pageNo, int pageSize) {
        return createOrderPageRequest(pageNo,pageSize,Sort.by(Sort.Direction.ASC, "createAt"));
    }

    public static PageRequest createOrderPageRequest(int pageNo, int pageSize, Sort sort) {
        if(pageNo < 0) {
            pageNo = 0;
        }

        if(pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        return PageRequest.of(pageNo, pageSize, sort);
    }

}
