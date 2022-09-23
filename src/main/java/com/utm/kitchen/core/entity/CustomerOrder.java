package com.utm.kitchen.core.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "customer_order")
public class CustomerOrder {
    @Id
    private long id;
    private long waiterId;
    private long tableId;
    private int priority;
    private int maxWait;
    private Instant pickUpTime;

    @Column(name = "distributed")
    private boolean isDistributed;

    @ManyToMany
    @JoinTable(
            name = "customer_order_dish",
            joinColumns = @JoinColumn(name = "customer_order_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes;
}
