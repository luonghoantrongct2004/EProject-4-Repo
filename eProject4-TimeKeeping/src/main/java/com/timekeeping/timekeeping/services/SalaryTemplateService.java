package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.SalaryTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaryTemplateService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public SalaryTemplate createSalaryTemplate(SalaryTemplate salaryTemplate) {
        entityManager.persist(salaryTemplate);
        return salaryTemplate;
    }

    @Transactional
    public SalaryTemplate updateSalaryTemplate(SalaryTemplate salaryTemplate) {
        return entityManager.merge(salaryTemplate);
    }
    public SalaryTemplate findById(Long id) {
        SalaryTemplate salaryTemplate = entityManager.find(SalaryTemplate.class, id);
        return salaryTemplate;
    }
    public Optional<SalaryTemplate> findSalaryTemplateById(int salaryID) {
        SalaryTemplate salaryTemplate = entityManager.find(SalaryTemplate.class, salaryID);
        return Optional.ofNullable(salaryTemplate);
    }

    @Transactional
    public void deleteSalaryTemplate(int salaryID) {
        SalaryTemplate salaryTemplate = entityManager.find(SalaryTemplate.class, salaryID);
        if (salaryTemplate != null) {
            entityManager.remove(salaryTemplate);
        }
    }

    public List<SalaryTemplate> findAllSalaryTemplates() {
        return entityManager.createQuery("SELECT st FROM SalaryTemplate st", SalaryTemplate.class).getResultList();
    }
    public List<SalaryTemplate> findByName(String name) {
        return entityManager.createQuery("FROM SalaryTemplate WHERE gradeName LIKE :name", SalaryTemplate.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
}
