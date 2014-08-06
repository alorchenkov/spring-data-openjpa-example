package com.cpwr.gdo.simulator.web.security;

import com.cpwr.gdo.simulator.web.page.BorderPage;
import com.cpwr.gdo.simulator.web.security.secure.SecurePage;

/**
 * Provides a Spring Security (ACEGI) logout page.
 */
public class Logout extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean onSecurityCheck() {
        getContext().getSession().invalidate();
        setRedirect(SecurePage.class);
        return false;
    }

    @Override
    public String getHelpPageLink() {
        return "";
    }
}
