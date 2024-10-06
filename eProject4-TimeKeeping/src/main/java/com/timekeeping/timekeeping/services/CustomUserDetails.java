package com.timekeeping.timekeeping.services;
import com.timekeeping.timekeeping.models.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails extends User {

    private Account account;
    @Override
    public String getUsername() {
        return account.getUsername();  // Or any other field
    }

    public CustomUserDetails(Account account) {
        // Pass the username, password, and authorities to the User class
        super(account.getUsername(), account.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        // Returning authorities as a collection of GrantedAuthority
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public Account getAccount() {
        return account;
    }
}
