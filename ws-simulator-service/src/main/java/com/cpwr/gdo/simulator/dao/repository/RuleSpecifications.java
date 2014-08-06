package com.cpwr.gdo.simulator.dao.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.model.Rule_;
import com.cpwr.gdo.simulator.model.Service;
import com.cpwr.gdo.simulator.model.Service_;
import com.cpwr.gdo.simulator.model.XpathRuleData;
import com.cpwr.gdo.simulator.model.XpathRuleData_;

public class RuleSpecifications {
    
    /**
     * Creates a specification used to find persons whose last name begins with
     * the given search term. This search is case insensitive.
     *
     * @param requestType the request type
     * @return the specification
     */
    public static Specification<Rule> requestTypeIsLike(final String requestType) {

        return new Specification<Rule>() {
            public Predicate toPredicate(Root<Rule> ruleRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                final String parameter = StringUtils.trimToEmpty(requestType).toUpperCase();
                return cb.equal(cb.upper(ruleRoot.<Service> get(Rule_.service).<String> get(Service_.requestType)),
                        parameter);

            }
        };
    }

    /**
     * Checks if is active.
     *
     * @return the specification
     */
    public static Specification<Rule> isActive() {

        return new Specification<Rule>() {

            public Predicate toPredicate(Root<Rule> ruleRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.equal(ruleRoot.<Boolean> get(Rule_.isActive), true);
            }
        };
    }

    /**
     * Checks if is service active.
     *
     * @return the specification
     */
    public static Specification<Rule> isServiceActive() {

        return new Specification<Rule>() {

            public Predicate toPredicate(Root<Rule> ruleRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.equal(ruleRoot.<Service> get(Rule_.service).<Boolean> get(Service_.isActive), true);
            }
        };
    }

    /**
     * Xpath value is equal.
     *
     * @param xpathValue the xpath value
     * @return the specification
     */
    public static Specification<Rule> xpathValueIsEqual(final String xpathValue) {

        return new Specification<Rule>() {
            final String parameter = StringUtils.trimToEmpty(xpathValue).toUpperCase();

            public Predicate toPredicate(Root<Rule> ruleRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.equal(
                        cb.upper(ruleRoot.<XpathRuleData> get(Rule_.xpathRuleData).<String> get(
                                XpathRuleData_.xpathValue)), parameter);
            }
        };
    }

    /**
     * Xpath expression is equal.
     *
     * @param xpathExpression the xpath expression
     * @return the specification
     */
    public static Specification<Rule> xpathExpressionIsEqual(final String xpathExpression) {

        return new Specification<Rule>() {
            final String parameter = StringUtils.trimToEmpty(xpathExpression).toUpperCase();

            public Predicate toPredicate(Root<Rule> ruleRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.equal(
                        cb.upper(ruleRoot.<XpathRuleData> get(Rule_.xpathRuleData).<String> get(
                                XpathRuleData_.xpathExpression)), parameter);
            }
        };
    }

    /**
     * Service id is equal.
     *
     * @param serviceId the service id
     * @return the specification
     */
    public static Specification<Rule> serviceIdIsEqual(final Long serviceId) {

        return new Specification<Rule>() {

            public Predicate toPredicate(Root<Rule> ruleRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.equal(ruleRoot.<Service> get(Rule_.service).<Long> get("id"), serviceId);
            }
        };
    }

    /**
     * Rule name is equal.
     *
     * @param ruleName the rule name
     * @return the specification
     */
    public static Specification<Rule> ruleNameIsEqual(final String ruleName) {

        return new Specification<Rule>() {
            public Predicate toPredicate(Root<Rule> ruleRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                final String parameter = StringUtils.trimToEmpty(ruleName).toUpperCase();
                return cb.equal(cb.upper(ruleRoot.<String> get(Rule_.name)), parameter);

            }
        };
    }
}
