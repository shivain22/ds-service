package com.ainnotate.aidas.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "sample_data_configuration")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sampleDataConfiguration")
public class SampleDataConfiguration extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name="Sample Data";

    @Column(name="description")
    private String description="Sample Data";

    @Column(name="number_of_orgs")
    private Integer numberOfOrgs=1;

    @Column(name="number_of_customers")
    private Integer numberOfCustomers=1;

    @Column(name="number_of_vendors")
    private Integer numberOfVendors=1;

    @Column(name="number_of_projects")
    private Integer numberOfProjects=1;

    @Column(name="auto_create_objects")
    private Integer autoCreateObjects=1;

    @Column(name="number_of_objects")
    private Integer numberOfObjects=1;

    @Column(name="number_of_uploads_per_object")
    private Integer numberOfUploadsPerObject=3;

    @Column(name="number_of_org_admins")
    private Integer numberOfOrgAdmins=1;

    @Column(name="number_of_customer_admins")
    private Integer numberOfCustomerAdmins=1;

    @Column(name="number_of_vendor_admins")
    private Integer numberOfVendorAdmins=1;

    @Column(name="number_of_vendor_users")
    private Integer numberOfVendorUsers=1;

    @Column(name="number_of_default_vendor_users")
    private Integer numberOfDefaultVendorUsers=1;

    @Column(name="number_of_org_qcs")
    private Integer numberOfOrgQcs=3;

    @Column(name="number_of_default_qcs")
    private Integer numberOfDefaultQcs=3;

    @Column(name="number_of_customer_qcs")
    private Integer numberOfCustomerQcs=3;

    @Column(name="org_prefix")
    private String orgPrefix="Test Sample";

    @Column(name="org_suffix")
    private String orgSuffix="Org";

    @Column(name="customer_prefix")
    private String customerPrefix="Test Sample";

    @Column(name="customer_suffix")
    private String customerSuffix="Customer";

    @Column(name="vendor_prefix")
    private String vendorPrefix="Test Sample";

    @Column(name="vendor_suffix")
    private String vendorSuffix="Vendor";

    @Column(name="project_prefix")
    private String projectPrefix="Test Sample";

    @Column(name="project_suffix")
    private String projectSuffix="Project";

    @Column(name="object_prefix")
    private String objectPrefix="Test Sample";

    @Column(name="object_suffix")
    private String objectSuffix="Object";

    @Column(name="org_admin_prefix")
    private String orgAdminPrefix="oa";

    @Column(name="org_admin_sufix")
    private String orgAdminSuffix="user";

    @Column(name="customer_admin_prefix")
    private String customerAdminPrefix="ca";

    @Column(name="customer_admin_suffix")
    private String customerAdminSuffix="user";

    @Column(name="vendor_admin_prefix")
    private String vendorAdminPrefix="va";

    @Column(name="vendor_admin_suffix")
    private String vendorAdminSuffix="user";

    @Column(name="vendor_user_prefix")
    private String vendorUserPrefix="v";

    @Column(name="vendor_user_suffix")
    private String vendorUserSuffix="user";

    @Column(name="org_qc_user_prefix")
    private String orgQcUserPrefix="oqc";

    @Column(name="customer_qc_user_suffix")
    private String orgQcUserSuffix="user";

    @Column(name="customer_qc_user_prefix")
    private String customerQcUserPrefix="cqc";

    @Column(name="vendor_qc_user_suffix")
    private String customerQcUserSuffix="user";

    @Column(name="vendor_qc_user_prefix")
    private String vendorQcUserPrefix="vqc";

    @Column(name="org_qc_user_suffix")
    private String vendorQcUserSuffix="user";

}
