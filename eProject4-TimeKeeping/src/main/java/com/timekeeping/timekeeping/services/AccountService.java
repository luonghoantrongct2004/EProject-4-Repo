package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.config.SecurityConfig;
import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Role;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import jakarta.persistence.EntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService, ApplicationContextAware {

    private final AccountRepository accountRepository;

    private ApplicationContext applicationContext;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.applicationContext = context;
    }
    @Autowired
    private EntityManager entityManager;
    private PasswordEncoder passwordEncoder;
    public List<Account> findAllById(List<Integer> ids) {
        return accountRepository.findAllById(ids);
    }

    public List<Account> findAllEmployees() {
        return accountRepository.findAllEmployees();
    }

    public List<Account> findByNameEmployee(String fullName) {
        return accountRepository.findByNameEmployee("%" + fullName + "%");
    }
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public List<Account> findByName(String name) {
        return entityManager.createQuery("FROM Account WHERE fullName LIKE :name", Account.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByEmail(email);

        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException("Sai tài khoản hoặc mật khẩu.");
        }

        Account account = accountOptional.get();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        return new CustomUserDetails(
                account
        );
    }

    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public Optional<Account> findByUsernameAndPassword(String username, String rawPassword) {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            // Log mật khẩu nhập vào và mật khẩu mã hóa để kiểm tra
            System.out.println("Raw Password: " + rawPassword);
            System.out.println("Encrypted Password: " + account.getPassword());

            if (passwordEncoder.matches(rawPassword, account.getPassword())) {
                return Optional.of(account);
            }
        }

        return Optional.empty();
    }


    public void save(Account account, Role roleId) {
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        account.setRole(roleId);
        account.setPassword(passwordEncoder.encode(account.getPassword())); // Mã hóa mật khẩu
        accountRepository.save(account);
    }

    public void update(Account account) {
        accountRepository.save(account); // Cập nhật tài khoản sau khi có đường dẫn ảnh
    }
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(int accountId) {
        return accountRepository.findById(accountId);
    }

    public void delete(int id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setStatus("InActive");
            accountRepository.save(account);
        }
    }

    public void turnOn(int id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setStatus("Active");
            accountRepository.save(account);
        }
    }
}
