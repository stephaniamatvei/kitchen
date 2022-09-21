package com.utm.kitchen.core.repository;

import com.utm.kitchen.core.entity.CustomerOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, Long> {
}
