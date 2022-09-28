package md.utm.kitchen.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.service.dto.CookDto;
import md.utm.kitchen.service.dto.CustomerOrderDto;
import md.utm.kitchen.service.dto.DishDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareOrderDishesService {
    private final SendOrderResultService sendOrderResultService;
    private final OrderDataService orderDataService;

    private List<CookDto> cooks;

    @Value("${app.timeUnit}")
    private String timeUnit;

    @PostConstruct
    public void postConstruct() {
        cooks = orderDataService.generateCooks();
    }

    public void invoke(CustomerOrderDto order) {
        final var startTime = System.currentTimeMillis();
        final var orderId = order.getId();

        log.info("Started preparing dishes for order '{}'", orderId);

        final var es = Executors.newCachedThreadPool();
        order.getDishes().forEach((d) -> es.execute(() -> prepareDish(orderId, d)));
        es.shutdown();

        final var endTime = System.currentTimeMillis();
        final var duration = endTime - startTime;

        log.info("Finished preparing dishes for order '{}' in {}", orderId, duration);
        sendOrderResultService.invoke(order);
    }

    private void prepareDish(Long orderId, DishDto dish) {
        final var dishCode = dish.getCode();
        final var preparationTime = dish.getPreparationTime();
        final var requiredCookProficiency = dish.getRequiredCookProficiency();

        final var suitableCooks = cooks.stream()
                .filter((i) -> i.getCookProficiency() >= requiredCookProficiency)
                .collect(Collectors.toList());

        final var cook = suitableCooks.stream().filter((c) -> c.getLock().tryLock()).findAny().orElseGet(() -> {
            var c = suitableCooks.stream().findAny().orElseThrow();
            c.getLock().lock();
            return c;
        });

        final var cookId = cook.getId();
        log.info("Cook '{}' started preparing dish '{}' for order '{}'", cookId, dishCode, orderId);

        blockThread(calculatePreparationMillis(preparationTime));
        cook.getLock().unlock();
        log.info("Cook '{}' finished preparing dish '{}' for order '{}'", cookId, dishCode, orderId);
    }

    @SneakyThrows
    private void blockThread(long millis) {
        Thread.sleep(millis);
    }

    private long calculatePreparationMillis(int preparationTime) {
        if (timeUnit.equals("MILLISECOND")) {
            return preparationTime;
        }

        if (timeUnit.equals("MINUTE")) {
            return preparationTime * 1000L * 60;
        }

        if (timeUnit.equals("HOUR")) {
            return preparationTime * 1000L * 60 * 60;
        }

        return preparationTime * 1000L;
    }
}
