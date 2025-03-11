package kz.medet.userservice.dto;

import lombok.Getter;

@Getter
public enum ERole {
    ROLE_ADMIN("ROLE_ADMIN", "Admin"),
    ROLE_CUSTOMER("ROLE_CUSTOMER", "Customer");

    private final String authority;
    private final String translation;

    ERole(String authority, String translation) {
        this.authority = authority;
        this.translation = translation;
    }
}
