package com.cpwr.gdo.simulator.dao.repository.security;

import com.cpwr.gdo.simulator.model.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AuthorityRepository  extends JpaRepository<Authority, Long>, JpaSpecificationExecutor<Authority> {
    @Query("select a from Authority a where a.authority=?1")
    Authority findByAuthority(final String name);

}
