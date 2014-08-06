package com.cpwr.gdo.simulator.dao.repository;

import com.cpwr.gdo.simulator.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface WsRuleRepository extends JpaRepository<Rule, Long>, JpaSpecificationExecutor<Rule> {

    /**
     * <code>     String selectRuleSql = "select rule.*, service.id, service.request_type "
     * + "from rule rule, service service where rule.active_ind='1' " +
     * "and rule.service_id = service.id and service.active_ind='1' " +
     * "and service.request_type = :requestType";</code>
     *
     * @param requestType the request type
     * @return the list
     */

    @Query("select r FROM Rule r where r.service.requestType=?1 and r.service.isActive=true and r.isActive=true")
    List<Rule> findRulesByRequestType(final String requestType);

    /**
     * Sets the active status for rule.
     *
     * @param ruleId   the rule id
     * @param isActive the is active
     * @return the integer
     */
    @Deprecated
    @Modifying
    @Transactional
    @Query("update Rule r set r.isActive=?2 where r.id=?1")
    Integer setActiveStatusForRule(Long ruleId, boolean isActive);

}
