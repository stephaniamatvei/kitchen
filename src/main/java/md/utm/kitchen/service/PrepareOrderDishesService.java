package md.utm.kitchen.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.model.Cook;
import md.utm.kitchen.model.CookingMachine;
import md.utm.kitchen.model.CustomerOrder;
import md.utm.kitchen.model.Dish;
import md.utm.kitchen.repository.CookRepository;
import md.utm.kitchen.repository.CookingMachineRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparingInt;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareOrderDishesService {
    private final CookRepository cookRepository;
    private final CookingMachineRepository cookingMachineRepository;
    private final SendOrderResultService sendOrderResultService;

    private List<Cook> cooks;
    private List<CookingMachine> cookingMachines;

    private final List<CustomerOrder> orders = Collections.synchronizedList(new ArrayList<>());

    @PostConstruct
    public void postConstruct() {
        cooks = cookRepository.findAll();
        cookingMachines = cookingMachineRepository.findAll();
    }

    // new thread starts for each cook
    public void invoke(CustomerOrder newOrder) {
        orders.add(newOrder);

        cooks.stream()
                .filter((c) -> c.getIsWorking().compareAndSet(false, true))
                .forEach((c) -> new Thread(() -> initCookWork(c)).start());
    }

    // the logic of preparing dishes according to cook's proficiency
    private void initCookWork(Cook cook) {
        if (cook.getPendingDishes().get() == cook.getCookProficiency()) {
            blockThread(50L);
            initCookWork(cook);
            return;
        }

        getPendingOrderWithLeastTimeLeft(cook).ifPresent((o) ->
                new Thread(() -> findDishAndStartPreparation(cook, o)).start());

        if (orders.isEmpty()) {
            log.info("Cook {} has finished work", cook.getId());

            cook.getIsWorking().set(false);
            return;
        }

        blockThread(50L);
        initCookWork(cook);
    }

    private Optional<CustomerOrder> getPendingOrderWithLeastTimeLeft(Cook cook) {
        return orders.stream()
                .filter((i) -> i.getDishes().stream().anyMatch((d) -> d.getRequiredCookRank() <= cook.getCookRank() && d.getAssignedCookId().get() == 0))
                .min(comparingInt(i -> i.getDishes().stream().mapToInt(Dish::getPreparationTime).sum()));
    }

    private void findDishAndStartPreparation(Cook cook, CustomerOrder order) {
        order.getDishes().stream()
                .filter((i) -> i.getRequiredCookRank() <= cook.getCookRank() && i.getAssignedCookId().compareAndSet(0, cook.getId()))
                .findAny()
                .ifPresent((d) -> prepareDish(cook, d, order));
    }

    // the logic of preparing dishes
    private void prepareDish(Cook cook, Dish dish, CustomerOrder order) {
        final var cookId = cook.getId();
        final var dishCode = dish.getCode();
        final var orderId = order.getId();

        getThreadLockedCookingMachine(dish).ifPresentOrElse(
                (m) -> {
                    final var apparatusCode = m.getApparatusCode();
                    log.info("Cook {} started working on dish {} using {} for order {}", cookId, dishCode, apparatusCode, orderId);
                    blockThread(calculatePreparationTime(dish.getPreparationTime()));
                    m.getLock().unlock();
                    log.info("Cook {} finished working on dish {} using {} for order {}", cookId, dishCode, apparatusCode, orderId);
                },
                () -> {
                    log.info("Cook {} started working on dish {} for order {}", cookId, dishCode, orderId);
                    cook.getPendingDishes().incrementAndGet();
                    blockThread(calculatePreparationTime(dish.getPreparationTime()));
                    cook.getPendingDishes().decrementAndGet();
                    log.info("Cook {} finished working on dish {} for order {}", cookId, dishCode, orderId);
                });

        if (order.getPendingDishesCount().decrementAndGet() == 0) {
            sendOrderToDiningHall(order);
        }
    }

    // the logic of preparing dishes using cooking machine
    private Optional<CookingMachine> getThreadLockedCookingMachine(Dish dish) {
        final var cookingMachineCode = dish.getCookingMachine();

        if (cookingMachineCode == null) {
            return Optional.empty();
        }

        final var cookingMachine = cookingMachines.stream()
                .filter((i) -> i.getApparatusCode().equals(cookingMachineCode) && i.getLock().tryLock()).findAny();

        if (cookingMachine.isPresent()) {
            return cookingMachine;
        }

        return cookingMachines.stream()
                .filter((i) -> i.getApparatusCode().equals(cookingMachineCode))
                .min(comparingInt((i) -> i.getLock().getQueueLength()))
                .map((m) -> {
                    m.getLock().lock();
                    return m;
                });
    }

    private void sendOrderToDiningHall(CustomerOrder order) {
        orders.remove(order);
        sendOrderResultService.invoke(order);
    }

    @SneakyThrows
    private void blockThread(long millis) {
        Thread.sleep(millis);
    }

    private long calculatePreparationTime(int timeUnits) {
        return timeUnits * 1000L;
    }
}
