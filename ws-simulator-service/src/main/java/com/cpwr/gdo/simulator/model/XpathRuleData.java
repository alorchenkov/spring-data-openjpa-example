package com.cpwr.gdo.simulator.model;

import static com.google.common.base.Objects.toStringHelper;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;


@Embeddable
public class XpathRuleData implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5526720255278934195L;

    /** The xpath expression. */
    @Column(name = "xpath_expr")
    private String xpathExpression = EMPTY;

    /** The xpath value. */
    @Column(name = "xpath_value")
    private String xpathValue = EMPTY;

    /** The is expression boolean. */
    @Column(name = "xpath_expr_ind")
    private boolean isExpressionBoolean;

    /** The rule id. */
    @Transient
    private Long ruleId;

    /**
     * Gets the xpath expression.
     * 
     * @return the xpath expression
     */
    public String getXpathExpression() {
        return xpathExpression;
    }

    /**
     * Sets the xpath expression.
     * 
     * @param xpathExpression
     *            the new xpath expression
     */
    public void setXpathExpression(String xpathExpression) {
        this.xpathExpression = xpathExpression;
    }

    /**
     * Gets the xpath value.
     * 
     * @return the xpath value
     */
    public String getXpathValue() {
        return xpathValue;
    }

    /**
     * Sets the xpath value.
     * 
     * @param xpathValue
     *            the new xpath value
     */
    public void setXpathValue(String xpathValue) {
        this.xpathValue = xpathValue;
    }

    /**
     * Checks if is expression boolean.
     * 
     * @return true, if is expression boolean
     */
    public boolean isExpressionBoolean() {
        return isExpressionBoolean;
    }

    /**
     * Sets the expression boolean.
     * 
     * @param isExpressionBoolean
     *            the new expression boolean
     */
    public void setExpressionBoolean(boolean isExpressionBoolean) {
        this.isExpressionBoolean = isExpressionBoolean;
    }

    /**
     * Gets the rule id.
     * 
     * @return the rule id
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * Sets the rule id.
     * 
     * @param ruleId
     *            the new rule id
     */
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toStringHelper(this).addValue(xpathExpression).addValue(xpathValue).addValue(isExpressionBoolean)
                .addValue(ruleId).toString();
    }
}
