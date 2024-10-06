package com.timekeeping.timekeeping.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@NamedQuery(name = "Account.findByUsername", query = "SELECT a FROM Account a WHERE a.username = :username")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountID;

    private String status;
    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date hireDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;
    private String phoneNumber;
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String gender;
    @Column(name = "full_name", columnDefinition = "nvarchar(255)")
    private String fullName;
    private int codeBank;
    private String password;
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonIgnore
    private Role role;
    @ManyToOne
    @JoinColumn(name = "salaryid")
    private SalaryTemplate salaryTemplate;

    @Column(name = "image_paths", columnDefinition = "TEXT")
    private String imagePaths;
    public Account() {
    }

    public Account(int accountID,String status, String address, Date hireDate,Department department,
                   Position position,String phoneNumber, String email, Date birthDate, String gender,
                   String fullName, int codeBank, String password, String username, Role role) {
        this.accountID = accountID;
        this.status = status;
        this.address = address;
        this.hireDate = hireDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.fullName = fullName;
        this.codeBank = codeBank;
        this.password = password;
        this.username = username;
        this.role = role;
        this.department = department;
        this.position = position;
    }
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assuming the account has one role, you can map it to a SimpleGrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = new Gson().toJson(imagePaths);
    }

    // Hàm tiện ích để chuyển từ JSON sang List
    public List<String> getImagePaths() {
        return new Gson().fromJson(this.imagePaths, new TypeToken<List<String>>(){}.getType());
    }
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCodeBank() {
        return codeBank;
    }

    public void setCodeBank(int codeBank) {
        this.codeBank = codeBank;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
    public SalaryTemplate getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }
}