package md.utm.kitchen.model;

import lombok.Value;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Value
public class Cook {
    long id;
    String givenName;
    String lastName;
    String catchPhrase;
    int cookRank;
    int cookProficiency;

    AtomicBoolean isWorking = new AtomicBoolean(false);
    AtomicInteger pendingDishes = new AtomicInteger(0);
}
