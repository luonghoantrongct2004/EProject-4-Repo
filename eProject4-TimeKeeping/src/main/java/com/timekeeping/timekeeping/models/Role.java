package com.timekeeping.timekeeping.models;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleID;
    private String name;
    private boolean active;

    public Role() {
    }

    public Role(int roleID, String name, boolean active) {
        this.roleID = roleID;
        this.name = name;
        this.active = active;
    }
    @Override
    public String getAuthority() {
        return name; // Return the role name as the authority
    }
    public boolean getActive(){
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public Role(boolean active) {
        this.active = active;
    }
    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }
}
