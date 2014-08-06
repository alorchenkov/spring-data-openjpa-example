package com.cpwr.gdo.simulator.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Rule.class)
public class Rule_ {

    /** The name. */
    public static volatile SingularAttribute<Rule, String> name;

    /** The service. */
    public static volatile SingularAttribute<Rule, Service> service;

    /** The is active. */
    public static volatile SingularAttribute<Rule, Boolean> isActive;

    /** The xpath rule data. */
    public static volatile SingularAttribute<Rule, XpathRuleData> xpathRuleData;
}
