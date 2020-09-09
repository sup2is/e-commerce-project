package me.sup2is.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("orderItems들 총합 totalPrice 계산")
    public void calculate_total_price() {
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(3);
        OrderItem orderItem1 = OrderItem.createOrderItem(itemBuilder);
        OrderItem orderItem2 = OrderItem.createOrderItem(itemBuilder);

        Order.Builder orderBuilder = new Order.Builder();
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);
        orderBuilder.orderItems(orderItems)
                .address("주문하는 주소");

        //when
        Order order = Order.createOrder(orderBuilder);

        //then
        assertEquals(60000L, order.getTotalPrice());
    }

    @Test
    @DisplayName("orderItems들 총합 totalPrice 계산 & 할인율 계산")
    public void calculate_discount_rate() {
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(10)
                .count(3);
        OrderItem orderItem1 = OrderItem.createOrderItem(itemBuilder);
        OrderItem orderItem2 = OrderItem.createOrderItem(itemBuilder);

        Order.Builder orderBuilder = new Order.Builder();
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);
        orderBuilder.orderItems(orderItems)
                .address("주문하는 주소");

        //when
        Order order = Order.createOrder(orderBuilder);

        //then
        assertEquals(54000L, order.getTotalPrice());

    }

}