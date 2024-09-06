package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Position;
import com.timekeeping.timekeeping.models.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Position> findAll() {
        TypedQuery<Position> query = entityManager.createQuery("SELECT p FROM Position p", Position.class);
        return query.getResultList();
    }
    public Position findById(int id) {
        return entityManager.find(Position.class, id);
    }

}
