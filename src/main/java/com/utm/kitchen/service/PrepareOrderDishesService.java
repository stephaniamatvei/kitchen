package com.utm.kitchen.service;

import com.utm.kitchen.core.entity.CustomerOrder;
import com.utm.kitchen.core.entity.Dish;
import com.utm.kitchen.core.repository.CookRepository;
import liquibase.repackaged.org.apache.commons.lang3.time.DurationFormatUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareOrderDishesService {
    private final CookRepository cookRepository;
    private final SendOrderResultService sendOrderResultService;
    private final ApplicationContext applicationContext;

    @Value("${app.timeUnit}")
    private String timeUnit;

    @Async
    public void invoke(CustomerOrder order) {
        final var proxy = applicationContext.getBean(getClass());
        final var orderId = order.getId();
        final var startTime = System.currentTimeMillis();

        log.info("Started preparing dishes for order '{}'", orderId);

        order.getDishes().stream()
                .map((d) -> proxy.prepareDish(orderId, d))
                .collect(Collectors.toList())
                .forEach(this::blockThreadForResult);

        final var endTime = System.currentTimeMillis();
        final var duration = endTime - startTime;
        final var formattedDuration = getFormattedPreparationDuration(duration);

        log.info("Finished preparing dishes for order '{}' in {}", orderId, formattedDuration);
        sendOrderResultService.invoke(order);
    }

    @Async
    @SneakyThrows
    @Transactional
    Future<Void> prepareDish(Long orderId, Dish dish) {
        final var dishCode = dish.getCode();
        final var preparationTime = dish.getPreparationTime();
        final var requiredCookProficiency = dish.getRequiredCookProficiency();

        cookRepository.findFreeByProficiency(requiredCookProficiency).ifPresent((c) -> {
            log.info("Started preparing dish '{}' for order '{}'", dishCode, orderId);
            blockThread(calculatePreparationMillis(preparationTime));
            log.info("Finished preparing dish '{}' for order '{}'", dishCode, orderId);
        });

        return new AsyncResult<>(null);
    }

    @SneakyThrows
    private void blockThread(long millis) {
        Thread.sleep(millis);
    }

    @SneakyThrows
    private void blockThreadForResult(Future<?> result) {
        result.get();
    }

    private String getFormattedPreparationDuration(long millis) {
        return DurationFormatUtils.formatDurationWords(millis, true, true);
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
