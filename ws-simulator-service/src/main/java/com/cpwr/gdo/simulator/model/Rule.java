package com.cpwr.gdo.simulator.model;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "simulator_rule")
@SequenceGenerator(name = "rule_id_gen", sequenceName = "rule_id_seq", allocationSize = 1)
public final class Rule extends AbstractEntity<Long> implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8433402547995528807L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_id_gen")
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** The name. */
    @NotEmpty
    @Length(max = 900)
    @Column(name = "rule_name")
    private String name;

    /** The description. */
    @NotEmpty
    @Length(max = 900)
    @Column(name = "description")
    private String description;

    /** The xpath rule data. */
    @Valid
    @Embedded
    private XpathRuleData xpathRuleData = new XpathRuleData();

    /** The priority. */
    @Transient
    private RulePriorityE priority;

    /** The delay. */
    @Min(0)
    @Max(5)
    @Transient
    private int delay;

    /** The response. */
    @NotEmpty
    @Column(name = "response")
    @Lob
    private String response;

    /** The service. */
    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    private Service service;

    /**
     * Gets the service.
     * 
     * @return the service
     */
    public Service getService() {
        return service;
    }

    /**
     * Sets the service.
     * 
     * @param service
     *            the new service
     */
    public void setService(Service service) {
        this.service = service;
    }

    /* transient filed */
    /** The is rule applicable to request. */
    @Transient
    private boolean isRuleApplicableToRequest;

    /** The is active. */
    @Column(name = "active_ind")
    private boolean isActive = true;

    /**
     * Gets the rule id.
     * 
     * @return the rule id
     */
    @Min(0)
    public Long getRuleId() {
        return getId();
    }

    /**
     * Sets the rule id.
     * 
     * @param ruleId
     *            the new rule id
     */
    public void setRuleId(Long ruleId) {
        setId(ruleId);
    }

    /**
     * Gets the priority.
     * 
     * @return the priority
     */
    public RulePriorityE getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     * 
     * @param priority
     *            the new priority
     */
    public void setPriority(RulePriorityE priority) {
        this.priority = priority;
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
        this.description = description;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the xpath rule data.
     * 
     * @return the xpath rule data
     */
    public XpathRuleData getXpathRuleData() {
        return xpathRuleData;
    }

    /**
     * Sets the xpath rule data.
     * 
     * @param xpathRuleData
     *            the new xpath rule data
     */
    public void setXpathRuleData(XpathRuleData xpathRuleData) {
        this.xpathRuleData = xpathRuleData;
    }

    /**
     * Checks if is rule applicable to request.
     * 
     * @return true, if is rule applicable to request
     */
    public boolean isRuleApplicableToRequest() {
        return isRuleApplicableToRequest;
    }

    /**
     * Sets the rule applicable to request.
     * 
     * @param isRuleApplicableToRequest
     *            the new rule applicable to request
     */
    public void setRuleApplicableToRequest(boolean isRuleApplicableToRequest) {
        this.isRuleApplicableToRequest = isRuleApplicableToRequest;
    }

    /**
     * Gets the response.
     * 
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets the response.
     * 
     * @param response
     *            the new response
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Gets the delay.
     * 
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the delay.
     * 
     * @param delay
     *            the new delay
     */
    public void setDelay(int delay) {
        this.delay = delay;
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

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.jpa.domain.AbstractPersistable#toString()
     */
    @Override
    public String toString() {
        return toStringHelper(this).addValue(id).addValue(description).addValue(name).addValue(xpathRuleData)
                .addValue(priority).addValue(isRuleApplicableToRequest).addValue(isActive).toString();

    }

    @Transient
    private String formattedResponse;

    public String getFormattedResponse() {
        return StringUtils.replace(StringUtils.replace(response, "<", "&lt;"), ">", "&gt;");
    }

    public void setFormattedResponse(String formattedResponse) {
    }
}
