package com.timekeeping.timekeeping.models;

import com.timekeeping.timekeeping.enums.ApprovalStatus;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@NamedQuery(name = "FinancialReport.findByTitle", query = "SELECT f FROM FinancialReport f WHERE f.title LIKE :title")
public class FinancialReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(columnDefinition = "NTEXT")
    private String content;
    private double totalSalaries;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "NVARCHAR(255)")
    private ApprovalStatus status;

    // Constructors
    public FinancialReport() {}

    public FinancialReport(int id, String title, String content, double totalSalaries, Account account,
                           LocalDate startDate, LocalDate endDate, LocalDate createdAt, ApprovalStatus status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.totalSalaries = totalSalaries;
        this.account = account;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getTotalSalaries() {
        return totalSalaries;
    }

    public void setTotalSalaries(double totalSalaries) {
        this.totalSalaries = totalSalaries;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public ApprovalStatus getStatus() { return status; }

    public void setStatus(ApprovalStatus status) { this.status = status; }
}
