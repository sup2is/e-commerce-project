package me.sup2is.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    @Test
    @DisplayName("orderItems들 총합 totalPrice 계산")
    public void calculate_total_price() {
        //given
        OrderItem orderItem1 = OrderItem.Builder.builder()
                .productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2)
                .build()
                .toEntity();

        OrderItem orderItem2 = OrderItem.Builder.builder()
                .productId(22L)
                .price(50000L)
                .discountRate(0)
                .count(1)
                .build()
                .toEntity();
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);


        //when
        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        //then
        assertEquals(70000L, order.getTotalPrice());
    }

    @Test
    @DisplayName("orderItems들 총합 totalPrice 계산 & 할인율 계산")
    public void calculate_discount_rate() {
        //given
        OrderItem orderItem1 = OrderItem.Builder.builder()
                .productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2)
                .build()
                .toEntity();

        OrderItem orderItem2 = OrderItem.Builder.builder()
                .productId(22L)
                .price(50000L)
                .discountRate(10)
                .count(1)
                .build()
                .toEntity();

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        //when
        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        //then
        assertEquals(65000L, order.getTotalPrice());

    }

}