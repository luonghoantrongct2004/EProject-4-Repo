package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Position;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    @PersistenceContext
    private EntityManager entityManager;

    // Find all positions
    public List<Position> findAll() {
        TypedQuery<Position> query = entityManager.createQuery("SELECT p FROM Position p", Position.class);
        return query.getResultList();
    }

    // Find a position by ID
    public Position findById(int id) {
        return entityManager.find(Position.class, id);
    }

    // Create a new position
    @Transactional
    public void save(Position position) {
        if (position.getId() == 0) { // If ID is 0, it's a new position
            entityManager.persist(position);
        } else { // Otherwise, it's an existing position that needs to be updated
            entityManager.merge(position);
        }
    }

    // Delete a position by ID
    @Transactional
    public void delete(int id) {
        Position position = findById(id);
        if (position != null) {
            entityManager.remove(position);
        }
    }
}
