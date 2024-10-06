package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Query("SELECT a FROM Activity a WHERE a.activityName LIKE :activityName")
    List<Activity> findByActivityName(@Param("activityName") String activityName);
}