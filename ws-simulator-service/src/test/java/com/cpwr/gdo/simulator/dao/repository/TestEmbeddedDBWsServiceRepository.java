package com.cpwr.gdo.simulator.dao.repository;

import static com.cpwr.gdo.simulator.dao.repository.ServiceSpecifications.isActive;
import static com.cpwr.gdo.simulator.dao.repository.ServiceSpecifications.requestTypeIsLike;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.cpwr.gdo.simulator.AbstractSpringTest;
import com.cpwr.gdo.simulator.model.FailedRequest;
import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.model.Service;

/**
 * The TestEmbeddedDBWsServiceRepository: DAO tests on the embedded DB.
 *
 */
@Transactional
public class TestEmbeddedDBWsServiceRepository extends AbstractSpringTest {

    private static final Logger LOG = Logger.getLogger(TestEmbeddedDBWsServiceRepository.class.getName());

    /**
     * The simulator dao.
     */
    @Autowired
    private WsServiceRepository wsServiceRepository;

    @Autowired
    private WsRuleRepository wsRuleRepository;

    @Autowired
    private WsFailedRequestRepository wsFailedRequestRepository;

    /**
     * Test create.
     */
    @Test
    public void testCreate() {
        long initialCount = wsServiceRepository.count();
        Service service = new Service();
        service.setRequestType("XMRADIO");
        service.setDescription("XMRADIO DESCRIPTION");
        wsServiceRepository.saveAndFlush(service);

        List<Service> services = wsServiceRepository.findAllServices();

        assertEquals(initialCount + 1, wsServiceRepository.count());
        service = wsServiceRepository.findServiceByRequestType("XMRADIO");
        // Assert.assertEquals("test-auditor", service.getCreatedBy());
        assertEquals("WS_SIMULATOR", service.getCreatedBy());
    }

    /**
     * Test find service by id.
     */
    @Test
    public void testFindServiceById() {
        Long serviceId = wsServiceRepository.findServiceByRequestType("WARRANTY").getId();
        final Service service = wsServiceRepository.findOne(serviceId);

        assertNotNull(service);
        assertEquals("WARRANTY", service.getRequestType());
        assertEquals("WARRANTY DESCRIPTION", service.getDescription());
    }

    @Test
    public void testFindServiceByRequestType() {
        final Service service = wsServiceRepository.findServiceByRequestType("WARRANTY");

        assertNotNull(service);
        assertEquals("WARRANTY", service.getRequestType());
        assertEquals("WARRANTY DESCRIPTION", service.getDescription());
    }

    @Test
    public void testGetAllServices() {
        final List<Service> services = wsServiceRepository.findAll();

        assertEquals(3, services.size());
        assertEquals("WARRANTY", services.get(0).getRequestType());
        assertEquals("WARRANTY DESCRIPTION", services.get(0).getDescription());
    }

    @Test
    public void testDeleteService() {
        Service service = wsServiceRepository.findServiceByRequestType("WARRANTY");

        assertNotNull(service);
        wsServiceRepository
                .setActiveStatusForService("WARRANTY", false, "[id=" + service.getId() + ":deleted]WARRANTY");
        service = wsServiceRepository.findServiceByRequestType("WARRANTY");
        Assert.assertNull(service);
    }

    @Test
    public void testFindServicesByRequestType() {
        List<Service> services = wsServiceRepository.findAll(Specifications.where(requestTypeIsLike("WARRANTY")).and(
                isActive()));
        assertEquals(2, services.size());
        LOG.info(services.get(0).getRequestType());
        LOG.info(services.get(1).toString());
        assertEquals("GIF_WARRANTY", services.get(1).getRequestType());
        assertEquals("GIF_WARRANTY DESCRIPTION", services.get(1).getDescription());
    }

