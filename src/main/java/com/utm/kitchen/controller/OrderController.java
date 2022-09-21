package com.utm.kitchen.controller;

import com.utm.kitchen.controller.request.CreateOrderRequest;
import com.utm.kitchen.service.CreateOrderService;
import com.utm.kitchen.service.command.CreateOrderCommand;
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
    private final CreateOrderService createOrderService;

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

        // THREAD 1 for saving order to db launches
        createOrderService.invoke(command);
        return ResponseEntity.noContent().build();
    }
}
