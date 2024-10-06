package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Region;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Region> findAll() {
        List<Region> regions = entityManager.createQuery("SELECT r FROM Region r", Region.class).getResultList();
        System.out.println("Regions fetched: " + regions.size()); // Log the number of regions fetched
        return regions;
    }
    public List<Region> findByName(String name) {
        return entityManager.createQuery("FROM Region WHERE regionName LIKE :name", Region.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
    // Method to find a region by its ID
    public Region findById(int regionID) {
        return entityManager.find(Region.class, regionID);
    }

    // Method to save a new region
    @Transactional
    public void save(Region region) {
        if (region.getRegionID() == 0) {
            entityManager.persist(region); // Create new region
        } else {
            entityManager.merge(region); // Update existing region
        }
    }

    // Method to delete a region by its ID
    @Transactional
    public void deleteById(int regionID) {
        Region region = findById(regionID);
        if (region != null) {
            entityManager.remove(region);
        }
    }
    public void delete(int id) {
        Region region = findById(id);
        if (region != null) {
            entityManager.remove(region);
        }
    }
}
