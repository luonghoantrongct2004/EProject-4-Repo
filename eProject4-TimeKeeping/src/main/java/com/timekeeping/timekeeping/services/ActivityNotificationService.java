package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.models.ActivityNotification;
import com.timekeeping.timekeeping.repositories.ActivityNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityNotificationService {

    @Autowired
    private ActivityNotificationRepository activityNotificationRepository;

    public List<ActivityNotification> getAllNotifications() {
        return activityNotificationRepository.findAllNotificationsArranged();
    }

    public void saveNotification(ActivityNotification notification) {
        activityNotificationRepository.save(notification);
    }

    public List<ActivityNotification> getUnreadNotifications() {
        return activityNotificationRepository.findUnreadNotifications();
    }

    public Optional<ActivityNotification> getActivityNotificationById(Long id) {
        return activityNotificationRepository.findById(id);
    }

    public List<ActivityNotification> findAll() {
        return activityNotificationRepository.findAll();
    }

    public Optional<ActivityNotification> findById(Long id) {
        return activityNotificationRepository.findById(id);
    }

    public List<ActivityNotification> findByActivityName(String activityName) {
        return activityNotificationRepository.findByActivity_ActivityNameContaining(activityName);
    }

    public ActivityNotification save(ActivityNotification activityNotification) {
        return activityNotificationRepository.save(activityNotification);
    }

    public void deleteById(Long id) {
        activityNotificationRepository.deleteById(id);
    }
}

