package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Requestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestionRepository extends JpaRepository<Requestion, Integer> {
    List<Requestion> findByRequestID(@Param("RequestID") Integer RequestID);
}
