package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsernameAndPassword(String username, String password);
    Optional<Account> findByUsername(String username);
    @Query("SELECT a FROM Account a JOIN FETCH a.role")
    List<Account> findAllWithRoles();
    Optional<Account> findByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.role.name = 'Employee'")
    List<Account> findAllEmployees();
    @Query("SELECT a FROM Account a WHERE a.fullName LIKE :fullName AND a.role.name = 'Employee'")
    List<Account> findByNameEmployee(@Param("fullName") String fullName);
}
