package com.cpwr.gdo.simulator.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * The metamodel class for XpathRuleData.
 *
 */
@StaticMetamodel(XpathRuleData.class)
public class XpathRuleData_ {

    /** The xpath expression. */
    public static volatile SingularAttribute<XpathRuleData, String> xpathExpression;

    /** The xpath value. */
    public static volatile SingularAttribute<XpathRuleData, String> xpathValue;
}