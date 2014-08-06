package com.cpwr.gdo.simulator.service.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * The Class AuditorStringAware.
 *
 */
@Service
public final class AuditorStringAware implements AuditorAware<String> {

    private static final String DEFAULT_AUDITOR = "WS_SIMULATOR";

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.AuditorAware#getCurrentAuditor()
     */
    public String getCurrentAuditor() {
        String auditor = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            auditor = auth.getName();
        } else {
            auditor = DEFAULT_AUDITOR;
        }
        // User user =
        // (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return auditor;
    }
}
