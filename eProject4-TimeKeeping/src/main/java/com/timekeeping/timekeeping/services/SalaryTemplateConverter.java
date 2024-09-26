package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.SalaryTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SalaryTemplateConverter implements Converter<String, SalaryTemplate> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SalaryTemplate convert(String id) {
        try {
            Long salaryTemplateId = Long.valueOf(id);
            return entityManager.find(SalaryTemplate.class, salaryTemplateId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid salaryTemplate ID: " + id, e);
        }
    }
}
