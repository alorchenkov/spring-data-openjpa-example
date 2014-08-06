package com.cpwr.gdo.simulator.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(Service.class)
public class Service_ {

    /** The id. */
    public static volatile SingularAttribute<Service, Long> id;

    /** The request type. */
    public static volatile SingularAttribute<Service, String> requestType;

    /** The is active. */
    public static volatile SingularAttribute<Service, Boolean> isActive;
}
