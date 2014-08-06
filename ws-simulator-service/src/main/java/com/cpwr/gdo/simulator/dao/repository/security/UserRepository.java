package com.cpwr.gdo.simulator.dao.repository.security;

import com.cpwr.gdo.simulator.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("select u from User u where u.username=?1")
    User findUserByUsername(final String username);
}
