package md.utm.kitchen.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Value
@Builder
public class CustomerOrder {
    long id;
    long waiterId;
    long tableId;
    int priority;
    int maxWait;
    Instant pickUpTime;
    List<Dish> dishes;

    AtomicInteger pendingDishesCount = new AtomicInteger(0);
}
