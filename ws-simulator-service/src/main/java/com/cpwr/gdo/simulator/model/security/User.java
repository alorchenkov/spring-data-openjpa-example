package com.cpwr.gdo.simulator.model.security;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cpwr.gdo.simulator.model.AbstractEntity;
import com.google.common.base.Objects;

@Entity
@Table(name = "users")
@SequenceGenerator(name = "user_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
public final class User extends AbstractEntity<Long> implements UserDetails, Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_gen")
    private Long id;

    @Column(name = "last_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "user_name", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Set<Authority> grantedAuthorities = new HashSet<Authority>();

    public void addAuthority(Authority auth) {
        if (!getAuthorities().contains(auth)) {
            ((Set<GrantedAuthority>) getAuthorities()).add(auth);
        }

    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    // @Override
    public String getPassword() {
        return password;
    }

    // @Override
    public String getUsername() {
        return username;
    }

    // @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return toStringHelper(this).addValue(id).addValue(username).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;

        return Objects.equal(this.id, other.id) && Objects.equal(this.username, other.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.username);
    }
}
