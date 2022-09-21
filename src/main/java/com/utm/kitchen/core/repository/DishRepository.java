package com.utm.kitchen.core.repository;

import com.utm.kitchen.core.entity.Dish;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends CrudRepository<Dish, Long> {

    @Query("SELECT d FROM Dish d WHERE d.id IN (:ids)")
    List<Dish> findById(List<Long> ids);

}
