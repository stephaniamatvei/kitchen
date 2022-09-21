package com.utm.kitchen.core.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cooking_machine")
public class CookingMachine extends BaseIdCodeDrivenEntity {
}
