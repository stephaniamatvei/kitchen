package com.utm.kitchen.core.repository;

import com.utm.kitchen.core.entity.CustomerOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, Long> {

    @Query(
            value = "SELECT * FROM CustomerOrder WHERE distributed IS FALSE ORDER BY priority LIMIT 1",
            nativeQuery = true
    )
    CustomerOrder findWithHighestPriority();

}
