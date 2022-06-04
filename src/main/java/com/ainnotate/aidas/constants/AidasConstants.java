package com.ainnotate.aidas.constants;

/**
 * Constants for Spring Security authorities.
 */
public final class AidasConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";

    public static final String CUSTOMER_ADMIN = "ROLE_CUSTOMER_ADMIN";

    public static final String VENDOR_ADMIN = "ROLE_VENDOR_ADMIN";

    public static final String VENDOR_USER = "ROLE_VENDOR_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final Integer AIDAS_UPLOAD_REJECTED=0;

    public static final Integer AIDAS_UPLOAD_APPROVED=1;

    public static final Integer AIDAS_UPLOAD_PENDING=2;

    public static final Integer AIDAS_UPLOAD_QC_PENDING=0;

    public static final Integer AIDAS_UPLOAD_QC_COMPLETED=1;

    public static final Integer AIDAS_UPLOAD_METADATA_COMPLETED=1;

    public static final Integer AIDAS_UPLOAD_METADATA_REQUIRED=0;

    public static final Integer AIDAS_PROPERTY_OPTIONAL=0;

    public static final Integer AIDAS_PROPERTY_REQUIRED=1;

    public static final Integer AIDAS_PROPERTY=0;

    public static final Integer AIDAS_SYSTEM_PROPERTY=1;

    public static final Integer AIDAS_METADATA_PROPERTY=2;

    public static final Integer DEFAULT_STATUS=1;

    public static final Integer STATUS_ENABLED=1;

    public static final Integer STATUS_DISABLED=1;



    private AidasConstants() {}
}
