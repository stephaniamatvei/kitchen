package md.utm.kitchen.controller;

import md.utm.kitchen.controller.request.CreateOrderRequest;
import md.utm.kitchen.service.OrderService;
import md.utm.kitchen.service.command.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService createOrderService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        final var command = CreateOrderCommand.builder()
                .orderId(request.getOrderId())
                .tableId(request.getTableId())
                .waiterId(request.getWaiterId())
                .items(request.getItems())
                .priority(request.getPriority())
                .maxWait(request.getMaxWait())
                .pickUpTime(request.getPickUpTime())
                .build();

        createOrderService.createOrder(command);
        return ResponseEntity.noContent().build();
    }
}