    /**
     * Test find rule by id.
     */
    @Test
    public void testFindRuleById() {
        List<Rule> rules = wsRuleRepository.findAll();
        Long ruleId = rules.get(0).getId();
        Long serviceId = rules.get(0).getService().getId();

        Rule rule = wsRuleRepository.findOne(ruleId);
        assertEquals("WARRANTY_DEFAULT_RULE", rule.getName());
        assertEquals("WARRANTY_DEFAULT_RULE_DESCRIPTION", rule.getDescription());
        assertEquals("//Warranty/VIN", rule.getXpathRuleData().getXpathExpression());
        assertEquals("123", rule.getXpathRuleData().getXpathValue());
        // Assert.assertEquals(new Long(1),
        // rule.getXpathRuleData().getRuleId());
        assertEquals(serviceId, rule.getService().getId());
        assertEquals("WARRANTY_DEFAULT_RESPONSE", rule.getResponse());
    }

    @Test
    public void testFindRulesByRequestType() {
        List<Rule> rules = wsRuleRepository.findAll(Specifications
                .where(RuleSpecifications.requestTypeIsLike("WARRANTY")).and(RuleSpecifications.isServiceActive())
                .and(RuleSpecifications.isActive()));
        Rule rule = rules.get(0);
        LOG.info(rules.get(0).toString());
        LOG.info(rules.get(1).toString());
        // LOG.info(rules.get(2).toString());

        assertEquals(2, rules.size());

        assertEquals("WARRANTY_DEFAULT_RULE", rule.getName());
        assertEquals("WARRANTY_DEFAULT_RULE_DESCRIPTION", rule.getDescription());
        assertEquals("//Warranty/VIN", rule.getXpathRuleData().getXpathExpression());
        assertEquals("123", rule.getXpathRuleData().getXpathValue());
        // Assert.assertEquals(new Long(1),
        // rule.getXpathRuleData().getRuleId());
        // assertEquals(new Long(1), rule.getService().getId());
        assertEquals("WARRANTY_DEFAULT_RESPONSE", rule.getResponse());

        rules = wsRuleRepository.findRulesByRequestType("XMRADIO");
        assertEquals(0, rules.size());
    }

    @Test
    public void testCreateRule() {
        Rule rule = new Rule();
        rule.setName("TestNAME");
        rule.setDescription("TestDescription");
        rule.setService(wsServiceRepository.findOne(3L));
        rule.setResponse("test_response");

        wsRuleRepository.saveAndFlush(rule);

        Rule rule1 = wsRuleRepository.findOne(rule.getRuleId());
        LOG.info("ruleId=" + rule.getId());

        assertEquals("TestNAME", rule1.getName());
    }

    @Test
    public void testCheckIfServiceExists() {
        boolean ind = 1 == wsServiceRepository.count(ServiceSpecifications.requestTypeEquals("XMRADIO"));
        assertFalse(ind);
        ind = 1 == wsServiceRepository.count(ServiceSpecifications.requestTypeEquals("RECALL"));
        assertTrue(ind);
    }

    @Test
    public void testDeleteRule() {
        List<Rule> rules = wsRuleRepository.findRulesByRequestType("GIF_WARRANTY");
        assertEquals(1, rules.size());
        Long ruleId = rules.get(0).getId();

        wsRuleRepository.setActiveStatusForRule(ruleId, false);
        rules = wsRuleRepository.findRulesByRequestType("GIF_WARRANTY");
        assertEquals(0, rules.size());
    }

    @Test
    public void testCheckIfRuleExists() {
        Long serviceId = wsServiceRepository.findServiceByRequestType("WARRANTY").getId();
        List<Service> services = wsServiceRepository.findAllServices();
        Rule rule = new Rule();
        rule.getXpathRuleData().setXpathExpression("//Warranty/VIN");
        rule.getXpathRuleData().setXpathValue("123");
        rule.setService(wsServiceRepository.findOne(serviceId));
        boolean isRuleInList = checkIfRuleExists(rule);
        assertTrue(isRuleInList);

        rule.getXpathRuleData().setXpathExpression("//Warranty/VIN");
        rule.getXpathRuleData().setXpathValue("97899");
        isRuleInList = checkIfRuleExists(rule);
        assertFalse(isRuleInList);

    }

