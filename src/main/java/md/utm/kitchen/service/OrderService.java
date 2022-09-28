package md.utm.kitchen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.service.command.CreateOrderCommand;
import md.utm.kitchen.service.dto.CustomerOrderDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderDataService orderDataService;
    private final PrepareOrderDishesService prepareOrderDishesService;

    private final List<CustomerOrderDto> customerOrders = Collections.synchronizedList(new ArrayList<>());

    public void createOrder(CreateOrderCommand command) {
        final var order = new CustomerOrderDto();

        order.setId(command.getOrderId());
        order.setTableId(command.getTableId());
        order.setWaiterId(command.getWaiterId());
        order.setPriority(command.getPriority());
        order.setMaxWait(command.getMaxWait());
        order.setPickUpTime(command.getPickUpTime());
        order.setDishes(orderDataService.findDishesByIds(command.getItems()));
        order.setDistributed(false);

        customerOrders.add(order);
        log.info("Successfully pushed order '{}' to queue", order.getId());
        new Thread(this::startPreparationThread).start();
    }

    private void startPreparationThread() {
        customerOrders.stream()
                .sorted(Comparator.comparingInt(CustomerOrderDto::getPriority).reversed())
                .filter((o) -> o.getLock().tryLock())
                .findAny()
                .ifPresent((o) -> {
                    prepareOrderDishesService.invoke(o);
                    customerOrders.remove(o);
                    o.getLock().unlock();
                });
    }
}
