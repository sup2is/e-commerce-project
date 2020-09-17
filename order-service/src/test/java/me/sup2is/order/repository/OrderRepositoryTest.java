package me.sup2is.order.repository;

import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.web.dto.OrderPageRequestDto;
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
@Import(OrderPageRequestDto.class)
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void find_all_by_member_id() throws IllegalAccessException {
        //given
        OrderItem orderItem1 = OrderItem.Builder.builder()
                .productId(1L)
                .discountRate(0)
                .count(2)
                .build()
                .toEntity();

        OrderItem orderItem2 = OrderItem.Builder.builder()
                .productId(22L)
                .discountRate(0)
                .count(1)
                .build()
                .toEntity();

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);


        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        Order order2 = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        FieldUtils.writeField(order, "memberId", 1L, true);
        FieldUtils.writeField(order2, "memberId", 1L, true);

        orderRepository.saveAndFlush(order);
        orderRepository.saveAndFlush(order2);

        PageRequest orderPageRequest = OrderPageRequestDto.createOrderPageRequest(0, 5);

        //when
        List<Order> orders = orderRepository.findByMemberId(1L, orderPageRequest);

        //then
        assertEquals(2, orders.size());
        assertEquals(order, orders.get(0));
        assertEquals(order2, orders.get(1));

    }


}