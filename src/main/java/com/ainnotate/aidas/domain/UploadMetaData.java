package com.ainnotate.aidas.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.ainnotate.aidas.dto.UploadMetadataDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A AidasUploadMetaData.
 */
@Entity
@Table(name = "upload_meta_data",indexes = {
    @Index(name="idx_umd_opid",columnList = "object_property_id"),
    @Index(name="idx_umd_ppid",columnList = "project_property_id"),
    @Index(name="idx_umd_upid",columnList = "upload_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_umd_opid_ppid_upid",columnNames={"object_property_id", "project_property_id","upload_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uploadmetadata")


@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetaDataForProject",
query=" select id as uploadMetaDataId," +
"        projectName," +
"        objectName," +
"        upload_id as uploadId," +
"        value," +
"        project_property_id as projectPropertyId," +
"        object_property_id as objectPropertyId" +
"        from (select umd.*,p.id as prop_id,o.name as objectName, pr.name as projectName " +
"from " +
"upload_meta_data umd, " +
"upload u, " +
"user_vendor_mapping_object_mapping uvmom, " +
"object o," +
"project_property pp," +
"property p," +
"project pr " +
" where " +
"umd.upload_id=u.id and " +
"u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
"uvmom.object_id=o.id and " +
"umd.project_property_id=pp.id and" +
" o.project_id=?1  and o.project_id=pr.id and" +
" pp.property_id=p.id and pp.add_to_metadata=1 " +
" union " +
"select umd.*,p.id as prop_id,o.name as objectName, pr.name as projectName " +
"from " +
"upload_meta_data umd, " +
"upload u, " +
"user_vendor_mapping_object_mapping uvmom, " +
"object o," +
"object_property op," +
"property p," +
"project pr " +
" where " +
"umd.upload_id=u.id and " +
"u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
"uvmom.object_id=o.id and " +
" op.property_id=p.id and " +
"umd.object_property_id=op.id and op.add_to_metadata=1 and" +
" op.property_id not in (select property_id from project_property where project_id=?1) and" +
" o.project_id=?1 and o.project_id=pr.id) umd order by umd.upload_id, umd.prop_id",resultSetMapping = "Mapping.UploadMetaDataDTO")


@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetaDataForUpload",
query=" select id as uploadMetaDataId," +
    "        projectName," +
    "        objectName," +
    "        upload_id as uploadId," +
    "        value," +
    "        project_property_id as projectPropertyId," +
    "        object_property_id as objectPropertyId" +
    "        from (select umd.*,p.id as prop_id,o.name as objectName, pr.name as projectName " +
    "from " +
    "upload_meta_data umd, " +
    "upload u, " +
    "user_vendor_mapping_object_mapping uvmom, " +
    "object o," +
    "project_property pp," +
    "property p," +
    "project pr " +
    " where " +
    "umd.upload_id=u.id and " +
    "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
    "uvmom.object_id=o.id and " +
    "umd.project_property_id=pp.id and" +
    " pp.show_to_vendor_user=1 and" +
    " u.id=?1  and o.project_id=pr.id and" +
    " pp.property_id=p.id" +
    " union " +
    "select umd.*,p.id as prop_id,o.name as objectName, pr.name as projectName " +
    "from " +
    "upload_meta_data umd, " +
    "upload u, " +
    "user_vendor_mapping_object_mapping uvmom, " +
    "object o," +
    "object_property op," +
    "property p," +
    "project pr " +
    " where " +
    "umd.upload_id=u.id and " +
    "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
    "uvmom.object_id=o.id and " +
    " op.show_to_vendor_user=1 and " +
    " op.property_id=p.id and " +
    "umd.object_property_id=op.id and" +
    " u.id=?1 and o.project_id=pr.id) umd order by umd.upload_id, umd.prop_id",resultSetMapping = "Mapping.UploadMetaDataDTO")



@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetaDataForProjectPropertiesForUpload",
    query=" select umd.id as uploadMetaDataId, \n" +
    "p.name as projectName, \n" +
    "o.name as objectName, \n" +
    "u.id as uploadId, \n" +
    "umd.value as value,\n" +
    "project_property_id as projectPropertyId,\n" +
    "-1 as objectPropertyId,\n"+
    "pr.name as projectPropertyName, \n"+
    "'' as objectPropertyName,\n"+ 
    "pr.property_type as propertyType \n" +
    "from upload_meta_data umd, \n"+ 
    "upload u, \n"+ 
    "project_property pp, \n"+ 
    "property pr, \n"+ 
    "project p, \n"+ 
    "object o,\n"+ 
    "user_vendor_mapping_object_mapping uvmom  \n"+ 
    "where  \n"+ 
    "umd.upload_id=u.id \n"+ 
    "and u.user_vendor_mapping_object_mapping_id=uvmom.id \n"+ 
    "and uvmom.object_id=o.id \n"+ 
    "and umd.project_property_id=pp.id \n"+ 
    "and pp.property_id=pr.id \n"+ 
    "and pp.project_id=p.id \n"+ 
    "and o.project_id=p.id \n"+
    "and pp.add_to_metadata=1 \n"+ 
    "and u.id=?",
    resultSetMapping = "Mapping.UploadMetaDataDTONew")

