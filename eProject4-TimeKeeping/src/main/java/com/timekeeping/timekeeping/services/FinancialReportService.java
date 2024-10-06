package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.models.FinancialReport;
import com.timekeeping.timekeeping.models.Recruitment;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import com.timekeeping.timekeeping.repositories.ActivityRepository;
import com.timekeeping.timekeeping.repositories.FinancialReportRepository;
//import com.timekeeping.timekeeping.repositories.RecruitmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinancialReportService {
    @Autowired
    private FinancialReportRepository financialReportRepository;

    public List<FinancialReport> getAllReports() {
        return financialReportRepository.findAll();
    }

    public Optional<FinancialReport> getReportById(int id) {
        return financialReportRepository.findById(id);
    }

    public List<FinancialReport> findByTitle(String title) {
        return financialReportRepository.findByTitle("%" + title + "%");
    }

    public FinancialReport saveReport(FinancialReport report) {
        return financialReportRepository.save(report);
    }

    public void deleteReport(int id) {
        financialReportRepository.deleteById(id);
    }
}