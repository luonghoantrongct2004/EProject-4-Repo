package com.timekeeping.timekeeping.services;


import com.timekeeping.timekeeping.models.Requestion;
import com.timekeeping.timekeeping.repositories.RequestionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RequestionService {
    @Autowired
    private EntityManager em;

    @Autowired
    private RequestionRepository requestionRepository;


    public List<Requestion> getAllRequestions() {return requestionRepository.findAll();}

    public Optional<Requestion> getRequestionById(int id) {return requestionRepository.findById(id);}

    public Requestion saveRequestion(Requestion requestion) {return requestionRepository.save(requestion);}

    public List<Requestion> filterByDateRange(Date startDate, Date endDate) {
        String queryStr = "SELECT r FROM Requestion r WHERE r.startDate >= :startDate AND r.endDate <= :endDate";
        TypedQuery<Requestion> query = em.createQuery(queryStr, Requestion.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    public void createRequestion(Requestion requestion) {
        requestionRepository.save(requestion);
    }

    public void updateRequestion(Requestion requestion) {
        requestionRepository.save(requestion);
    }
}
