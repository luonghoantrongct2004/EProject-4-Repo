package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Optional<Activity> getActivityById(int id) {
        return activityRepository.findById(id);
    }

    public List<Activity> findByActivityName(String activityName) {
        return activityRepository.findByActivityName("%" + activityName + "%");
    }

    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public void deleteActivity(int id) {
        activityRepository.deleteById(id);
    }

    public double calculateTotalBudget() {
        // Lấy tất cả các hoạt động từ bảng Activity
        List<Activity> activities = activityRepository.findAll();

        // Tính tổng ngân sách của tất cả các hoạt động
        return activities.stream().mapToDouble(Activity::getBudget).sum();
    }
}
