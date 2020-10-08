package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sup2is.order.client.PaymentModuleClient;
import me.sup2is.order.client.ProductServiceClient;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.OrderStatus;
import me.sup2is.order.domain.dto.ModifyOrderItem;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.exception.CancelFailureException;
import me.sup2is.order.exception.OrderNotFoundException;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.exception.ProductNotFoundException;
import me.sup2is.order.repository.OrderItemRepository;
import me.sup2is.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({OrderService.class,
        OrderItemService.class,
        ObjectMapper.class,
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
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
    CachedProductStockService cachedProductStockService;

    @MockBean(name = "productServiceClient")
    ProductServiceClient productServiceClient;

    @MockBean
    PaymentModuleClient paymentModuleClient;

    @Test
    @DisplayName("정상적인 order와 orderItems의 save")
    public void save() throws OutOfStockException {
        //given
        OrderItem orderItem1 = getOrderItem(1L, 10000L, 2);
        OrderItem orderItem2 = getOrderItem(22L, 50000L, 1);

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();


        when(cachedProductStockService.getCachedProductStock(anyLong()))
                .thenReturn(new ProductStockDto(1L,5,10000L), new ProductStockDto(22L,5,50000L));

        //when
        orderService.order(1L, order);

        //then
        Order findOrder = orderRepository.findById(order.getId()).get();

        assertEquals(order, findOrder);
        assertEquals(70000L, order.getTotalPrice());
        assertEquals(OrderStatus.ORDER, order.getOrderStatus());
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
    @DisplayName("비정상적인 product ID 요청")
    public void save_invalid_product_id() throws OutOfStockException {
        //given
        OrderItem orderItem1 = getOrderItem(66666L, 10000L, 2);

        List<OrderItem> orderItems = Arrays.asList(orderItem1);

        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();


        doThrow(ProductNotFoundException.class)
                .when(cachedProductStockService)
                .checkItemStock(66666L, 2);

        //when
        //then
        assertThrows(ProductNotFoundException.class, () -> orderService.order(1L, order));

    }

    @Test
    @DisplayName("주문 취소")
    public void cancel() throws OutOfStockException, CancelFailureException, OrderNotFoundException, IllegalAccessException {
        //given
        OrderItem orderItem1 = getOrderItem(1L, 10000L, 2);
        OrderItem orderItem2 = getOrderItem(22L, 50000L, 1);

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        when(cachedProductStockService.getCachedProductStock(anyLong()))
                .thenReturn(new ProductStockDto(1L,5,10000L), new ProductStockDto(22L,5,50000L));

        orderService.order(1L, order);



        //when
        orderService.cancel(order.getId(), 1L);

        //then
        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());
        assertEquals(OrderStatus.CANCEL, order.getOrderItems().get(0).getOrderStatus());
        assertEquals(OrderStatus.CANCEL, order.getOrderItems().get(1).getOrderStatus());
    }

    @Test
    @DisplayName("주문 취소요청시 다른유저의 주문에 접근")
    public void cancel_illegal_access() throws OutOfStockException {
        //given
        OrderItem orderItem1 = getOrderItem(1L, 10000L, 2);
        OrderItem orderItem2 = getOrderItem(22L, 50000L, 1);

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        when(cachedProductStockService.getCachedProductStock(anyLong()))
                .thenReturn(new ProductStockDto(1L,5,10000L), new ProductStockDto(22L,5,50000L));

        orderService.order(1L, order);

        //when
        //then
        assertThrows(IllegalAccessException.class, () -> orderService.cancel(order.getId(), 2L));
    }


    @Test
    @DisplayName("주문 수문 수정")
    public void modify() throws OutOfStockException, OrderNotFoundException, IllegalAccessException {
        //given
        OrderItem orderItem1 = getOrderItem(1L, 10000L, 2);
        OrderItem orderItem2 = getOrderItem(22L, 50000L, 1);

        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        Order order = Order.Builder.builder()
                .orderItems(orderItems)
                .address("주문하는 주소")
                .build()
                .toEntity();

        when(cachedProductStockService.getCachedProductStock(anyLong()))
                .thenReturn(new ProductStockDto(1L,5,10000L), new ProductStockDto(22L,5,50000L));

        orderService.order(1L, order);

        ModifyOrderItem modifyOrderItem = new ModifyOrderItem(1L, 5);

        //when
        String newAddress = "변경된 주소";
        orderService.modify(1L, order.getId(), newAddress, Arrays.asList(modifyOrderItem));

        //then
        Order findOrder = orderService.findOne(order.getId());

        assertEquals(newAddress, findOrder.getAddress());
        assertEquals(5, findOrder.getOrderItems().get(0).getCount());

    }

    private OrderItem getOrderItem(long productId, long price, int count) {

        return OrderItem.Builder.builder()
                .productId(productId)
                .discountRate(0)
                .count(count)
                .build()
                .toEntity();

    }


}