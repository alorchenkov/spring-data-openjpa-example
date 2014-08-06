package com.cpwr.gdo.simulator.dao.repository.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.cpwr.gdo.simulator.AbstractSpringTest;
import com.cpwr.gdo.simulator.model.security.Authority;
import com.cpwr.gdo.simulator.model.security.AuthorityEnum;
import com.cpwr.gdo.simulator.model.security.User;

/**
 * The TestAuthRepositories: DAO tests on the embedded DB.
 *
 */

@Transactional
public class TestAuthRepositories extends AbstractSpringTest {
    private static final Logger LOG = Logger.getLogger(TestAuthRepositories.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Before
    public void setUp() throws Exception {
        final Authority authority1 = authorityRepository.findByAuthority(AuthorityEnum.WS_SUPERVISOR.toString());
        // the second role
        final Authority authority2 = authorityRepository.findByAuthority(AuthorityEnum.WS_USER.toString());
        assertNotNull(authority2);

        final Authority authority3 = authorityRepository.findByAuthority(AuthorityEnum.WS_ADMIN.toString());
        assertNotNull(authority3);

        // create new user
        User user = new User();
        user.setUsername("test2");
        user.setPassword("test2");
        // add new roles
        user.addAuthority(authority1);
        user.addAuthority(authority2);
        user.addAuthority(authority3);

        userRepository.saveAndFlush(user);

        user = new User();
        user.setUsername("test1");
        user.setPassword("test1");
        // add new roles
        user.addAuthority(authority3);

        userRepository.saveAndFlush(user);
    }

    @Test
    public void testFindUserByUsername() throws Exception {
        User user = userRepository.findUserByUsername("test1");

        assertNotNull(user);
        assertEquals("test1", user.getUsername());

        Authority authority3 = authorityRepository.findByAuthority(AuthorityEnum.WS_USER.toString());
        assertNotNull(authority3);

        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) user.getAuthorities();
        assertEquals(1, authorities.size());
        for (GrantedAuthority auth : authorities) {
            assertEquals(AuthorityEnum.WS_ADMIN.toString(), auth.getAuthority());
        }
    }

    @Test
    public void testNewUser() throws Exception {
        User user = userRepository.findUserByUsername("test2");

        assertNotNull(user);
        assertEquals("test2", user.getUsername());

        assertEquals(3, user.getAuthorities().size());

        Authority authority = authorityRepository.findByAuthority(AuthorityEnum.WS_SUPERVISOR.toString());
        assertNotNull(authority);

        Iterator<GrantedAuthority> iterator = ((Set<GrantedAuthority>) user.getAuthorities()).iterator();
        while (iterator.hasNext()) {
            GrantedAuthority auth = iterator.next();
            if (StringUtils.equalsIgnoreCase(AuthorityEnum.WS_USER.toString(), auth.getAuthority())) {
                iterator.remove();
            }
        }

        userRepository.saveAndFlush(user);

        User user1 = userRepository.findUserByUsername("test2");

        LOG.info("created date: " + user1.getCreatedBy() + " " + user1.getCreatedDate());

        assertNotNull(user1);
        assertEquals("test2", user1.getUsername());

        Set<GrantedAuthority> authorities1 = (Set<GrantedAuthority>) user1.getAuthorities();
        assertEquals(2, authorities1.size());

        for (GrantedAuthority auth : authorities1) {
            LOG.info(auth.toString());
            // Assert.assertEquals(AuthorityEnum.WS_SUPERVISOR.toString(),
            // auth.getAuthority());
        }

        authority = authorityRepository.findByAuthority(AuthorityEnum.WS_USER.toString());
        assertNotNull(authority);

        authority = authorityRepository.findByAuthority(AuthorityEnum.WS_SUPERVISOR.toString());
        assertNotNull(authority);

        authority = authorityRepository.findByAuthority(AuthorityEnum.WS_ADMIN.toString());
        assertNotNull(authority);
    }

    @Override
    public Logger getLog() {
        return LOG;
    }
}
