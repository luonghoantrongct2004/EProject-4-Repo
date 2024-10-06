package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.ActivityNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityNotificationRepository extends JpaRepository<ActivityNotification, Long> {
    @Query("SELECT n FROM ActivityNotification n WHERE n.isRead = false")
    List<ActivityNotification> findUnreadNotifications();
    @Query("SELECT n FROM ActivityNotification n ORDER BY n.notificationTime DESC")
    List<ActivityNotification> findAllNotificationsArranged();
    List<ActivityNotification> findByActivity_ActivityNameContaining(String activityName);
}
