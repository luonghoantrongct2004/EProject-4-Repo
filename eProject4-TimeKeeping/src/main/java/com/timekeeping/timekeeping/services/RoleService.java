package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Role;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;
    public List<Role> findAll() {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r", Role.class);
        return query.getResultList();
    }
    public List<Role> findByName(String name) {
        return entityManager.createQuery("FROM Role WHERE name LIKE :name", Role.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
    public List<Role> findByActive() {
        return entityManager.createQuery("FROM Role WHERE active = true", Role.class)
                .getResultList();
    }
    public Role findById(int id) {
        return entityManager.find(Role.class, id);
    }

    @Transactional
    public void save(Role role) {
        if (role.getRoleID() == 0) {
            entityManager.persist(role); // Creates a new role if it doesn't exist
        } else {
            entityManager.merge(role); // Updates an existing role if it does exist
        }
    }

    @Transactional
    public void delete(int id) {
        Role role = findById(id);
        if (role != null) {
            role.setActive(false); // Assuming you have an `active` field in the Role entity
            entityManager.merge(role);
        }
    }

    @Transactional
    public void activate(int id) {
        Role role = findById(id);
        if (role != null) {
            role.setActive(true); // Assuming you have an `active` field in the Role entity
            entityManager.merge(role);
        }
    }
}
