package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.repositories.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JavaMailSender javaMailSender;

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

    public void sendEmail(Activity activity, long id) {
        for (Account acc : accountRepository.findAllEmployees()) {
            MimeMessage message = javaMailSender.createMimeMessage();
            try {
                String content = """
                    Dear %s, <br/><br/>
                    
                    We are excited to invite you to our upcoming %s, which will be held on %s at %s. %s. <br/><br/>
                        
                    Event Details: <br/><br/>
                
                    Date: %s <br/>
                    Time: %s <br/>
                    Location: %s <br/>
                    Please let us know if you will be attending by confirming your participation through the link below: <br/>
                    <a href="http://localhost:8080/notifications/view/%d">Confirm Your Attendance</a> <br/><br/>
                
                    We look forward to having you there and hope you can join us for this fun and engaging event. <br/><br/>
                
                    If you have any questions or need further details, feel free to reach out to at <a href="mailto:goppycompany@gmail.com">goppycompany@gmail.com</a> or <a href="tel:0359126487">0359126487</a>. <br/><br/>
                
                    Best regards, <br/>
                    Goppy
                """;
                content = String.format(content, acc.getFullName(), activity.getActivityName(), activity.getStartTime(), activity.getLocation(), activity.getDescription(), activity.getStartTime().toLocalDate(), activity.getStartTime().toLocalTime(), activity.getLocation(), id);
                String mandatory = String.valueOf(activity.getType());
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom("goppycompany@gmail.com");
                helper.setTo(acc.getEmail());
                helper.setSubject("[" + mandatory + "] " + activity.getActivityName() + " - Confirm Your Attendance");
                helper.setText(content, true);
                javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
