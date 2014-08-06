package com.cpwr.gdo.simulator.dao;

import java.util.List;

import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.model.Service;

public interface SimulatorDao {
    // List<Rule> findRulesByServiceId(final Long serviceId);
    /**
     * Find rules by request type.
     * 
     * @param requestType
     *            the request type
     * @return the list
     */
    List<Rule> findRulesByRequestType(final String requestType);

    /**
     * Gets the all services.
     * 
     * @return the all services
     */
    List<Service> getAllServices();

    /**
     * Check if service exists.
     * 
     * @param requestType
     *            the request type
     * @return true, if successful
     */
    boolean checkIfServiceExists(final String requestType);

    /**
     * Check if rule exists.
     * 
     * @param rule
     *            the rule
     * @return true, if successful
     */
    boolean checkIfRuleExists(final Rule rule);

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
     * Delete service.
     * 
     * @param requestType
     *            the request type
     */
    void deleteService(final String requestType);

    /**
     * Find service by request type.
     * 
     * @param requestType
     *            the request type
     * @return the service
     */
    Service findServiceByRequestType(final String requestType);

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
     * Delete rule.
     * 
     * @param rule
     *            the rule
     */
    void deleteRule(final Rule rule);

    /**
     * Find service by id.
     * 
     * @param serviceId
     *            the service id
     * @return the service
     */
    Service findServiceById(final Long serviceId);

    /**
     * Update service.
     * 
     * @param service
     *            the service
     */
    void updateService(final Service service);

    /**
     * Update rule.
     * 
     * @param service
     *            the service
     */
    void updateRule(final Rule service);

    /**
     * Check if rule name exists.
     * 
     * @param rule
     *            the rule
     * @return true, if successful
     */
    boolean checkIfRuleNameExists(final Rule rule);
}
