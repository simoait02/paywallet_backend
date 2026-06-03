package com.paylogic.paywalletlite.security;

import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationFacade {

    /**
     * Retourne l'ID de l'utilisateur authentifié depuis le JWT.
     */
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("Authentication User : "+ authentication);
            throw new SecurityException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UUID) {
            return (UUID) principal;
        }

        if (principal instanceof String) {
            return UUID.fromString((String) principal);
        }

        throw new SecurityException("Invalid principal type: " + principal.getClass());
    }

    /**
     * Vérifie si l'utilisateur courant est ADMIN.
     */
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public RoleType getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .map(a -> RoleType.valueOf(a.getAuthority().replace("ROLE_", "")))
                .findFirst()
                .orElse(null);
    }
}