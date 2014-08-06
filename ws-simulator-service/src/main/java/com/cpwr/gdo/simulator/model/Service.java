package com.cpwr.gdo.simulator.model;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;


@Entity
@Table(name = "service")
@SequenceGenerator(name = "service_id_gen", sequenceName = "service_id_seq", allocationSize = 1)
public class Service extends AbstractEntity<Long> implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7474735184390840704L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_id_gen")
    private Long id;

    /** The request type. */
    @NotNull
    @Length(max = 150)
    @Column(name = "request_type")
    private String requestType;

    /** The description. */
    @Column(name = "description")
    private String description;

    /** The is active. */
    @Column(name = "active_ind")
    private boolean isActive = true;

    /** The rules. */
    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<Rule> rules = new ArrayList<Rule>();

    @Transient
    private Long rulesAmount;

    /**
     * Gets the service id.
     * 
     * @return the service id
     */
    @Min(0)
    public Long getServiceId() {
        return getId();
    }

    /**
     * Sets the service id.
     * 
     * @param serviceId
     *            the new service id
     */
    public void setServiceId(Long serviceId) {
        setId(serviceId);
    }

    /**
     * Gets the request type.
     * 
     * @return the request type
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Sets the request type.
     * 
     * @param requestType
     *            the new request type
     */
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this.description = StringUtils.trimToEmpty(description);
    }

    /**
     * Gets the rules.
     * 
     * @return the rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Sets the rules.
     * 
     * @param rules
     *            the new rules
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Checks if is active.
     * 
     * @return true, if is active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the active.
     * 
     * @param isActive
     *            the new active
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getRulesAmount() {
        return rulesAmount;
    }

    public void setRulesAmount(Long rulesAmount) {
        this.rulesAmount = rulesAmount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.jpa.domain.AbstractPersistable#toString()
     */
    @Override
    public String toString() {
        return toStringHelper(this).addValue(id).addValue(requestType).addValue(description).toString();
    }

}
