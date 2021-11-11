package com.ainnotate.aidas.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";

    public static final String CUSTOMER_ADMIN = "ROLE_CUSTOMER_ADMIN";

    public static final String VENDOR_ADMIN = "ROLE_VENDOR_ADMIN";

    public static final String VENDOR_USER = "ROLE_VENDOR_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
