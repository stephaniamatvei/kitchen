package md.utm.kitchen.controller.request;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Value
public class CreateOrderRequest {
    @NotNull Long orderId;
    @NotNull Long tableId;
    @NotNull Long waiterId;
    @NotNull List<Long> items;
    @NotNull Integer priority;
    @NotNull Integer maxWait;
    @NotNull Instant pickUpTime;
}