    @Test
    public void testCheckIfRuleNameExists() {
        Long serviceId = wsServiceRepository.findServiceByRequestType("WARRANTY").getId();
        List<Service> services = wsServiceRepository.findAllServices();
        Rule rule = new Rule();
        rule.setService(wsServiceRepository.findOne(serviceId));
        rule.setName("WARRANTY_RULE");
        boolean isRuleInList = checkIfRuleNameExists(rule);
        assertTrue(isRuleInList);

        rule.setName("WARRANTY_TEST_RULE");
        isRuleInList = checkIfRuleNameExists(rule);
        assertFalse(isRuleInList);
    }

    @Test
    public void testUpdateService() {
        Long serviceId = wsServiceRepository.findServiceByRequestType("WARRANTY").getId();

        Service service = wsServiceRepository.findOne(serviceId);
        service.setRequestType("NEW WARRANTY");
        service.setDescription("NEW WARRANTY DESCRIPTION");
        wsServiceRepository.saveAndFlush(service);
        Service updatedService = wsServiceRepository.findOne(serviceId);
        assertEquals("NEW WARRANTY", updatedService.getRequestType());
        assertEquals("NEW WARRANTY DESCRIPTION", updatedService.getDescription());

    }

    @Test
    public void testUpdateRule() {
        List<Rule> rules = wsRuleRepository.findAll();
        Long ruleId = rules.get(rules.size() - 1).getId();
        Rule rule = wsRuleRepository.findOne(ruleId);
        Long serviceId = rule.getService().getId();

        rule.getXpathRuleData().setXpathExpression("new expr");
        rule.getXpathRuleData().setXpathValue("new value");

        rule.setName("new name");
        rule.setDescription("new descr");
        rule.setResponse("new response");

        wsRuleRepository.saveAndFlush(rule);

        Rule updatedRule = wsRuleRepository.findOne(ruleId);
        assertEquals("new name", updatedRule.getName());
        assertEquals("new descr", updatedRule.getDescription());
        assertEquals("new expr", updatedRule.getXpathRuleData().getXpathExpression());
        assertEquals("new value", updatedRule.getXpathRuleData().getXpathValue());
        // Assert.assertEquals(new Long(3),
        // updatedRule.getXpathRuleData().getRuleId());
        assertEquals(serviceId, updatedRule.getService().getId());
        assertEquals("new response", updatedRule.getResponse());
    }

    private boolean checkIfRuleExists(final Rule rule) {
        LOG.info("serviceId=" + rule.getService().getId() + ", e=" + rule.getXpathRuleData().getXpathExpression()
                + ", value=" + rule.getXpathRuleData().getXpathValue());
        return 1 == wsRuleRepository.count(Specifications
                .where(RuleSpecifications.serviceIdIsEqual(rule.getService().getId()))
                .and(RuleSpecifications.xpathExpressionIsEqual(rule.getXpathRuleData().getXpathExpression()))
                .and(RuleSpecifications.xpathValueIsEqual(rule.getXpathRuleData().getXpathValue()))

        );
    }

    private boolean checkIfRuleNameExists(final Rule rule) {
        return 1 == wsRuleRepository.count(Specifications.where(
                RuleSpecifications.serviceIdIsEqual(rule.getService().getId())).and(
                RuleSpecifications.ruleNameIsEqual(rule.getName())));
    }

    @Test
    public void testFailedRequest() throws Exception {
        final FailedRequest failedRequest = new FailedRequest("", "", "");
        failedRequest.setComments("test comments");
        failedRequest.setRequest("failed request xml");
        failedRequest.setRequestType("TEST_WS_FAILED_SERVICE");
        wsFailedRequestRepository.saveAndFlush(failedRequest);

        final FailedRequest newRequest = wsFailedRequestRepository.findOne(failedRequest.getId());
        assertEquals("test comments", newRequest.getComments());

    }

    @Override
    public Logger getLog() {
        return LOG;
    }
}
