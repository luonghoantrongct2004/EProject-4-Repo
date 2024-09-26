package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @PersistenceContext
    private EntityManager entityManager;

    // Find all departments
    public List<Department> findAll() {
        TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Department d", Department.class);
        return query.getResultList();
    }

    // Find department by ID
    public Department findById(int id) {
        return entityManager.find(Department.class, id);
    }

    // Create or update a department
    @Transactional
    public void save(Department department) {
        if (department.getId() == 0) {
            entityManager.persist(department);
        } else {
            entityManager.merge(department);
        }
    }

    // Delete department by ID
    @Transactional
    public void delete(int id) {
        Department department = findById(id);
        if (department != null) {
            entityManager.remove(department);
        }
    }
}
