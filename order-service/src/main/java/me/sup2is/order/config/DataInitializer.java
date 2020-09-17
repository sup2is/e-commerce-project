package me.sup2is.order.config;

import lombok.RequiredArgsConstructor;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.service.OrderService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
/**
 * OrderService의 DataInitializer는 ProductService의 DataInitial에 의존적임
 * 추후 변경해야할듯 DataInitializer 모듈을 새로 생성?
 * but 개발환경만 포함하기때문에 의존해도 괜찮을것같음
 */
public class DataInitializer {

    private final OrderService orderService;

    @PostConstruct
    public void init() throws OutOfStockException {
        for (int i = 0; i < 100; i++) {

            Order order = Order.Builder.builder()
                    .orderItems(getOrderItem())
                    .address("서울시 강남구")
                    .build()
                    .toEntity();

            orderService.order(2L, order);
        }
    }

    private List<OrderItem> getOrderItem() {
        List<OrderItem> l = new ArrayList<>();
        int i = (int) (Math.random() * 3) + 1;
        for (int j = 0; j < i; j++) {
            l.add(OrderItem.Builder.builder()
                    .count(1)
                    .discountRate(0)
                    .productId(Long.valueOf((int) (Math.random() * 100) + 1))
                    .build()
                    .toEntity());
        }
        return l;
    }

}
