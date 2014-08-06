package com.cpwr.gdo.simulator.dao.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.cpwr.gdo.simulator.model.Service;
import com.cpwr.gdo.simulator.model.Service_;

public class ServiceSpecifications {
    
    /**
     * Creates a specification used to find persons whose last name begins with
     * the given search term. This search is case insensitive.
     *
     * @param requestType the request type
     * @return the specification
     */
    public static Specification<Service> requestTypeIsLike(final String requestType) {

        return new Specification<Service>() {
            public Predicate toPredicate(Root<Service> serviceRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                final String likePattern = "%" + StringUtils.trimToEmpty(requestType).toUpperCase() + "%";
                return cb.like(cb.upper(serviceRoot.<String> get(Service_.requestType)), likePattern);

            }
        };
    }

    /**
     * Request type equals.
     *
     * @param requestType the request type
     * @return the specification
     */
    public static Specification<Service> requestTypeEquals(final String requestType) {

        return new Specification<Service>() {
            public Predicate toPredicate(Root<Service> serviceRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                final String equalPattern = StringUtils.trimToEmpty(requestType).toUpperCase();
                return cb.equal(cb.upper(serviceRoot.<String> get(Service_.requestType)), equalPattern);

            }
        };
    }

    /**
     * Checks if is active.
     *
     * @return the specification
     */
    public static Specification<Service> isActive() {

        return new Specification<Service>() {

            public Predicate toPredicate(Root<Service> serviceRoot, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.equal(serviceRoot.<Boolean> get(Service_.isActive), true);

            }
        };
    }
}
