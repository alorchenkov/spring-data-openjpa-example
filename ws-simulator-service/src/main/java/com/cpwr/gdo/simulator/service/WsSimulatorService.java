package com.cpwr.gdo.simulator.service;

import static com.cpwr.gdo.simulator.dao.repository.RuleSpecifications.ruleNameIsEqual;
import static com.cpwr.gdo.simulator.dao.repository.RuleSpecifications.serviceIdIsEqual;
import static com.cpwr.gdo.simulator.dao.repository.RuleSpecifications.xpathExpressionIsEqual;
import static com.cpwr.gdo.simulator.dao.repository.RuleSpecifications.xpathValueIsEqual;
import static com.cpwr.gdo.simulator.dao.repository.ServiceSpecifications.isActive;
import static com.cpwr.gdo.simulator.dao.repository.ServiceSpecifications.requestTypeEquals;
import static com.cpwr.gdo.simulator.dao.repository.ServiceSpecifications.requestTypeIsLike;
import static com.cpwr.gdo.simulator.utils.XPathUtils.checkIfRuleIsApplicable;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToEmpty;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cpwr.gdo.simulator.dao.repository.WsFailedRequestRepository;
import com.cpwr.gdo.simulator.dao.repository.WsRuleRepository;
import com.cpwr.gdo.simulator.dao.repository.WsServiceRepository;
import com.cpwr.gdo.simulator.model.FailedRequest;
import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.model.Service;
import com.cpwr.gdo.simulator.model.XpathRuleData;
import com.cpwr.gdo.simulator.utils.XPathUtils;

/**
 * The Class WsSimulatorService.
 * 
 */
@Component
@Qualifier("simulatorService")
public class WsSimulatorService implements SimulatorService {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(WsSimulatorService.class);

    /** The ws service repository. */
    @Autowired
    private WsServiceRepository wsServiceRepository;

    /** The ws rule repository. */
    @Autowired
    private WsRuleRepository wsRuleRepository;

    @Autowired
    private WsFailedRequestRepository failedRequestRepository;

    /** The response payloads. */
    private Map<String, String> responsePayloads = new HashMap<String, String>();

