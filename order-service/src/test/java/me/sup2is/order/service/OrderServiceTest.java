package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.OrderStatus;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.repository.OrderItemRepository;
import me.sup2is.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static reactor.core.publisher.Mono.when;

@DataJpaTest
@Import({OrderService.class, OrderItemService.class, ObjectMapper.class})
@Transactional
@EnableJpaAuditing
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @MockBean
    HashOperations<String, String, ProductStockDto> productStockDtoHashOperations;

    @Test
    public void save() throws OutOfStockException {
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2);
        OrderItem orderItem1 = OrderItem.createOrderItem(itemBuilder);
        OrderItem orderItem2 = OrderItem.createOrderItem(itemBuilder);

        Order.Builder orderBuilder = new Order.Builder();
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);
        orderBuilder.orderItems(orderItems)
                .memberId(10L);

        Order order = Order.createOrder(orderBuilder);
        Mockito.when(productStockDtoHashOperations.get(anyString(), anyString()))
                .thenReturn(new ProductStockDto(orderItem1.getProductId(),orderItem1.getCount()));


        //when
        orderService.order(order);

        //then
        Order findOrder = orderRepository.findById(order.getId()).get();

        assertEquals(order, findOrder);
        assertEquals(40000, order.getTotalPrice());
        assertEquals(OrderStatus.CHECK, order.getOrderStatus());
        assertEquals(order, orderItem1.getOrder());
        assertEquals(order, orderItem2.getOrder());

        OrderItem findOrderItem1 = orderItemRepository.findById(orderItem1.getId()).get();
        OrderItem findOrderItem2 = orderItemRepository.findById(orderItem2.getId()).get();

        assertEquals(orderItem1, findOrderItem1);
        assertEquals(orderItem2, findOrderItem2);
        assertNotNull(orderItem1.getOrder());
        assertNotNull(orderItem2.getOrder());

        assertNotNull(order.getCreateAt());
        assertNotNull(order.getUpdatedAt());

    }

    @Test
    @DisplayName("재고부족으로 주문 실패")
    public void reject_save() throws OutOfStockException {
        //given
        //given
        OrderItem.Builder itemBuilder = new OrderItem.Builder();
        itemBuilder.productId(1L)
                .price(10000L)
                .discountRate(0)
                .count(2);
        OrderItem orderItem1 = OrderItem.createOrderItem(itemBuilder);
        OrderItem orderItem2 = OrderItem.createOrderItem(itemBuilder);

        Order.Builder orderBuilder = new Order.Builder();
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);
        orderBuilder.orderItems(orderItems)
                .memberId(10L);

        Order order = Order.createOrder(orderBuilder);
        Mockito.when(productStockDtoHashOperations.get(anyString(), anyString()))
                .thenReturn(new ProductStockDto(orderItem1.getProductId(),orderItem1.getCount() - 1));


        //when
        //then
        assertThrows(OutOfStockException.class, () -> orderService.order(order));
    }

}