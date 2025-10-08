package com.scheduler.schedulingservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class CustomAuthorization {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthorization.class);

    public boolean isAdminOrSelf(Authentication authentication, String patientId) {
        if (authentication == null || patientId == null)
            return false;

        boolean isMedicoOrEnfermeiro = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO") || a.getAuthority().equals("ROLE_ENFERMEIRO"));

        Long principal = Long.parseLong(authentication.getPrincipal().toString());

        boolean isSelf = principal.equals(Long.valueOf(patientId));

        return isMedicoOrEnfermeiro || isSelf;
    }
}