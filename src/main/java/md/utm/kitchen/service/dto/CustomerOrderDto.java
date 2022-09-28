package md.utm.kitchen.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerOrderDto {
    @EqualsAndHashCode.Include
    private long id;
    private long waiterId;
    private long tableId;
    private int priority;
    private int maxWait;
    private Instant pickUpTime;
    private boolean isDistributed;
    private List<DishDto> dishes;

    private final Lock lock = new ReentrantLock();
}
