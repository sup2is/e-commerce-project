package me.sup2is.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.runtime.options.Option;
import lombok.RequiredArgsConstructor;
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
    private static final String PREFIX = "product:";

    public void order(Order order) throws OutOfStockException {
        for (OrderItem orderItem : order.getOrderItems())
            checkItemStock(orderItem);

        for (OrderItem orderItem : order.getOrderItems())
            orderItemService.addItem(orderItem);

        orderRepository.save(order);

        //todo product-serivce와 redis hash에 담긴 count 조정
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


    public boolean payment() {
        //실제 모듈을 태우지는 않고 약 1~10초간격으로, 1/10 확률로 결제 실패, 9/10 확률로 결제 완료
        int random = (int)(Math.random() * 10) + 1;
        try {
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(random == 5) {
            return false;
        }
        return true;
    }

}
