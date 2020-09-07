package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.runtime.options.Option;
import lombok.RequiredArgsConstructor;
import me.sup2is.order.client.PaymentModuleClient;
import me.sup2is.order.client.ProductServiceClient;
import me.sup2is.order.exception.CancelFailureException;
import me.sup2is.order.exception.OrderNotFoundException;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.repository.OrderRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final CachedProductStockService cachedProductStockService;
    private final PaymentModuleClient paymentModuleClient;
    private final ProductServiceClient productServiceClient;

    public void order(Order order) throws OutOfStockException {
        for (OrderItem orderItem : order.getOrderItems())
            cachedProductStockService.checkItemStock(orderItem);

        paymentModuleClient.payment();

        orderItemService.addItems(order.getOrderItems());
        orderRepository.save(order);
        productServiceClient.modifyStock(ProductStockDto.createDtoByOrderItems(order.getOrderItems()));
    }

    public Order findOne(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("order is not found"));
    }

    public void cancel(Long orderId) throws CancelFailureException, OrderNotFoundException {
        Order order = findOne(orderId);
        order.cancel();
        orderItemService.cancelItems(order.getOrderItems());
    }
}

