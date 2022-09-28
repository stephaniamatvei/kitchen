package md.utm.kitchen.service.command;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class CreateOrderCommand {
    Long orderId;
    Long tableId;
    Long waiterId;
    List<Long> items;
    Integer priority;
    Integer maxWait;
    Instant pickUpTime;
}
