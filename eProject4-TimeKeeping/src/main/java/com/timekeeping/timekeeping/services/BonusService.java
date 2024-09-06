package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Bonus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BonusService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Bonus bonus) {
        if (bonus.getBonusID() == 0) {
            // If the bonus ID is 0, persist the new bonus
            entityManager.persist(bonus);
        } else {
            // If the bonus ID is not 0, it's an existing bonus, so we merge it
            entityManager.merge(bonus);
        }
    }
    public List<Bonus> findByType(String type) {
        return entityManager.createQuery("FROM Bonus WHERE bonusType LIKE :type", Bonus.class)
                .setParameter("type", "%" + type + "%")
                .getResultList();
    }
    @Transactional
    public void delete(int id) {
        Bonus bonus = entityManager.find(Bonus.class, id);
        if (bonus != null) {
            entityManager.remove(bonus);
        }
    }

    public Bonus findById(int id) {
        return entityManager.find(Bonus.class, id);
    }

    public List<Bonus> findAll() {
        return entityManager.createQuery("SELECT b FROM Bonus b", Bonus.class).getResultList();
    }

}
