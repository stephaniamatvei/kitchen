package com.utm.kitchen.core.repository;

import com.utm.kitchen.core.entity.Cook;
import com.utm.kitchen.core.entity.CookProficiency;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CookRepository extends CrudRepository<Cook, Long> {

    @Query("SELECT c FROM Cook c WHERE c.cookProficiency = :proficiency AND c.dishLockId IS NULL")
    Optional<Cook> findFreeByProficiency(CookProficiency proficiency);

}
