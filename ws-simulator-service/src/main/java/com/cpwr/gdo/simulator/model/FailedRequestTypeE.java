package com.cpwr.gdo.simulator.model;

public enum FailedRequestTypeE {
    NOT_IMPLEMENTED("This method is not implemented in GDO Simulator."), RESPONSE_NOT_FOUND(
            "Response was not found for the request.");

    /** The package id. */
    private String description;

    /**
     * 
     * @param description
     */
    private FailedRequestTypeE(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
