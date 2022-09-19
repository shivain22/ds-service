package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "customer_qc_project_mapping",indexes = {
    @Index(name="idx_cqpm_project",columnList = "project_id"),
    @Index(name="idx_cqpm_ucm",columnList = "user_customer_mapping_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_cqpm_ucmid_pid",columnNames={"user_customer_mapping_id","project_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class CustomerQcProjectMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","customer"})
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_cqpm_project"))
    private Project project;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","customer"})
    @JoinColumn(name = "user_customer_mapping_id", nullable = false, foreignKey = @ForeignKey(name="fk_cqpm_ucm"))
    private UserCustomerMapping userCustomerMapping;

    @Column(name="qc_level")
    private Integer qcLevel;

    @Column(name="current_qc_batch_no")
    private Integer currentQcBatchNo=0;

    public Integer getCurrentQcBatchNo() {
        return currentQcBatchNo;
    }

    public void setCurrentQcBatchNo(Integer currentQcBatchNo) {
        this.currentQcBatchNo = currentQcBatchNo;
    }

    public Integer getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Integer qcLevel) {
        this.qcLevel = qcLevel;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UserCustomerMapping getUserCustomerMapping() {
        return userCustomerMapping;
    }

    public void setUserCustomerMapping(UserCustomerMapping userCustomerMapping) {
        this.userCustomerMapping = userCustomerMapping;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerQcProjectMapping)) {
            return false;
        }
        return Objects.equals(id, ((CustomerQcProjectMapping) o).id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CustomerQcProjectMapping{" +"id="+id+",user_id="+this.userCustomerMapping.getUser().getId()+",customer_id="+this.userCustomerMapping.getCustomer().getId()+"}";
    }
}
