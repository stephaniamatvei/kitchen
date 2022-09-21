package com.utm.kitchen.core.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cook")
public class Cook extends BaseIdCodeDrivenEntity {
    private String givenName;
    private String lastName;
    private String catchPhrase;

    @ManyToOne
    @JoinColumn(name = "cook_rank_id")
    private CookRank cookRank;

    @ManyToOne
    @JoinColumn(name = "cook_proficiency_id")
    private CookProficiency cookProficiency;

    private Long dishLockId;
}
