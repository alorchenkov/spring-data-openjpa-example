package com.cpwr.gdo.simulator.web.security.secure;


import com.cpwr.gdo.simulator.web.page.BorderPage;

/**
 * Provides a Spring Security (ACEGI) path protected secure page class.
 */
public class SecurePage extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Override
    public String getHelpPageLink() {
        return null;
    }
}
