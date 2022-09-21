package com.utm.kitchen.service;

import com.utm.kitchen.core.entity.CustomerOrder;
import com.utm.kitchen.core.repository.CustomerOrderRepository;
import com.utm.kitchen.core.repository.DishRepository;
import com.utm.kitchen.service.command.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderService {
    private final DishRepository dishRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final PrepareOrderDishesService prepareOrderDishesService;

    @Transactional
    public void invoke(CreateOrderCommand command) {
        final var order = new CustomerOrder();
        order.setId(command.getOrderId());
        order.setTableId(command.getTableId());
        order.setWaiterId(command.getWaiterId());
        order.setPriority(command.getPriority());
        order.setMaxWait(command.getMaxWait());
        order.setPickUpTime(command.getPickUpTime());
        order.setDishes(dishRepository.findById(command.getItems()));

        final var savedOrder = customerOrderRepository.save(order);
        // THREAD 2 for food preparation launches
        prepareOrderDishesService.invoke(savedOrder);

        // THREAD 1 finishes its execution returning its order code as response
        log.info("Successfully created order '{}'", order.getId());
    }
}
