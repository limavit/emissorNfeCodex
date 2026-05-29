package br.com.nfesefassp.security;

import br.com.nfesefassp.repository.*;

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {
    }

    public static UUID id() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (UUID) principal;
    }
}
