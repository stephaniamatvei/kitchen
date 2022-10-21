package md.utm.kitchen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.model.CustomerOrder;
import md.utm.kitchen.model.Dish;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendOrderResultService {
    private final ObjectMapper objectMapper;

    @Value("${app.diningHallResultEndpointUrl}")
    private String diningHallResultEndpointUrl;
    private WebClient client;

    @PostConstruct
    public void postConstruct() {
        client = WebClient.builder().baseUrl(diningHallResultEndpointUrl).build();
    }

    public void invoke(CustomerOrder order) {
        log.info("Sending result for order '{}' to dining hall...", order.getId());

        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest(order))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @SneakyThrows
    private String createRequest(CustomerOrder order) {
        final var dishes = order.getDishes().stream()
                .map(Dish::getId).collect(Collectors.toList());

        final var request = Request.builder()
                .orderId(order.getId())
                .tableId(order.getTableId())
                .waiterId(order.getWaiterId())
                .items(dishes)
                .cookingDetails(List.of())
                .priority(order.getPriority())
                .maxWait(order.getMaxWait())
                .pickUpTime(order.getPickUpTime())
                .build();

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
    }

    @Getter
    @Builder
    private static class Request {
        long orderId;
        long tableId;
        long waiterId;
        List<Long> items;
        int priority;
        int maxWait;
        Instant pickUpTime;
        int cookingTime;
        List<CookingDetailsItem> cookingDetails;

        @Getter
        @AllArgsConstructor
        private static class CookingDetailsItem {
            long foodId;
            long cookId;
        }
    }
}