@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetaDataProjectAndObjectPropertiesForUpload",
query=" select id as uploadMetaDataId," +
	    "        projectName," +
	    "        objectName," +
	    "        upload_id as uploadId," +
	    "        value," +
	    "        project_property_id as projectPropertyId," +
	    "        object_property_id as objectPropertyId,"
	    + "'' as projectPropertyName, p.name as objectPropertyName" +
	    "        from upload_meta_data umd, upload u, project_property pp, property p "
	    + " where umd.upload_id=u.id and umd.project_property_id=pp.id and pp.property_id=p.id and u.id=?",resultSetMapping = "Mapping.UploadMetaDataDTONew")

@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetaDataForObjectPropertiesForUpload",
query=" select id as uploadMetaDataId," +
	    "        projectName," +
	    "        objectName," +
	    "        upload_id as uploadId," +
	    "        value," +
	    "        project_property_id as projectPropertyId," +
	    "        object_property_id as objectPropertyId,"
	    + " p.name as projectPropertyName" +
	    "        from upload_meta_data umd, upload u, object_property op, property p "
	    + " where umd.upload_id=u.id and umd.project_property_id=op.id and op.property_id=p.id and u.id=?",resultSetMapping = "Mapping.UploadMetaDataDTONew")


@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetaDataForProjectWithStatus",
    query=" select id as uploadMetaDataId," +
        "        projectName," +
        "        objectName," +
        "        upload_id as uploadId," +
        "        value," +
        "        project_property_id as projectPropertyId," +
        "        object_property_id as objectPropertyId" +
        "        from (select umd.*,p.id as prop_id,o.name as objectName, pr.name as projectName " +
        "from " +
        "upload_meta_data umd, " +
        "upload u, " +
        "user_vendor_mapping_object_mapping uvmom, " +
        "object o," +
        "project_property pp," +
        "property p," +
        "project pr " +
        " where " +
        "umd.upload_id=u.id and " +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
        "uvmom.object_id=o.id and " +
        "umd.project_property_id=pp.id and" +
        " pp.add_to_metadata=1 and" +
        " o.project_id=?1  and o.project_id=pr.id and" +
        " pp.property_id=p.id and u.approval_status=?2" +
        " union " +
        "select umd.*,p.id as prop_id,o.name as objectName, pr.name as projectName " +
        "from " +
        "upload_meta_data umd, " +
        "upload u, " +
        "user_vendor_mapping_object_mapping uvmom, " +
        "object o," +
        "object_property op," +
        "property p," +
        "project pr " +
        " where " +
        "umd.upload_id=u.id and " +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
        "uvmom.object_id=o.id and " +
        " op.add_to_metadata=1 and " +
        " op.property_id=p.id and " +
        "umd.object_property_id=op.id and" +
        " op.property_id not in (select property_id from project_property where project_id=?1) and" +
        " o.project_id=?1 and o.project_id=pr.id and u.approval_status=?2) umd order by umd.upload_id, umd.prop_id",resultSetMapping = "Mapping.UploadMetaDataDTO")

@SqlResultSetMappings(value = {
@SqlResultSetMapping(name = "Mapping.UploadMetaDataDTO",
    classes = @ConstructorResult(targetClass = UploadMetadataDTO.class,
        columns = {
            @ColumnResult(name = "uploadMetaDataId",type = Long.class),
            @ColumnResult(name = "projectName",type = String.class),
            @ColumnResult(name = "objectName",type = String.class),
            @ColumnResult(name = "uploadId",type = Long.class),
            @ColumnResult(name = "value",type = String.class),
            @ColumnResult(name = "projectPropertyId",type = Long.class),
            @ColumnResult(name = "objectPropertyId",type = Long.class)
        })),

@SqlResultSetMapping(name = "Mapping.UploadMetaDataDTONew",
classes = @ConstructorResult(targetClass = UploadMetadataDTO.class,
    columns = {
        @ColumnResult(name = "uploadMetaDataId",type = Long.class),
        @ColumnResult(name = "projectName",type = String.class),
        @ColumnResult(name = "objectName",type = String.class),
        @ColumnResult(name = "uploadId",type = Long.class),
        @ColumnResult(name = "value",type = String.class),
        @ColumnResult(name = "projectPropertyId",type = Long.class),
        @ColumnResult(name = "objectPropertyId",type = Long.class),
        @ColumnResult(name = "projectPropertyName",type = String.class),
        @ColumnResult(name = "objectPropertyName",type = String.class),
        @ColumnResult(name = "propertyType",type = Integer.class)
    })),
@SqlResultSetMapping(name = "Mapping.UploadMetaDataDTO1",
classes = @ConstructorResult(targetClass = UploadMetadataDTO.class,
columns = {
    @ColumnResult(name = "projectName",type = String.class),
    @ColumnResult(name = "objectName",type = String.class),
    @ColumnResult(name = "uploadId",type = Long.class),
    @ColumnResult(name = "objectKey",type = String.class),
    @ColumnResult(name = "propertyId",type = Long.class),
    @ColumnResult(name = "propertyName",type = String.class),
    @ColumnResult(name = "uploadMetaDataId",type = Long.class),
    @ColumnResult(name = "value",type = String.class),
    @ColumnResult(name = "optional",type = Integer.class),
    @ColumnResult(name = "isProjectProperty",type = Integer.class)
}))

})




