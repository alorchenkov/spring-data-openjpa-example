package com.cpwr.gdo.simulator.service;

import static com.cpwr.gdo.simulator.dao.repository.ServiceSpecifications.isActive;
import static com.cpwr.gdo.simulator.dao.repository.ServiceSpecifications.requestTypeIsLike;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import com.cpwr.gdo.simulator.UnitTestBase;
import com.cpwr.gdo.simulator.dao.repository.WsRuleRepository;
import com.cpwr.gdo.simulator.dao.repository.WsServiceRepository;
import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.model.Service;

/**
 */
public final class TestWsSimulatorService extends UnitTestBase {

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(TestWsSimulatorService.class.getName());

    /** The service. */
    private WsSimulatorService service = new WsSimulatorService();

    /** The simulator dao. */
    private WsServiceRepository mockWsServiceRepository = mock(WsServiceRepository.class);
    private WsRuleRepository mockWsRuleRepository = mock(WsRuleRepository.class);

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        service.setWsServiceRepository(mockWsServiceRepository);
        service.setWsRuleRepository(mockWsRuleRepository);
    }

    /**
     * Test create new empty rule.
     */
    @Test
    public void testCreateNewEmptyRule() {
        Rule rule = service.createNewEmptyRule();

        assertNotNull(rule);
        assertNotNull(rule.getService());
        assertNotNull(rule.getXpathRuleData());
    }

    /**
     * Test get all services.
     */
    @Test
    public void testGetAllServices() {
        List<Service> mockServices = new ArrayList<Service>();
        mockServices.add(new Service());
        mockServices.add(new Service());

        when(mockWsServiceRepository.findAll(any(Specifications.class), any(Sort.class))).thenReturn(mockServices);

        List<Service> services = service.getAllServices();

        verify(mockWsServiceRepository, times(1)).findAll(any(Specifications.class), any(Sort.class));

        assertNotNull(services);
        assertEquals(2, services.size());
    }

    /**
     * Test get rules for service.
     */
    @Test
    public void testGetRulesForService() {
        List<Rule> mockRules = new ArrayList<Rule>();
        mockRules.add(new Rule());
        mockRules.add(new Rule());
        when(mockWsRuleRepository.findRulesByRequestType(anyString())).thenReturn(mockRules);

        List<Rule> rules = service.getRulesForService("test");

        verify(mockWsRuleRepository, times(1)).findRulesByRequestType(anyString());

        assertNotNull(rules);
        assertEquals(2, rules.size());

    }

    /**
     * Test create service.
     */
    @Test
    public void testCreateService() {
        Mockito.doReturn(new Service()).when(mockWsServiceRepository).saveAndFlush(any(Service.class));

        Service webService = new Service();
        service.createService(webService);

        verify(mockWsServiceRepository, times(1)).saveAndFlush(any(Service.class));
    }

    /**
     * Test create rule.
     */
    @Test
    public void testCreateRule() {
        Mockito.doReturn(new Rule()).when(mockWsRuleRepository).saveAndFlush(any(Rule.class));

        Rule rule = service.createNewEmptyRule();
        rule.getXpathRuleData().setXpathValue("value");
        service.createRule(rule);

        verify(mockWsRuleRepository, times(1)).saveAndFlush(any(Rule.class));

        assertFalse(rule.getXpathRuleData().isExpressionBoolean());

    }

    /**
     * Test get response payload.
     */
    @Test
    public void testGetResponsePayload() {
        final String requestType = "GWMWarranty";
        final String xpathExpr = "//Warranty/VIN";
        final String xpathValue1 = "1";
        final String xpathValue2 = "2";
        final String xpathValue3 = "1G4HD57M29U129307";

        final String response1 = "response1";
        final String response2 = "response2";
        final String validResponse = "valid_response";

        final String requestXML = "<ns3:GetWarrantyRequest "
                + " xmlns:ns2=\"http://schema.gm.com/GIF/GSSM/GWM/Warranty/Impl\""
                + " xmlns=\"http://schema.gm.com/GIF/GSSM/GWM/Warranty/TransactionHeader\""
                + " xmlns:ns3=\"http://wsdl.gm.com/GIF/GSSM/GWM/Warranty/Impl\">"
                + "<TransactionHeader><Action>GET_WARRANTY</Action>" + "<MessageID>Dynamic</MessageID>"
                + "<SourceApplication>NGDOE</SourceApplication>" + "<TargetApplication>GWM</TargetApplication>"
                + "<TargetComponent>GWM warranty</TargetComponent>" + "</TransactionHeader>" + "<ns2:Warranty>"
                + "<ns2:VIN>1G4HD57M29U129307</ns2:VIN>" + "<ns2:Odometer MilageUOM=\"K\">10000</ns2:Odometer>"
                + "</ns2:Warranty></ns3:GetWarrantyRequest>";

        Rule rule1 = service.createNewEmptyRule();
        rule1.setResponse(response1);
        rule1.getXpathRuleData().setXpathExpression(xpathExpr);
        rule1.getXpathRuleData().setXpathValue(xpathValue1);

        Rule rule2 = service.createNewEmptyRule();
        rule2.setResponse(response2);
        rule2.getXpathRuleData().setXpathExpression(xpathExpr);
        rule2.getXpathRuleData().setXpathValue(xpathValue2);

        Rule rule3 = service.createNewEmptyRule();
        rule3.setResponse(validResponse);
        rule3.getXpathRuleData().setXpathExpression(xpathExpr);
        rule3.getXpathRuleData().setXpathValue(xpathValue3);

        List<Rule> mockRules = new ArrayList<Rule>();
        mockRules.add(rule1);
        mockRules.add(rule2);
        mockRules.add(rule3);

        when(mockWsRuleRepository.findRulesByRequestType(Mockito.eq(requestType))).thenReturn(mockRules);

        String response = service.getResponsePayload(requestType, requestXML.getBytes());

        assertNotNull(StringUtils.trimToNull(response));
        assertEquals(validResponse, response);

        verify(mockWsRuleRepository, times(1)).findRulesByRequestType(Mockito.eq(requestType));
    }

    /**
     * Test find rule by rule id.
     */
    @Test
    public void testFindRuleByRuleId() {
        when(mockWsRuleRepository.findOne(Mockito.anyLong())).thenReturn(new Rule());

        Rule rule = service.findRuleByRuleId(1L);

        assertNotNull(rule);

        verify(mockWsRuleRepository, times(1)).findOne(Mockito.anyLong());
    }

    /**
     * Test find services by request type.
     */
    @Test
    public void testFindServicesByRequestType() {
        List<Service> mockServices = new ArrayList<Service>();
        mockServices.add(new Service());
        mockServices.get(0).setDescription("TEST MOCK DESCR");

        when(
                mockWsServiceRepository.findAll(Specifications.where(requestTypeIsLike(Mockito.eq("WARRANTY"))).and(
                        isActive()))).thenReturn(new ArrayList<Service>());

        List<Service> services = service.findServicesByRequestType("WARRANTY");

        assertNotNull(services);
        // Assert.assertSame(services, mockServices);
        // Assert.assertEquals(1, services.size());
        // Assert.assertEquals("TEST MOCK DESCR",
        // services.get(0).getDescription());

        // Mockito.verify(mockWsServiceRepository,
        // Mockito.times(1)).findAll(Specifications.where(requestTypeIsLike(Mockito.eq("WARRANTY"))).and(isActive()));
    }

    /**
     * Test find service by request type.
     */
    @Test
    public void testFindServiceByRequestType() {
        Service mockService = new Service();
        when(mockWsServiceRepository.findServiceByRequestType("WARRANTY")).thenReturn(mockService);

        Service result = service.findServiceByRequestType("WARRANTY");
        assertNotNull(result);
        Assert.assertSame(result, mockService);
        verify(mockWsServiceRepository, times(1)).findServiceByRequestType("WARRANTY");
    }

    /**
     * Test delete rule by id.
     */
    @Test
    public void testDeleteRuleById() {
        final Rule mockRule = new Rule();
        mockRule.setId(1L);

        when(mockWsRuleRepository.findOne(1L)).thenReturn(mockRule);
        // Mockito.when(mockWsRuleRepository.setActiveStatusForRule(1L,
        // false)).thenReturn(1);
        when(mockWsRuleRepository.save(mockRule)).thenReturn(mockRule);

        service.deleteRuleById(1L);

        // Mockito.verify(mockWsRuleRepository,
        // Mockito.times(1)).setActiveStatusForRule(1L, false);
        verify(mockWsRuleRepository, times(1)).save(mockRule);
    }

    /**
     * Test find service by id.
     */
    @Test
    public void testFindServiceById() {
        when(mockWsServiceRepository.findOne(Mockito.anyLong())).thenReturn(new Service());

        Service srv = service.findServiceById(1L);
        assertNotNull(srv);

        verify(mockWsServiceRepository, times(1)).findOne(Mockito.anyLong());
    }

    /**
     * Test delete service.
     */
    @Test
    public void testDeleteService() {
        final Service mockService = new Service();
        mockService.setId(1000L);

        when(mockWsServiceRepository.findServiceByRequestType("WARRANTY")).thenReturn(mockService);
        when(
                mockWsServiceRepository.setActiveStatusForService("WARRANTY", false, "[id=" + mockService.getId()
                        + ":deleted]WARRANTY")).thenReturn(1);

        service.deleteService("WARRANTY");

        verify(mockWsServiceRepository, times(1)).setActiveStatusForService("WARRANTY", false,
                "[id=1000:deleted]WARRANTY");
    }

    /**
     * Test get response payload from disk.
     */
    // @Test
    public void testGetResponsePayloadFromDisk() {
        String response = service.getResponsePayload("ServiceRecall", new byte[] {});

        assertNotNull(StringUtils.trimToNull(response));
        assertEquals("<impl:GetRecallResponse></impl:GetRecallResponse>", response);
    }

    /**
     * Test get response payload from disk with exception.
     */
    //@Test
    public void testGetResponsePayloadFromDiskWithException() {
        String response = service.getResponsePayload("ServiceRecall_ERROR", new byte[] {});

        Assert.assertNull(StringUtils.trimToNull(response));
    }

    /**
     * Test get response default payload.
     */
    //@Test
    @Deprecated
    public void testGetResponseDefaultPayload() {
        final String requestType = "GWMWarranty";
        final String xpathExpr = "//Warranty/VIN";
        final String xpathValue1 = "1";
        final String xpathValue2 = StringUtils.EMPTY;
        final String xpathValue3 = "1G4HD57M29U129307";

        final String response1 = "response1";
        final String defaultResponse = "default_response";
        final String response3 = "valid_response";

        final String requestXML = "<ns3:GetWarrantyRequest "
                + " xmlns:ns2=\"http://schema.gm.com/GIF/GSSM/GWM/Warranty/Impl\""
                + " xmlns=\"http://schema.gm.com/GIF/GSSM/GWM/Warranty/TransactionHeader\""
                + " xmlns:ns3=\"http://wsdl.gm.com/GIF/GSSM/GWM/Warranty/Impl\">"
                + "<TransactionHeader><Action>GET_WARRANTY</Action>" + "<MessageID>Dynamic</MessageID>"
                + "<SourceApplication>NGDOE</SourceApplication>" + "<TargetApplication>GWM</TargetApplication>"
                + "<TargetComponent>GWM warranty</TargetComponent>" + "</TransactionHeader>" + "<ns2:Warranty>"
                + "<ns2:VIN>12345678910</ns2:VIN>" + "<ns2:Odometer MilageUOM=\"K\">10000</ns2:Odometer>"
                + "</ns2:Warranty></ns3:GetWarrantyRequest>";

        Rule rule1 = service.createNewEmptyRule();
        rule1.setResponse(response1);
        rule1.getXpathRuleData().setXpathExpression(xpathExpr);
        rule1.getXpathRuleData().setXpathValue(xpathValue1);

        Rule rule2 = service.createNewEmptyRule();
        rule2.setResponse(defaultResponse);
        rule2.getXpathRuleData().setXpathExpression(StringUtils.EMPTY);
        rule2.getXpathRuleData().setXpathValue(xpathValue2);

        Rule rule3 = service.createNewEmptyRule();
        rule3.setResponse(response3);
        rule3.getXpathRuleData().setXpathExpression(xpathExpr);
        rule3.getXpathRuleData().setXpathValue(xpathValue3);

        List<Rule> mockRules = new ArrayList<Rule>();
        mockRules.add(rule1);
        mockRules.add(rule2);
        mockRules.add(rule3);

        when(mockWsRuleRepository.findRulesByRequestType(Mockito.eq(requestType))).thenReturn(mockRules);

        String response = service.getResponsePayload(requestType, requestXML.getBytes());

        assertNotNull(StringUtils.trimToNull(response));
        assertEquals(defaultResponse, response);

        verify(mockWsRuleRepository, times(1)).findRulesByRequestType(Mockito.eq(requestType));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.covisint.UnitTestBase#getLog()
     */
    // @Override
    public java.util.logging.Logger getLog() {
        return LOG;
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testValidateRuleMandatoryFields() throws Exception {
        Rule rule = new Rule();
        rule.setId(1000L);
        rule.setService(new Service());
        rule.getService().setId(5000L);

        when(mockWsRuleRepository.count(any(Specification.class))).thenReturn(0L);

        when(mockWsRuleRepository.findOne(rule.getId())).thenReturn(new Rule());

        List<String> errors = service.validateRule(rule);

        for (String violation : errors) {
            LOG.info(violation);
        }

        assertEquals(3, errors.size());

        assertEquals("Rule Name is mandatory field.", errors.get(0));
        assertEquals("Rule Description is mandatory field.", errors.get(1));
        assertEquals("Response is mandatory field.", errors.get(2));
    }

    @Test
    public void testValidateIfRuleNameExists() throws Exception {
        Rule rule = new Rule();
        rule.setId(null);
        rule.setName("TEST_RULE_NAME");
        rule.setDescription("TEST_RULE_DESCRIPTION");
        rule.setResponse("<test>Test Response</test>");
        rule.setService(new Service());
        rule.getService().setId(5000L);

        when(mockWsRuleRepository.count(any(Specification.class))).thenReturn(1L);

        List<String> errors = service.validateRule(rule);

        for (String violation : errors) {
            LOG.info(violation);
        }

        assertEquals(2, errors.size());

        assertEquals("Rule Name must be unique in database.", errors.get(0));
        assertEquals("The combination of Xpath Expression and Xpath value must be unique in database.", errors.get(1));
    }

    @Test
    public void testValidateIfRuleExists() throws Exception {
        Rule rule = new Rule();
        rule.setId(1000L);
        rule.setName("TEST_RULE_NAME");
        rule.setDescription("TEST_RULE_DESCRIPTION");
        rule.setResponse("<test>Test Response</test>");
        rule.setService(new Service());
        rule.getService().setId(5000L);

        when(mockWsRuleRepository.count(any(Specification.class))).thenReturn(1L);
        when(mockWsRuleRepository.findOne(rule.getId())).thenReturn(rule);

        List<String> errors = service.validateRule(rule);

        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateIfRuleExistsWithSameXpath() throws Exception {
        Rule rule = new Rule();
        rule.setId(1000L);
        rule.setName("TEST_RULE_NAME");
        rule.setDescription("TEST_RULE_DESCRIPTION");
        rule.setResponse("<test>Test Response</test>");
        rule.setService(new Service());
        rule.getService().setId(5000L);

        when(mockWsRuleRepository.count(any(Specification.class))).thenReturn(1L);
        when(mockWsRuleRepository.findOne(rule.getId())).thenReturn(rule);

        List<String> errors = service.validateRule(rule);

        assertEquals(0, errors.size());
    }

    /**
     * Test cases for rule's validation:
     *
     * <ul>
     * <li>1. Mandatory fields: rule name, rule description, response.</li>
     * <li>2. New rule: [mandatory fields] + unique rule name + unique xpath
     * attributes</li>
     * <li>3. Edited rule: [mandatory fields] + unique edited rule name + unique edited xpath.
     * The checking starts only when rule name and xpath attributes were modified. I.e. we have
     * (2) after verifying changes for rule name and xpath attributes</li>
     * </ul>
     *
     * <p><p/>
     * Unique rule name - it means unique for the service.
     * For example there are two service WarrantyService and ProfileService.
     * Both of them can have rule named BUICK_TEST_RULE but WarrantyService can have only
     * one rule BUICK_TEST_RULE.
     * 
     * @throws Exception
     */
    @Test
    public void testValidate() throws Exception {

    }

}
