package com.utm.kitchen.core.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dish")
public class Dish extends BaseIdCodeDrivenEntity {
    private int preparationTime;

    @ManyToOne
    @JoinColumn(name = "cook_proficiency_id")
    private CookProficiency requiredCookProficiency;

    @ManyToOne
    @JoinColumn(name = "cooking_machine_id")
    private CookingMachine cookingMachine;
}
