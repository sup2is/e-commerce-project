package me.sup2is.order.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    public void order(Order order) {
        for (OrderItem orderItem : order.getOrderItems())
            orderItemService.addItem(orderItem);

        orderRepository.save(order);
    }

}
