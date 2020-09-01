package me.sup2is.order.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public void addItem(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

}
