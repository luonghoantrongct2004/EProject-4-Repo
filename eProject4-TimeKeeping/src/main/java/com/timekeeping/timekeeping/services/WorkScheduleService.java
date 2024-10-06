package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.enums.ApprovalStatus;
import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Shift;
import com.timekeeping.timekeeping.models.WorkSchedule;
import com.timekeeping.timekeeping.repositories.ShiftRepository;
import com.timekeeping.timekeeping.repositories.WorkScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WorkScheduleService {
    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    public List<WorkSchedule> getAllSchedules() {
        return workScheduleRepository.findAll();
    }

    public Optional<WorkSchedule> getScheduleById(int id) {
        return workScheduleRepository.findById(id);
    }

    public List<WorkSchedule> findByFullName(String fullName) {
        return workScheduleRepository.findByFullName("%" + fullName + "%");
    }

    public List<WorkSchedule> getSchedulesForWeek(LocalDate startOfWeek, LocalDate endOfWeek) {
//        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
//        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return workScheduleRepository.findByDateBetween(startOfWeek, endOfWeek);
    }

    public WorkSchedule saveSchedule(WorkSchedule schedule) {
        return workScheduleRepository.save(schedule);
    }

    public void deleteSchedule(int id) {
        workScheduleRepository.deleteById(id);
    }

    public void updateShiftSchedule(int scheduleId, int shiftId) {
        WorkSchedule existingSchedule = workScheduleRepository.findById(scheduleId).orElseThrow();
        Shift existingShift = shiftRepository.findById(shiftId).orElseThrow();
        existingSchedule.setShift(existingShift);
        workScheduleRepository.save(existingSchedule);
    }

    public void approvalSchedule(int id, ApprovalStatus status) {
        WorkSchedule existingSchedule = workScheduleRepository.findById(id).orElseThrow();
        existingSchedule.setStatus(status);
        workScheduleRepository.save(existingSchedule);
    }

    public WorkSchedule findScheduleForAccountAndDate(Account account, LocalDate date, List<WorkSchedule> weeklySchedules) {
        for (WorkSchedule schedule : weeklySchedules) {
            if (schedule.getAccount().equals(account) && schedule.getDate().equals(date)) {
                return schedule;
            }
        }

        return null;
    }

    public WorkSchedule findById(int id) {
        return workScheduleRepository.findById(id).orElse(null);
    }

    public List<WorkSchedule> findScheduleForShiftAndDate(int shiftId, LocalDate date) {
        List<WorkSchedule> schedules = workScheduleRepository.findScheduleForShiftAndDate(shiftId, date);

        if(schedules != null && !schedules.isEmpty()) {
            return schedules;
        }

        return null;
    }
}