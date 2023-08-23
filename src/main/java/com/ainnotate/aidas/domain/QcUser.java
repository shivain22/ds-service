package com.ainnotate.aidas.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qc_user")
public class QcUser {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "login")
    private String login;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "user_mapping_id")
    private Long userMappingId;
    @Column(name = "entity_id")
    private Long entityId;
    @Column(name = "uum_id")
    private Long uumId;
    @Column(name = "uom_id")
    private Long uomId;
    @Column(name = "ucmId")
    private Long ucmId;
    @Column(name = "uvmId")
    private Long uvmId;
    @Column(name = "qc_level")
    private Integer qcLevel;
    @Column(name = "status")
    private Integer status;

    

    public Long getUumId() {
		return uumId;
	}

	public void setUumId(Long uumId) {
		this.uumId = uumId;
	}

	public Long getUomId() {
		return uomId;
	}

	public void setUomId(Long uomId) {
		this.uomId = uomId;
	}

	public Long getUcmId() {
		return ucmId;
	}

	public void setUcmId(Long ucmId) {
		this.ucmId = ucmId;
	}

	public Long getUvmId() {
		return uvmId;
	}

	public void setUvmId(Long uvmId) {
		this.uvmId = uvmId;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

   

    public Long getUserMappingId() {
		return userMappingId;
	}

	public void setUserMappingId(Long userMappingId) {
		this.userMappingId = userMappingId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Integer getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Integer qcLevel) {
        this.qcLevel = qcLevel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



}
