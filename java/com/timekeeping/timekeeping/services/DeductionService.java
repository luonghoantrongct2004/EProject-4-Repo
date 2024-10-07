package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Deduction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeductionService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Deduction> findAll() {
        return entityManager.createQuery("FROM Deduction", Deduction.class).getResultList();
    }
    public List<Deduction> findByType(String type) {
        return entityManager.createQuery("FROM Deduction WHERE deductionType LIKE :type", Deduction.class)
                .setParameter("type", "%" + type + "%")
                .getResultList();
    }

    public Deduction findById(int id) {
        return entityManager.find(Deduction.class, id);
    }

    public void save(Deduction deduction) {
        if (deduction.getDeductionID() == 0) {
            entityManager.persist(deduction);
        } else {
            entityManager.merge(deduction);
        }
    }

    public void delete(int id) {
        Deduction deduction = entityManager.find(Deduction.class, id);
        if (deduction != null) {
            entityManager.remove(deduction);
        }
    }
}
