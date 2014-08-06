package com.cpwr.gdo.simulator.model.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.cpwr.gdo.simulator.model.AbstractEntity;
import com.google.common.base.Objects;

/**
 */
@Entity
@Table(name = "authorities")
@SequenceGenerator(name = "authority_id_gen", sequenceName = "authorities_id_seq", allocationSize = 1)
public final class Authority extends AbstractEntity<Long> implements GrantedAuthority, Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_id_gen")
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // @Enumerated(value = EnumType.STRING)
    // @Index(name = "authname")
    @Column(name = "authority", unique = true)
    private String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

    public Authority() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this).addValue(id).addValue(authority).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Authority other = (Authority) obj;

        return Objects.equal(this.id, other.id) && Objects.equal(this.authority, other.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.authority);
    }
}
