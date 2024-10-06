package com.timekeeping.timekeeping.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int candidateID;
    private String profilePicture;
    private String profilePicturePath;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull
    private String description;
    @NotNull
    private String requirements;
    @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number must be exactly 10 digits and optionally start with a '+'")
    private String phoneNumber;
    private String gender;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    @NotBlank(message = "Resume is required")
    private String resume;
    @NotNull
    private String location;
    @NotNull
    private LocalDate candidateDate;
    @NotBlank(message = "Status is required")// Path to resume file
    private String status; // e.g., Applied, Interviewed, Hired, Rejected

    // Getters and Setters

    public Candidate() {
    }



    public Candidate(int candidateID, String profilePicture, String profilePicturePath, String firstName, String lastName, String email, String description, String requirements, String phoneNumber, String gender, Date dateOfBirth,  String  resume, String location, LocalDate  candidateDate, String status) {
        this.candidateID = candidateID;
        this.profilePicture = profilePicture;
        this.profilePicturePath = profilePicturePath;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.description = description;
        this.requirements = requirements;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.resume = resume;
        this.location = location;
        this.candidateDate = candidateDate;
        this.status = status;
    }


    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public  String  getResume() {
        return resume;
    }

    public void setResume( String  resume) {
        this.resume = resume;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
        this.candidateID = candidateID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate  getCandidateDate() {
        return candidateDate;
    }

    public void setCandidateDate(LocalDate  candidateDate) {
        this.candidateDate = candidateDate;
    }



}