package me.sup2is.order.service;

import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@Import(OrderItemService.class)
@EnableJpaAuditing
class OrderItemServiceTest {

    @Autowired
    OrderItemService orderItemService;

    @Test
    @DisplayName("주문 취소시 하위 item 들도 CANCEL상태 인지 확인")
    public void cancel_order() {
        //given
        OrderItem orderItem1 = OrderItem.Builder.builder()
                .productId(1L)
                .discountRate(0)
                .count(2)
                .build()
                .toEntity();

        OrderItem orderItem2 = OrderItem.Builder.builder()
                .productId(1L)
                .discountRate(0)
                .count(2)
                .build()
                .toEntity();

        List<OrderItem> items = Arrays.asList(orderItem1, orderItem2);
        orderItemService.addItems(items);
        //when
        orderItemService.cancelItems(items);

        //then
        assertEquals(OrderStatus.CANCEL, orderItem1.getOrderStatus());
        assertEquals(OrderStatus.CANCEL, orderItem2.getOrderStatus());

    }



}