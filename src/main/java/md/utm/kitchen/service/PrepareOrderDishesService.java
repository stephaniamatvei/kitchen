package md.utm.kitchen.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.service.dto.CookDto;
import md.utm.kitchen.service.dto.CookingMachineDto;
import md.utm.kitchen.service.dto.CustomerOrderDto;
import md.utm.kitchen.service.dto.DishDto;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareOrderDishesService {
    private final SendOrderResultService sendOrderResultService;
    private final OrderDataService orderDataService;

    private List<CookDto> cooks;
    private List<CookingMachineDto> cookingMachines;

    private final List<CustomerOrderDto> customerOrders = Collections.synchronizedList(new ArrayList<>());

    @PostConstruct
    public void postConstruct() {
        cooks = orderDataService.readCooks();
        cookingMachines = orderDataService.readCookingMachines();
    }

    public void invoke(CustomerOrderDto order) {
        customerOrders.add(order);

        final var startTime = System.currentTimeMillis();
        final var orderId = order.getId();

        log.info("Started preparing dishes for order '{}'", orderId);

        final var es = Executors.newFixedThreadPool(1000);
        order.getDishes().forEach((d) -> es.execute(() -> prepareDish(orderId, d)));
        es.shutdown();

        try {
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {
        }

        final var endTime = System.currentTimeMillis();
        final var duration = endTime - startTime;

        log.info("Finished preparing dishes for order '{}' in {}", orderId, duration);
        sendOrderResultService.invoke(order);
    }

    private void prepareDish(Long orderId, DishDto dish) {
        final var dishCode = dish.getCode();
        final var machine = getThreadLockedMachine(dish.getCookingMachine());
        final var cook = getThreadLockedCook(dish.getRequiredCookProficiency());
        final var cookId = cook.getId();

        machine.ifPresentOrElse(
                (m) -> log.info("Cook '{}' started preparing dish '{}' in '{}' for order '{}'", cookId, dishCode, m.getApparatusCode(), orderId),
                () -> log.info("Cook '{}' started preparing dish '{}' for order '{}'", cookId, dishCode, orderId));

        machine.ifPresent((m) -> cook.getLock().unlock());
        blockThread(calculatePreparationTime(dish.getPreparationTime()));

        if (machine.isEmpty()) {
            cook.getLock().unlock();
        }

        machine.ifPresent((m) -> m.getLock().unlock());

        log.info("Cook '{}' finished preparing dish '{}' for order '{}'", cookId, dishCode, orderId);
    }

    // prepare foods according to cook's PROFICIENCY
    private CookDto getThreadLockedCook(int requiredCookRank) {
        final var suitableCooks = cooks.stream()
                .filter((i) -> i.getCookRank() >= requiredCookRank)
                .collect(Collectors.toList());

        return suitableCooks.stream().filter((c) -> c.getLock().tryLock()).findAny().orElseGet(() -> {
            final var c = suitableCooks.stream()
                    .min(Comparator.comparingInt(i -> i.getLock().getQueueLength()))
                    .orElseThrow();

            c.getLock().lock();
            return c;
        });
    }

    // prepare foods using COOKING APPARATUSES
    private Optional<CookingMachineDto> getThreadLockedMachine(String requiredMachine) {
        if (requiredMachine == null) {
            return Optional.empty();
        }

        final var suitableMachines = cookingMachines.stream()
                .filter((i) -> i.getApparatusCode().equals(requiredMachine))
                .collect(Collectors.toList());

        final var result = suitableMachines.stream().filter((m) -> m.getLock().tryLock()).findAny().orElseGet(() -> {
            final var m = suitableMachines.stream()
                    .min(Comparator.comparingInt(i -> i.getLock().getQueueLength()))
                    .orElseThrow();

            m.getLock().lock();
            return m;
        });

        return Optional.of(result);
    }


    @SneakyThrows
    private void blockThread(long millis) {
        Thread.sleep(millis);
    }

    private long calculatePreparationTime(int timeUnits) {
        return timeUnits * 1000L;
    }
}
