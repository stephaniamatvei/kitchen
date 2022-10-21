package md.utm.kitchen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.model.CustomerOrder;
import md.utm.kitchen.repository.DishRepository;
import md.utm.kitchen.service.command.CreateOrderCommand;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final DishRepository dishRepository;
    private final PrepareOrderDishesService prepareOrderDishesService;

    public void createOrder(CreateOrderCommand command) {
        final var dishes = dishRepository.findByIds(command.getItems());

        final var order = CustomerOrder.builder()
                .id(command.getOrderId())
                .tableId(command.getTableId())
                .waiterId(command.getWaiterId())
                .priority(command.getPriority())
                .maxWait(command.getMaxWait())
                .pickUpTime(command.getPickUpTime())
                .dishes(dishes)
                .build();

        order.getPendingDishesCount().set(dishes.size());

        log.info("Pushing order '{}' to queue", order.getId());
        new Thread(() -> prepareOrderDishesService.invoke(order)).start();
    }
}
