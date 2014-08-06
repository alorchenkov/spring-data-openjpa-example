package com.cpwr.gdo.simulator;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cpwr.gdo.simulator.model.security.User;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-application-context.xml" })
//@ContextConfiguration(classes = { SimulatorServiceConfig.class, DataSourceConfig.class } , loader=AnnotationConfigContextLoader.class)
//@ActiveProfiles("dev")
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class AbstractSpringTest extends UnitTestBase {

    @BeforeClass
    public static void authenticate() {
        User user = new User();
        user.setUsername("WS_SIMULATOR");
        user.setPassword("WS_SIMULATOR");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
