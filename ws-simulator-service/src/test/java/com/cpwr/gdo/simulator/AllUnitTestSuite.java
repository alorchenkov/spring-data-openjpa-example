package com.cpwr.gdo.simulator;

import com.cpwr.gdo.simulator.dao.repository.security.TestAuthRepositories;
import com.cpwr.gdo.simulator.service.TestAuthDetailsService;
import com.cpwr.gdo.simulator.service.TestWsSimulatorService;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cpwr.gdo.simulator.dao.repository.TestEmbeddedDBWsServiceRepository;
import com.cpwr.gdo.simulator.utils.TestXPathUtils;

@RunWith(Suite.class)
@SuiteClasses({TestEmbeddedDBWsServiceRepository.class, TestWsSimulatorService.class,
        TestXPathUtils.class, TestAuthRepositories.class, TestAuthDetailsService.class})
public class AllUnitTestSuite {

}
