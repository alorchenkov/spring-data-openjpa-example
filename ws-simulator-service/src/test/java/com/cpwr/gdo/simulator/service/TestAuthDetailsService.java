package com.cpwr.gdo.simulator.service;

/**

 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.cpwr.gdo.simulator.AbstractSpringTest;
import com.cpwr.gdo.simulator.dao.repository.security.AuthorityRepository;
import com.cpwr.gdo.simulator.model.security.Authority;
import com.cpwr.gdo.simulator.model.security.AuthorityEnum;
import com.cpwr.gdo.simulator.model.security.User;

/**
 * The TestAuthRepositories: DAO tests on the embedded DB.
 *
 */

// @ContextConfiguration(classes = { SimulatorServiceConfig.class,
// DataSourceConfig.class } , loader=AnnotationConfigContextLoader.class)
// @ContextConfiguration(locations = { "classpath:test-application-context.xml",
// "classpath:test-security-context.xml" })
// @ActiveProfiles("dev")
// @Transactional
public class TestAuthDetailsService extends AbstractSpringTest {
    /**
     * The Constant LOG.
     */
    private static final Logger LOG = Logger.getLogger(TestAuthDetailsService.class.getName());

    @Autowired
    @Qualifier("authService")
    private AuthDetailsService service;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    public void testCreateNewUser() {
        service.createUser("Test user 11", "test@mail.ru", "test11", "test11");
        service.createUser("Test user 12", "test12@mail.ru", "test12", "test12");

        User user = (User) service.loadUserByUsername("test11");
        User user2 = (User) service.loadUserByUsername("test12");

        assertNotNull(user);
        assertEquals(1, user.getAuthorities().size());
        assertEquals(AuthorityEnum.WS_USER.toString(), user.getAuthorities().iterator().next().getAuthority());

        assertNotNull(user2);
        assertEquals(1, user2.getAuthorities().size());
        assertEquals(AuthorityEnum.WS_USER.toString(), user2.getAuthorities().iterator().next().getAuthority());

    }

    @Test
    public void removeRoleFromUser() {
        service.createUser("Test user 13", "test@mail.ru", "test13", "test13");
        User user = (User) service.loadUserByUsername("test13");

        assertNotNull(user);
        assertEquals(1, user.getAuthorities().size());
        assertEquals(AuthorityEnum.WS_USER.toString(), user.getAuthorities().iterator().next().getAuthority());

        service.excludeUserFromRole(user.getId(), AuthorityEnum.WS_USER);
        user = (User) service.loadUserByUsername("test13");
        assertEquals(0, user.getAuthorities().size());

        Authority authority3 = authorityRepository.findByAuthority(AuthorityEnum.WS_USER.toString());
        assertNotNull(authority3);
    }

    @Override
    public Logger getLog() {
        return LOG;
    }
}
