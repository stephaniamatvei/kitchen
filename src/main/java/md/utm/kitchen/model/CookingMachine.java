package md.utm.kitchen.model;

import lombok.Value;

import java.util.concurrent.locks.ReentrantLock;

@Value
public class CookingMachine {
    long id;
    String apparatusCode;

    ReentrantLock lock = new ReentrantLock();
}
