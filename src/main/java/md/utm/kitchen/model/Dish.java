package md.utm.kitchen.model;

import lombok.Value;

import java.util.concurrent.atomic.AtomicLong;

@Value
public class Dish {
    long id;
    String code;
    int preparationTime;
    int requiredCookRank;
    String cookingMachine;

    AtomicLong assignedCookId = new AtomicLong(0L);
}