    @PostConstruct
    private void checkInjection() {
        checkNotNull(wsServiceRepository, "wsServiceRepository must not be null!");
        checkNotNull(wsRuleRepository, "wsRuleRepository must not be null!");
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cpwr.gdo.simulator.service.IGmocXpathParserService#getAllServices ()
     */
    public List<Service> getAllServices() {
        LOG.debug("getAllServices... ");

        List<Service> services = wsServiceRepository.findAll(isActive(), new Sort(DESC, "createdDate"));

        LOG.debug("amount of services: {}.", services.size());

        return services;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#createService
     * (com.cpwr.gdo.simulator.model.Service)
     */
    @Transactional
    public void createService(final Service service) {
        LOG.debug("createService, service: {}", service);

        wsServiceRepository.saveAndFlush(service);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#createRule
     * (com.cpwr.gdo.simulator.model.Rule)
     */
    @Transactional
    public void createRule(final Rule rule) {
        LOG.debug("createRule, rule: {}", rule);

        String expValue = rule.getXpathRuleData().getXpathValue();

        if (isNotBlank(expValue)) {
            rule.getXpathRuleData().setExpressionBoolean(false);
        }

        wsRuleRepository.saveAndFlush(rule);

        LOG.debug("createRule result: {}", rule);
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#
     * getResponsePayload(java.lang.String, byte[])
     */
    public String getResponsePayload(final String requestType, final byte[] requestXml) {

        final String requestPayload = new String(requestXml);

        LOG.debug("requestType: {}, \n requestPayload: {}", requestType, requestPayload);

        String responseXml = null;

        final Rule rule = getApplicableRule(requestType, requestPayload);

        if (rule != null) {
            responseXml = rule.getResponse();
        } else {
            /* failed request */
            saveFailedRequest(new FailedRequest(requestType, requestPayload, ""));
        }

        LOG.debug("result: {}", responseXml);

        return responseXml;
    }

    public Rule getApplicableRule(final String requestType, final String requestPayload) {
        Rule result = null;

        // 1. get rules list
        final List<Rule> rules = wsRuleRepository.findRulesByRequestType(requestType);

        // 2. XPath query to get applicable rule
        for (Rule rule : rules) {

            processRule(rule, requestPayload);

            if (rule.isRuleApplicableToRequest()) {
                result = rule;
                break;
            }
        }

        return result;
    }

    /**
     * Gets the default response payload.
     * 
     * @param rules
     *            the rules
     * @return the default response payload
     */
    private String getDefaultResponsePayload(List<Rule> rules) {
        String result = null;

        for (Rule rule : rules) {

            final String xpathExpr = rule.getXpathRuleData().getXpathExpression();
            final String xpathValue = rule.getXpathRuleData().getXpathValue();

            if (isBlank(xpathExpr) && isBlank(xpathValue)) {
                result = rule.getResponse();

                LOG.debug("default rule: {}", rule);

                break;
            }
        }

        LOG.debug("result not null: {}", isNotBlank(result));

        return result;
    }

    /**
     * Process rule.
     * 
     * @param rule
     *            the rule
     * @param requestPayload
     *            the request payload
     */
    private void processRule(final Rule rule, final String requestPayload) {

        LOG.debug("rule: {}", rule);

        boolean isRuleApplicableToRequest;
        String expression = rule.getXpathRuleData().getXpathExpression();
        String value = rule.getXpathRuleData().getXpathValue();

        LOG.debug("expression: {}, value: {}, isBoolean: {}", expression, value, rule.getXpathRuleData()
                .isExpressionBoolean());

        if (rule.getXpathRuleData().isExpressionBoolean()) {
            isRuleApplicableToRequest = checkIfRuleIsApplicable(expression, requestPayload);
        } else {
            isRuleApplicableToRequest = checkIfRuleIsApplicable(expression, value, requestPayload);
        }

        rule.setRuleApplicableToRequest(isRuleApplicableToRequest);

        LOG.debug("isRuleApplicableToRequest: {}", isRuleApplicableToRequest);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#
     * getRulesForService(java.lang.String)
     */
    public List<Rule> getRulesForService(final String requestType) {

        LOG.debug("getRulesForService, requestType: {}", requestType);

        return wsRuleRepository.findRulesByRequestType(requestType);
    }

    // /@Override
    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#
     * findServicesByRequestType(java.lang.String)
     */
    public List<Service> findServicesByRequestType(final String requestType) {
        LOG.debug("findServicesByRequestType, requestType: {}", requestType);

        return wsServiceRepository.findAll(where(requestTypeIsLike(requestType)).and(isActive()));
    }

    // /@Override
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cpwr.gdo.simulator.service.IGmocXpathParserService#findRuleByRuleId
     * (java.lang.Long)
     */
    public Rule findRuleByRuleId(final Long ruleId) {
        LOG.debug("findRuleByRuleId, ruleId: {}", ruleId);

        return wsRuleRepository.findOne(ruleId);
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#
     * findUserByUsername(java.lang.String)
     */
    public Service findServiceByRequestType(final String requestType) {
        LOG.debug("findUserByUsername, filePath: {}", requestType);

        return wsServiceRepository.findServiceByRequestType(requestType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cpwr.gdo.simulator.service.IGmocXpathParserService#deleteRuleById
     * (java.lang.Long)
     */
    @Transactional
    public void deleteRuleById(final Long ruleId) {
        LOG.debug("deleteRuleById, ruleId: {}", ruleId);

        /*
         * Rule rule = new Rule(); rule.setId(ruleId);
         * wsRuleRepository.setActiveStatusForRule(ruleId, false);
         */

        final Rule rule = wsRuleRepository.findOne(ruleId);
        final String deleteTag = "[id=" + ruleId + ":deleted]";

        rule.setName(deleteTag + rule.getName());
        rule.getXpathRuleData().setXpathExpression(deleteTag + rule.getXpathRuleData().getXpathExpression());
        rule.getXpathRuleData().setXpathValue(deleteTag + rule.getXpathRuleData().getXpathValue());
        rule.setActive(false);

        wsRuleRepository.save(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cpwr.gdo.simulator.service.IGmocXpathParserService#findServiceById
     * (java.lang.Long)
     */
    public Service findServiceById(final Long serviceId) {
        LOG.debug("findServiceById, serviceId: {}", serviceId);

        return wsServiceRepository.findOne(serviceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#deleteService
     * (java.lang.String)
     */
    @Transactional
    public void deleteService(final String requestType) {
        LOG.debug("deleteService, requestType: {}", requestType);

        final Service service = wsServiceRepository.findServiceByRequestType(requestType);
        final String deleteTag = "[id=" + service.getId() + ":deleted]";

        wsServiceRepository.setActiveStatusForService(requestType, false, deleteTag + requestType);
        // wsServiceRepository.setActiveStatusForService(requestType, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cpwr.gdo.simulator.service.IGmocXpathParserService#validateService
     * (com.cpwr.gdo.simulator.model.Service)
     */
    public List<String> validateService(final Service service) {
        List<String> errors = new ArrayList<String>();

        validateMandatoryFields(service, errors);

        if (service.getId() == null) {
            validateServiceName(service, errors);
        } else {
            validateEditedService(service, errors);
        }

        LOG.debug("violations: {}", reflectionToString(errors, SHORT_PREFIX_STYLE));

        return errors;
    }

    private void validateEditedService(Service service, List<String> errors) {
        final Service oldService = wsServiceRepository.findOne(service.getId());

        if (!equalsIgnoreCase(oldService.getRequestType(), service.getRequestType())) {
            validateServiceName(service, errors);
        }

        LOG.debug("[edited service] violations: {}", reflectionToString(errors, SHORT_PREFIX_STYLE));
    }

    private void validateServiceName(Service service, List<String> errors) {
        boolean indicator = checkIfServiceExists(service.getRequestType());
        if (indicator) {
            errors.add("This request type is already in database.");
        }
        LOG.debug("[service] violations: {}", reflectionToString(errors, SHORT_PREFIX_STYLE));
    }

    private void validateMandatoryFields(Service service, List<String> errors) {
        if (isBlank(service.getRequestType())) {
            errors.add("RequestType is mandatory field.");
        }
        if (isBlank(service.getDescription())) {
            errors.add("Service Description is mandatory field.");
        }

        LOG.debug("[mandatory fields] violations: {}", reflectionToString(errors, SHORT_PREFIX_STYLE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#validateRule
     * (com.cpwr.gdo.simulator.model.Rule)
     */
    public List<String> validateRule(final Rule rule) {
        List<String> errors = new ArrayList<String>();

        validateMandatoryFields(rule, errors);

        if (rule.getId() == null) {
            validateNewRule(rule, errors);
        } else {
            validateEditedRule(rule, errors);
        }

        LOG.debug("violations: {}", reflectionToString(errors, SHORT_PREFIX_STYLE));

        return errors;
    }

    private void validateEditedRule(Rule rule, List<String> errors) {
        final Rule oldRule = wsRuleRepository.findOne(rule.getId());

        /* Checking if the new name of edited rules is unique */
        if (!equalsIgnoreCase(oldRule.getName(), rule.getName())) {
            validateUniqueRuleName(rule, errors);
        }

        /* 1.1. Checking if the XPATH attributes of edited rules are unique */
        String oldXpath = trimToEmpty(oldRule.getXpathRuleData().getXpathExpression());
        String oldXpathValue = trimToEmpty(oldRule.getXpathRuleData().getXpathValue());
        String newXpath = trimToEmpty(rule.getXpathRuleData().getXpathExpression());
        String newXpathValue = trimToEmpty(rule.getXpathRuleData().getXpathValue());

        /* 1.2. Verifying when XPATH attributes are not equal */
        if (!equalsIgnoreCase(oldXpath + oldXpathValue, newXpath + newXpathValue)) {
            validateXpathAttributes(rule, errors);
        }

        LOG.debug("[edited rule] violations: {}" + reflectionToString(errors, SHORT_PREFIX_STYLE));
    }

    private void validateXpathAttributes(Rule rule, List<String> errors) {
        boolean ind = checkIfRuleExists(rule);
        if (ind) {
            errors.add("The combination of Xpath Expression and Xpath value must be unique in database.");
        }
    }

    private void validateUniqueRuleName(Rule rule, List<String> errors) {
        boolean ind = checkIfRuleNameExists(rule);
        if (ind) {
            errors.add("Rule Name must be unique in database.");
        }
    }

    private void validateNewRule(Rule rule, List<String> errors) {
        validateUniqueRuleName(rule, errors);
        validateXpathAttributes(rule, errors);

        LOG.debug("[new rule] violations: {}", reflectionToString(errors, SHORT_PREFIX_STYLE));
    }

    private void validateMandatoryFields(Rule rule, List<String> errors) {
        if (isBlank(rule.getName())) {
            errors.add("Rule Name is mandatory field.");
        }
        if (isBlank(rule.getDescription())) {
            errors.add("Rule Description is mandatory field.");
        }

        if (isBlank(rule.getResponse())) {
            errors.add("Response is mandatory field.");
        }

        LOG.debug("[mandatory fields] violations: {}", reflectionToString(errors, SHORT_PREFIX_STYLE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cpwr.gdo.simulator.service.IGmocXpathParserService#
     * createNewEmptyRule()
     */
    public Rule createNewEmptyRule() {
        LOG.debug("createNewEmptyRule...");

        Rule rule = new Rule();
        rule.setService(new Service());
        rule.setXpathRuleData(new XpathRuleData());

        return rule;
    }

    /**
     * Check if rule exists.
     * 
     * @param rule
     *            the rule
     * @return true, if successful
     */
    private boolean checkIfRuleExists(final Rule rule) {

        boolean result = 1 == wsRuleRepository.count(where(serviceIdIsEqual(rule.getService().getId())).and(
                xpathExpressionIsEqual(rule.getXpathRuleData().getXpathExpression())).and(
                xpathValueIsEqual(rule.getXpathRuleData().getXpathValue()))

        );

        LOG.debug("ifRuleExists: {}", result);

        return result;
    }

    /**
     * Check if rule name exists.
     * 
     * @param rule
     *            the rule
     * @return true, if successful
     */
    private boolean checkIfRuleNameExists(final Rule rule) {
        boolean result = 1 == wsRuleRepository.count(where(serviceIdIsEqual(rule.getService().getId())).and(
                ruleNameIsEqual(rule.getName())));

        LOG.debug("ifRuleNameExists: {}", result);

        return result;
    }

    /**
     * Check if service exists.
     * 
     * @param requestType
     *            the request type
     * @return true, if successful
     */
    private boolean checkIfServiceExists(final String requestType) {
        LOG.debug("requestType: {}", requestType);

        boolean result = 1 == wsServiceRepository.count(requestTypeEquals(requestType));

        LOG.debug("ifServiceExists: {}", result);

        return result;
    }

    /**
     * Sets the ws service repository.
     * 
     * @param wsServiceRepository
     *            the new ws service repository
     */
    public void setWsServiceRepository(WsServiceRepository wsServiceRepository) {
        this.wsServiceRepository = wsServiceRepository;
    }

    /**
     * Sets the ws rule repository.
     * 
     * @param wsRuleRepository
     *            the new ws rule repository
     */
    public void setWsRuleRepository(WsRuleRepository wsRuleRepository) {
        this.wsRuleRepository = wsRuleRepository;
    }

    @Override
    public List<FailedRequest> listFailedRequest() {

        final List<FailedRequest> failedRequests = failedRequestRepository.findAll();

        LOG.debug("failedRequest: {}", reflectionToString(failedRequests, SHORT_PREFIX_STYLE));

        return failedRequests;
    }

    @Override
    public void saveFailedRequest(final FailedRequest failedRequest) {
        LOG.debug("failedRequest: {}", failedRequest);

        checkNotNull(failedRequest, "failedRequest must not be null!");

        failedRequestRepository.saveAndFlush(failedRequest);

        LOG.debug("failedRequest saved, id:{}" + failedRequest.getId());
    }

    @Override
    public List<String> findRequestTypes(final String requestType) {
        LOG.debug("findRequestTypes, requestType: {}", requestType);

        final List<Service> services = wsServiceRepository.findAll(where(requestTypeIsLike(requestType))
                .and(isActive()));

        final List<String> requestTypes = new ArrayList<String>();

        for (Service service : services) {
            requestTypes.add(service.getRequestType());
        }

        return requestTypes;
    }

    @Override
    public boolean checkXpathExpression(final String expression, final String value, final String requestPayload) {
        boolean result;

        if (isNotBlank(value)) {
            result = XPathUtils.checkIfRuleIsApplicable(expression, value, requestPayload);
        } else {
            result = XPathUtils.checkIfRuleIsApplicable(expression, requestPayload);
        }

        LOG.debug("checkXpathExpression: {}", result);

        return result;
    }
}
