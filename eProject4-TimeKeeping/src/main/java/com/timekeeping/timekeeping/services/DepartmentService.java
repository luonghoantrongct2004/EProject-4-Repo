package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Department;
import com.timekeeping.timekeeping.models.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Department> findAll() {
        TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Department d", Department.class);
        return query.getResultList();
    }
    public Department findById(int id) {
        return entityManager.find(Department.class, id);
    }
}
