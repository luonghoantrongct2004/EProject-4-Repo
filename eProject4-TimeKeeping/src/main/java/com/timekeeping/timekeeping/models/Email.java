package com.timekeeping.timekeeping.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    @NotBlank(message = "Email cannot be blank")
    private String toEmail;
    @NotBlank(message = "subject cannot be blank")
    private String subject;
    @NotBlank(message = "body cannot be blank")
    private String body;
}
