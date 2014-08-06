package com.cpwr.gdo.simulator;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpwr.gdo.simulator.dao.repository.WsFailedRequestRepository;
import com.cpwr.gdo.simulator.dao.repository.WsRuleRepository;
import com.cpwr.gdo.simulator.dao.repository.WsServiceRepository;
import com.cpwr.gdo.simulator.dao.repository.security.AuthorityRepository;
import com.cpwr.gdo.simulator.model.FailedRequest;
import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.service.AuthDetailsService;

/**
 * The class populates the test data in the database for tests.
 * 
 */
@Service
public final class TestBootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(TestBootstrap.class);

    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private WsFailedRequestRepository failedRequestRepository;

    @Autowired
    private WsServiceRepository wsServiceRepository;

    @Autowired
    private WsRuleRepository wsRuleRepository;

    @Autowired
    private AuthDetailsService service;

    @PostConstruct
    @Transactional
    private void loadTestData() {
        LOG.debug("loadTestData...");

        checkNotNull(authorityRepository, "AuthorityRepository must not be null!");
        checkNotNull(failedRequestRepository, "WsFailedRequestRepository must not be null!");
        checkNotNull(wsServiceRepository, "wsServiceRepository must not be null!");
        checkNotNull(wsRuleRepository, "wsRuleRepository must not be null!");
        checkNotNull(service, "AuthDetailsService must not be null!");

        /* load failed request test data */
        loadTestFailedRequestData();
        loadTestRecords();
        loadTestUsers();
    }

    private void loadTestFailedRequestData() {
        LOG.debug("loadTestFailedRequestData...");

        final long testFailedRequestAmount = failedRequestRepository.count();

        if (testFailedRequestAmount < 50) {
            for (int i = 1; i < 5; i++) {
                FailedRequest failedRequest = new FailedRequest("", "", "");
                failedRequest.setRequestType("failedrequestType" + i);
                failedRequest.setRequest("<request>value=" + i + "</request>");
                failedRequestRepository.saveAndFlush(failedRequest);
            }
        }

    }

    private void loadTestRecords() {
        LOG.debug("loadTestRecords...");

        com.cpwr.gdo.simulator.model.Service service = new com.cpwr.gdo.simulator.model.Service();
        service.setRequestType("WARRANTY");
        service.setDescription("WARRANTY DESCRIPTION");
        wsServiceRepository.saveAndFlush(service);

        service = new com.cpwr.gdo.simulator.model.Service();
        service.setRequestType("RECALL");
        service.setDescription("RECALL DESCRIPTION");
        wsServiceRepository.saveAndFlush(service);

        service = new com.cpwr.gdo.simulator.model.Service();
        service.setRequestType("GIF_WARRANTY");
        service.setDescription("GIF_WARRANTY DESCRIPTION");
        wsServiceRepository.saveAndFlush(service);

        Rule rule = new Rule();
        rule.setName("WARRANTY_DEFAULT_RULE");
        rule.setDescription("WARRANTY_DEFAULT_RULE_DESCRIPTION");
        rule.setService(wsServiceRepository.findServiceByRequestType("WARRANTY"));
        rule.setResponse("WARRANTY_DEFAULT_RESPONSE");
        rule.getXpathRuleData().setXpathExpression("//Warranty/VIN");
        rule.getXpathRuleData().setXpathValue("123");
        rule.getXpathRuleData().setExpressionBoolean(false);

        wsRuleRepository.saveAndFlush(rule);

        rule = new Rule();
        rule.setName("WARRANTY_RULE");
        rule.setDescription("WARRANTY_RULE_DESCRIPTION");
        rule.setService(wsServiceRepository.findServiceByRequestType("WARRANTY"));
        rule.setResponse("WARRANTY_RESPONSE");
        rule.getXpathRuleData().setXpathExpression("//Warranty/VIN");
        rule.getXpathRuleData().setXpathValue("567");
        rule.getXpathRuleData().setExpressionBoolean(false);

        wsRuleRepository.saveAndFlush(rule);

        rule = new Rule();
        rule.setName("GIF_WARRANTY_RULE");
        rule.setDescription("GIF_WARRANTY_RULE_DESCRIPTION");
        rule.setService(wsServiceRepository.findServiceByRequestType("GIF_WARRANTY"));
        rule.setResponse("GIF_WARRANTY_RULE_RESPONSE");
        rule.getXpathRuleData().setXpathExpression("//Warranty/VIN");
        rule.getXpathRuleData().setXpathValue("890");
        rule.getXpathRuleData().setExpressionBoolean(false);

        wsRuleRepository.saveAndFlush(rule);
    }

    private void loadTestUsers() {
        LOG.debug("loadTestUsers...");

        for (int i = 100; i < 110; i++) {
            service.createUser("Test user " + i, "test" + i + "@mail.ru", "test" + i, "test" + i);
        }
    }
}
