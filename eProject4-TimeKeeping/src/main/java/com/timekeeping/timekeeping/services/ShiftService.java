package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Shift;
import com.timekeeping.timekeeping.repositories.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShiftService {
    @Autowired
    private ShiftRepository shiftRepository;

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public Optional<Shift> getShiftById(int id) {
        return shiftRepository.findById(id);
    }

    public Shift saveShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    public void deleteShift(int id) {
        shiftRepository.deleteById(id);
    }

    public List<Shift> findShiftsByName(String shiftName) {
        return shiftRepository.findByShiftName("%" + shiftName + "%");
    }
}
