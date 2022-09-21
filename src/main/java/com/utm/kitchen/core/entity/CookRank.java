package com.utm.kitchen.core.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cook_rank")
public class CookRank extends BaseIdValueDrivenEntity {
}
