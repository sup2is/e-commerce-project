package me.sup2is.order.repository;

import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.OrderPageRequest;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@EnableJpaAuditing
@Import(OrderPageRequest.class)
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void find_all_by_member_id() throws IllegalAccessException {
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2);

        OrderItem.Builder itemBuilder2 = new OrderItem.Builder();
        itemBuilder2.productId(22L)
                .price(50000L)
                .discountRate(0)
                .count(1);

        OrderItem orderItem1 = OrderItem.createOrderItem(itemBuilder);
        OrderItem orderItem2 = OrderItem.createOrderItem(itemBuilder2);

        Order.Builder orderBuilder = new Order.Builder();
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);
        orderBuilder.orderItems(orderItems)
                .address("주문하는 주소");

        Order order = Order.createOrder(orderBuilder);
        Order order2 = Order.createOrder(orderBuilder);
        FieldUtils.writeField(order, "memberId", 1L, true);
        FieldUtils.writeField(order2, "memberId", 1L, true);

        orderRepository.saveAndFlush(order);
        orderRepository.saveAndFlush(order2);

        PageRequest orderPageRequest = OrderPageRequest.createOrderPageRequest(0, 5);

        //when
        List<Order> orders = orderRepository.findByMemberId(1L, orderPageRequest);

        //then
        assertEquals(2, orders.size());
        assertEquals(order, orders.get(0));
        assertEquals(order2, orders.get(1));

    }


}