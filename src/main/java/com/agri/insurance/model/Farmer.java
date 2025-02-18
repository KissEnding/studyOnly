package com.agri.insurance.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
@Entity
@Table(name = "farmer")
public class Farmer implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(name = "id_card", length = 18)
    private String idCard;

    @Column(length = 200)
    private String location;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(length = 50)
    private String username;

    @Column(length = 100)
    private String password;

    @Column(name = "household_doc", columnDefinition = "MEDIUMTEXT")
    private String householdDoc;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 20)
    private String status = "pending";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_FARMER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "approved".equals(status);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
} 