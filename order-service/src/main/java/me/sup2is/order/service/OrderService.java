package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.runtime.options.Option;
import lombok.RequiredArgsConstructor;
import me.sup2is.order.client.PaymentModuleClient;
import me.sup2is.order.client.ProductServiceClient;
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
    private final HashOperations<String, String, ProductStockDto> productStockDtoHashOperations;
    private final ObjectMapper objectMapper;
    private final PaymentModuleClient paymentModuleClient;
    private final ProductServiceClient productServiceClient;
    private static final String PREFIX = "product:";

    public void order(Order order) throws OutOfStockException {
        for (OrderItem orderItem : order.getOrderItems())
            checkItemStock(orderItem);

        paymentModuleClient.payment();

        for (OrderItem orderItem : order.getOrderItems())
            orderItemService.addItem(orderItem);

        orderRepository.save(order);

        productServiceClient.modifyStock(ProductStockDto.createDtoByOrderItems(order.getOrderItems()));
    }

    private void checkItemStock(OrderItem orderItem) throws OutOfStockException {

        ProductStockDto productStockDto =
                Optional.ofNullable(
                        objectMapper.convertValue(productStockDtoHashOperations.get(PREFIX + orderItem.getProductId(),
                                "product"),
                                ProductStockDto.class))
                .orElseThrow(() -> new IllegalArgumentException());

        if(productStockDto.getStock() < orderItem.getCount())
            throw new OutOfStockException("product ID : " + orderItem.getProductId() + " stock is lack");

    }

}
