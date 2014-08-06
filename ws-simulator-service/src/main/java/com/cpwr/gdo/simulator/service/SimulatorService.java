package com.cpwr.gdo.simulator.service;

import java.util.List;

import com.cpwr.gdo.simulator.model.FailedRequest;
import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.model.Service;

/**
 * The core interface.
 *
 */
public interface SimulatorService {

    /**
     * Gets the response payload.
     * 
     * @param requestType
     *            the request type
     * @param requestXml
     *            the request xml
     * @return the response payload
     */
    String getResponsePayload(final String requestType, final byte[] requestXml);

    /**
     * Creates the service.
     * 
     * @param service
     *            the service
     */
    void createService(final Service service);

    /**
     * Creates the rule.
     * 
     * @param rule
     *            the rule
     */
    void createRule(final Rule rule);

    /**
     * Gets the all services.
     * 
     * @return the all services
     */
    List<Service> getAllServices();

    /**
     * Gets the rules for service.
     * 
     * @param requestType
     *            the request type
     * @return the rules for service
     */
    List<Rule> getRulesForService(final String requestType);

    /**
     * Find services by request type.
     * 
     * @param requestType
     *            the request type
     * @return the list
     */
    List<Service> findServicesByRequestType(final String requestType);

    /**
     * Find rule by rule id.
     * 
     * @param ruleId
     *            the rule id
     * @return the rule
     */
    Rule findRuleByRuleId(final Long ruleId);

    /**
     * Find service by request type.
     * 
     * @param requestType
     *            the request type
     * @return the service
     */
    Service findServiceByRequestType(final String requestType);

    /**
     * Delete rule by id.
     * 
     * @param ruleId
     *            the rule id
     */
    void deleteRuleById(final Long ruleId);

    /**
     * Find service by id.
     * 
     * @param serviceId
     *            the service id
     * @return the service
     */
    Service findServiceById(final Long serviceId);

    /**
     * Delete service.
     * 
     * @param requestType
     *            the request type
     */
    void deleteService(final String requestType);

    /**
     * Validate service.
     * 
     * @param service
     *            the service
     * @return the list
     */
    List<String> validateService(final Service service);

    /**
     * Validate rule.
     * 
     * @param service
     *            the service
     * @return the list
     */
    List<String> validateRule(final Rule service);

    /**
     * Creates the new empty rule.
     * 
     * @return the rule
     */
    Rule createNewEmptyRule();

    List<FailedRequest> listFailedRequest();

    void saveFailedRequest(final FailedRequest failedRequest);

    List<String> findRequestTypes(final String requestType);

    Rule getApplicableRule(final String requestType, final String requestPayload);

    boolean checkXpathExpression(final String expression, final String value, final String requestPayload);
}
