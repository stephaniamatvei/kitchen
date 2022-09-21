package com.utm.kitchen.core.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cook_proficiency")
public class CookProficiency extends BaseIdValueDrivenEntity {
}