@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetadataProjectProperties",
query="select\n"
		+ "p.name as projectName,\n"
		+ "o.name as objectName,\n"
		+ "u.id as uploadId,\n"
		+ "u.upload_url as objectKey, \n"
		+ "umd.project_property_id as propertyId,\n"
		+ "pr.name as propertyName,\n"
		+ "umd.id as uploadMetaDataId,\n"
		+ "umd.value as value,\n"
		+ "pp.optional as optional,\n"
		+ "1 as isProjectProperty\n"
		+ "from upload_meta_data umd, upload u,project_property pp,property pr,user_vendor_mapping_object_mapping uvmom, object o,user_vendor_mapping uvm,project p\n"
		+ "where \n"
		+ "umd.upload_id=u.id\n"
		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id\n"
		+ "and uvmom.object_id=o.id\n"
		+ "and o.project_id=p.id\n"
		+ "and uvmom.user_vendor_mapping_id=uvm.id\n"
		+ "and umd.project_property_id=pp.id\n"
		+ "and pp.property_id=pr.id\n"
		+ "and project_property_id is not null\n"
		+ "and uvmom.object_id=?2\n"
		+ "and uvm.id=?1\n"
		+ "and pp.show_to_vendor_user=1 and u.metadata_status=0 order by pp.id",resultSetMapping = "Mapping.UploadMetaDataDTO1")


@NamedNativeQuery(name = "UploadMetaData.getAllUploadMetadataObjectProperties",
query="select\n"
		+ "p.name as projectName,\n"
		+ "o.name as objectName,\n"
		+ "u.id as uploadId,\n"
		+ "u.upload_url as objectKey, \n"
		+ "umd.object_property_id as propertyId,\n"
		+ "pr.name as propertyName,\n"
		+ "umd.id as uploadMetaDataId,\n"
		+ "umd.value as value,\n"
		+ "op.optional as optional,\n"
		+ "2 as isProjectProperty\n"
		+ "from upload_meta_data umd, upload u,object_property op,property pr,user_vendor_mapping_object_mapping uvmom, object o,user_vendor_mapping uvm,project p\n"
		+ "where \n"
		+ "umd.upload_id=u.id\n"
		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id\n"
		+ "and uvmom.object_id=o.id\n"
		+ "and o.project_id=p.id\n"
		+ "and uvmom.user_vendor_mapping_id=uvm.id\n"
		+ "and umd.object_property_id=op.id\n"
		+ "and op.property_id=pr.id\n"
		+ "and umd.object_property_id is not null\n"
		+ "and uvmom.object_id=?2\n"
		+ "and uvm.id=?1\n"
		+ "and op.show_to_vendor_user=1  and u.metadata_status=0 order by op.id",resultSetMapping = "Mapping.UploadMetaDataDTO1")









@Audited
public class UploadMetaData extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    @JoinColumn(name = "upload_id", nullable = true, foreignKey = @ForeignKey(name="fk_umd_upload"))
    private Upload upload;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    @JoinColumn(name = "project_property_id", nullable = true, foreignKey = @ForeignKey(name="fk_umd_project_property"))
    private ProjectProperty projectProperty;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    @JoinColumn(name = "object_property_id", nullable = true, foreignKey = @ForeignKey(name="fk_umd_object_property_id"))
    private ObjectProperty objectProperty;

    public ObjectProperty getObjectProperty() {
        return objectProperty;
    }

    public void setObjectProperty(ObjectProperty objectProperty) {
        this.objectProperty = objectProperty;
    }

    public ProjectProperty getProjectProperty() {
        return projectProperty;
    }

    public void setProjectProperty(ProjectProperty projectProperty) {
        this.projectProperty = projectProperty;
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UploadMetaData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public UploadMetaData name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public UploadMetaData value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public Upload getUpload() {
        return this.upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public UploadMetaData upload(Upload upload) {
        this.setUpload(upload);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadMetaData)) {
            return false;
        }
        return id != null && id.equals(((UploadMetaData) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        if(this.projectProperty!=null && this.projectProperty.getProperty()!=null){
            return "UploadMetaData{id="+id+",upload_id="+this.upload.getId()+",project_property_id="+this.projectProperty.getId()+",property_id="+this.projectProperty.getProperty().getId()+"}";
        }
        if(this.objectProperty!=null && this.objectProperty.getProperty()!=null){
            return "UploadMetaData{id="+id+",upload_id="+this.upload.getId()+",object_property_id"+this.objectProperty.getId()+",property_id="+this.objectProperty.getProperty().getId()+"}";
        }else{
            return "UploadMetaData{id="+id+",upload_id="+this.upload.getId()+"}";
        }
    }
}
