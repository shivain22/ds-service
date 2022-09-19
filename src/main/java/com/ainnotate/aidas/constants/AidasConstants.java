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

    public static final String QC_USER = "ROLE_QC";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final Integer AIDAS_UPLOAD_REJECTED=0;

    public static final Integer AIDAS_UPLOAD_APPROVED=1;

    public static final Integer AIDAS_UPLOAD_PENDING=2;

    public static final Integer AIDAS_UPLOAD_QC_PENDING=2;

    public static final Integer AIDAS_UPLOAD_QC_REJECTED=0;

    public static final Integer AIDAS_UPLOAD_QC_APPROVED=1;

    /*public static final Integer AIDAS_UPLOAD_QC_COMPLETED=1;*/

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

    public static final String DEFAULT_STORAGE_KEY_NAME="defaultStorage";

    public static final String DOWNLOAD_BUCKETNAME_KEY_NAME="downloadBucketName";

    public static final String DOWNLOAD_REGION_KEY_NAME="downloadRegion";

    public static final String DOWNLOAD_ACCESS_KEY_KEY_NAME="downloadAccessKey";

    public static final String DOWNLOAD_ACCESS_SECRET_KEY_NAME="downloadAccessSecret";

    public static final String DOWNLOAD_PREFIX_KEY_NAME="downloadPrefix";

    public static final String UPLOAD_BUCKETNAME_KEY_NAME="uploadBucketName";

    public static final String UPLOAD_REGION_KEY_NAME="uploadRegion";

    public static final String UPLOAD_ACCESS_KEY_KEY_NAME="uploadAccessKey";

    public static final String UPLOAD_ACCESS_SECRET_KEY_NAME="uploadAccessSecret";

    public static final String UPLOAD_PREFIX_KEY_NAME="uploadPrefix";

    public static final String S3="s3";

    public static final Integer AUTO_CREATE_OBJECTS=1;

    public static final Integer CREATE_MANUAL_OBJECTS=0;

    public static final Integer QC_LEVEL_FCFS=0;

    public static final Integer QC_LEVEL_ED=1;

    private AidasConstants() {}
}
