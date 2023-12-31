package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.ObjectDTO;
import com.ainnotate.aidas.dto.ProjectDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.envers.Audited;

/**
 * A AidasObject.
 */


@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProject",
    query = "select\n" +
        "    o.id,\n" +
        "    uvmom.uvmom_id as userVendorMappingObjectMappingId,\n" +
        "    o.project_id as projectId,\n" +
        "    o.parent_object_id as parentObjectId,\n" +
        "    o.number_of_uploads_required as numberOfUploadsRequired,\n" +
        "    o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired,\n" +
        "    ((uvmom.total_required-(select sum(cuvmomv.total_uploaded) from consolidated_user_vendor_mapping_object_mapping_view cuvmomv where object_id=609 group by object_id))\n" +
        "    +  (select sum(cuvmomv.rejected) from consolidated_user_vendor_mapping_object_mapping_view cuvmomv where object_id=609 group by object_id)) as totalRequired,\n" +
        "    uvmom.total_uploaded as totalUploaded,\n" +
        "    uvmom.approved as totalApproved,\n" +
        "    uvmom.rejected as totalRejected,\n" +
        "    uvmom.pending as totalPending,\n" +
        "    o.buffer_percent as bufferPercent,\n" +
        "    o.name as name,\n" +
        "    o.description as description,\n" +
        "    o.image_type as imageType,\n" +
        "    o.audio_type as audioType,\n" +
        "    o.video_type as videoType ,\n" +
        "    o.name\n" +
        "    from consolidated_user_vendor_mapping_object_mapping_view uvmom,object o where uvmom.object_id=o.id and  o.project_id=?2"
    ,resultSetMapping = "Mapping.ObjectDTO")





@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProject.count",
    query ="select count(o.id) as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)

@SqlResultSetMappings(value={
    @SqlResultSetMapping(
        name = "Mapping.ObjectDTOCount",
        columns = { @ColumnResult(name = "count", type = Integer.class) }
    ),
    @SqlResultSetMapping(
        name = "Mapping.ObjectDTO",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
                @ColumnResult(name = "projectId",type = Long.class),
                @ColumnResult(name = "parentObjectId",type = Long.class),
                @ColumnResult(name = "totalRequired",type = Integer.class),
                @ColumnResult(name = "numberOfUploadsRequired",type = Integer.class),
                @ColumnResult(name = "numberOfBufferedUploadsRequired",type = Integer.class),
                @ColumnResult(name = "totalUploaded",type = Integer.class),
                @ColumnResult(name = "totalApproved",type = Integer.class),
                @ColumnResult(name = "totalRejected",type = Integer.class),
                @ColumnResult(name = "totalPending",type = Integer.class),
                @ColumnResult(name = "bufferPercent",type = Integer.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "description",type = String.class),
                @ColumnResult(name = "imageType",type = String.class),
                @ColumnResult(name = "audioType",type = String.class),
                @ColumnResult(name = "videoType",type = String.class)
            })),
    @SqlResultSetMapping(name = "Mapping.ObjectDTOWithProjectId",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
                @ColumnResult(name = "projectId",type = Long.class),
                @ColumnResult(name = "parentObjectId",type = Long.class),
                @ColumnResult(name = "numberOfUploadsRequired",type = Integer.class),
                @ColumnResult(name = "numberOfBufferedUploadsRequired",type = Integer.class),
                @ColumnResult(name = "totalRequired",type = Integer.class),
                @ColumnResult(name = "totalUploaded",type = Integer.class),
                @ColumnResult(name = "totalApproved",type = Integer.class),
                @ColumnResult(name = "totalRejected",type = Integer.class),
                @ColumnResult(name = "totalPending",type = Integer.class),
                @ColumnResult(name = "bufferPercent",type = Integer.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "description",type = String.class),
                @ColumnResult(name = "imageType",type = String.class),
                @ColumnResult(name = "audioType",type = String.class),
                @ColumnResult(name = "videoType",type = String.class),
                @ColumnResult(name = "objectDescriptionLink",type = String.class)
            })),
    @SqlResultSetMapping(name = "Mapping.ObjectDTOForDropdown",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
                @ColumnResult(name = "projectId",type = Long.class),
                @ColumnResult(name = "parentObjectId",type = Long.class),
                @ColumnResult(name = "totalRequired",type = Integer.class),
                @ColumnResult(name = "numberOfUploadsRequired",type = Integer.class),
                @ColumnResult(name = "numberOfBufferedUploadsRequired",type = Integer.class),
                @ColumnResult(name = "totalUploaded",type = Integer.class),
                @ColumnResult(name = "totalApproved",type = Integer.class),
                @ColumnResult(name = "totalRejected",type = Integer.class),
                @ColumnResult(name = "totalPending",type = Integer.class),
                @ColumnResult(name = "bufferPercent",type = Integer.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "description",type = String.class),
                @ColumnResult(name = "imageType",type = String.class),
                @ColumnResult(name = "audioType",type = String.class),
                @ColumnResult(name = "videoType",type = String.class)
            })),
    @SqlResultSetMapping(name = "Mapping.getAllObjectDTOsOfProject",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class)
            }))
})







@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForNonGrouped",
query = "select \n" +
    "o.id," +
    "-1 as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 as totalApproved, \n" +
    "0 as totalRejected, \n" +
    "0 as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from object o \n" +
    "where o.status=1 and o.is_dummy=0  and o.project_id=?1"
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForNonGrouped.count",
query = "select count(o.id) as count  \n" +
        "from object o  \n" +
        "where o.status=1 and o.is_dummy=0 and o.project_id=?1",resultSetMapping = "Mapping.ObjectDTOCount"
)






@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectId",
    query = "select \n" +
        "o.id," +
        "-1 as userVendorMappingObjectMappingId,"+
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_uploads_required as numberOfUploadsRequired," +
        "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
        "o.total_required as totalRequired," +
        "0 as totalUploaded, \n" +
        "0 as totalApproved, \n" +
        "0 as totalRejected, \n" +
        "0 as totalPending,\n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from object o \n" +
        "where o.status=1 and o.is_dummy=0  and o.project_id=?1 and o.object_acquired_by_uvmom_id is null"
        ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectId.count",
    query = "select count(o.id) as count  \n" +
            "from object o  \n" +
            "where o.status=1 and o.is_dummy=0 and o.project_id=?1",resultSetMapping = "Mapping.ObjectDTOCount"
)



@NamedNativeQuery(name="Object.getExistingForNonGrouped",
query = 
	"select " +
			"a.id," +
		    "a.projectId,"+
		    "a.userVendorMappingObjectMappingId,"+
		    "a.parentObjectId,"+
		    "a.numberOfUploadsRequired," +
		    "a.numberOfBufferedUploadsRequired," +
		    "a.totalRequired," +
		    "a.totalUploaded, \n" +
		    "a.totalApproved, \n" +
		    "a.totalRejected, \n" +
		    "a.totalPending,\n" +
		    "a.bufferPercent,\n" +
		    "a.name,\n" +
		    "a.description,\n" +
		    "a.imageType,\n" +
		    "a.audioType,\n" +
		    "a.videoType, \n" +
		    "a.objectDescriptionLink \n"
		    + " from " +
		    
    " ((select \n" +
    "o.id," +
    "o.project_id as projectId,"+
    "uvmom.id as userVendorMappingObjectMappingId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "uvmom.total_uploaded as totalUploaded, \n" +
    "uvmom.total_approved as totalApproved, \n" +
    "uvmom.total_rejected as totalRejected, \n" +
    "uvmom.total_pending as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from user_vendor_mapping_object_mapping uvmom  \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "where uvmom.user_vendor_mapping_id=?1 "
    + "and uvmom.status=1 \n"
    + "and o.status=1 and o.is_dummy=0  \n"
    + "and o.project_id=?2 \n"
    + "order by o.id desc )\n" +
    " union \n" +
    " (select \n" +
    "o.id," +
    "-1 as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 as totalApproved, \n" +
    "0 as totalRejected, \n" +
    "0 as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from object o \n" +
    "where o.status=1 \n"
    + "and o.is_dummy=0  \n"
    + "and o.project_id=?2 \n"
    + "and o.id not in \n"
    + "(select \n"
    + "o.id \n"
    + "from \n"
    + "object o, \n"
    + "user_vendor_mapping_object_mapping uvmom \n"
    + "where uvmom.object_id=o.id \n"
    + "and uvmom.user_vendor_mapping_id=?1))) a order by a.id  \n"
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getExistingForNonGrouped.count",
query = "select sum(a.count) as count  from \n" +
        " ((select count(o.id) as count from object o where o.id not in (select o.id from object o, user_vendor_mapping_object_mapping uvmom where uvmom.object_id=o.id and uvmom.user_vendor_mapping_id=?1) ) union ("+
		" select count(o.id) as count  "+
		"from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 ))a",resultSetMapping = "Mapping.ObjectDTOCount"
)





@NamedNativeQuery(name="Object.getExistingForNonGroupedSearch",
query = 
	"select \n"
	+ "a.id,\n"
	+ "a.projectId,\n"
	+ "a.userVendorMappingObjectMappingId,\n"
	+ "a.parentObjectId,\n"
	+ "a.numberOfUploadsRequired,\n"
	+ "a.numberOfBufferedUploadsRequired,\n"
	+ "a.totalRequired,\n"
	+ "a.totalUploaded, \n"
	+ "a.totalApproved, \n"
	+ "a.totalRejected, \n"
	+ "a.totalPending,\n"
	+ "a.bufferPercent,\n"
	+ "a.name,\n"
	+ "a.description,\n"
	+ "a.imageType,\n"
	+ "a.audioType,\n"
	+ "a.videoType, \n"
	+ "a.objectDescriptionLink \n"
	+ " from  \n"
	+ "(\n"
	+ "(\n"
	+ "select \n"
	+ "o.id,\n"
	+ "o.project_id as projectId,\n"
	+ "uvmom.id as userVendorMappingObjectMappingId,\n"
	+ "o.parent_object_id parentObjectId,\n"
	+ "o.number_of_uploads_required as numberOfUploadsRequired,\n"
	+ "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired,\n"
	+ "o.total_required as totalRequired,\n"
	+ "uvmom.total_uploaded as totalUploaded, \n"
	+ "uvmom.total_approved as totalApproved, \n"
	+ "uvmom.total_rejected as totalRejected, \n"
	+ "uvmom.total_pending as totalPending,\n"
	+ "o.buffer_percent as bufferPercent,\n"
	+ "o.name as name,\n"
	+ "o.description as description,\n"
	+ "o.image_type as imageType,\n"
	+ "o.audio_type as audioType,\n"
	+ "o.video_type as videoType, \n"
	+ "o.object_description_link as objectDescriptionLink \n"
	+ "from user_vendor_mapping_object_mapping uvmom,  \n"
	+ "object o\n"
	+ "where \n"
	+ "o.id=uvmom.object_id   \n"
	+ "and uvmom.user_vendor_mapping_id=?1 \n"
	+ "and uvmom.status=1 \n"
	+ "and o.status=1 and o.is_dummy=0  \n"
	+ "and o.project_id=?2 \n"
	+ "and o.name like CONCAT('%',?3,'%')\n"
	+ "order by o.id desc \n"
	+ ")\n"
	+ "union \n"
	+ "(\n"
	+ "select \n"
	+ "o.id,\n"
	+ "o.project_id as projectId,\n"
	+ "-1 as userVendorMappingObjectMappingId,\n"
	+ "o.parent_object_id parentObjectId,\n"
	+ "o.number_of_uploads_required as numberOfUploadsRequired,\n"
	+ "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired,\n"
	+ "o.total_required as totalRequired,\n"
	+ "0 as totalUploaded, \n"
	+ "0 as totalApproved, \n"
	+ "0 as totalRejected, \n"
	+ "0 as totalPending,\n"
	+ "o.buffer_percent as bufferPercent,\n"
	+ "o.name as name,\n"
	+ "o.description as description,\n"
	+ "o.image_type as imageType,\n"
	+ "o.audio_type as audioType,\n"
	+ "o.video_type as videoType, \n"
	+ "o.object_description_link as objectDescriptionLink \n"
	+ "from object o \n"
	+ "where o.status=1 \n"
	+ "and o.is_dummy=0  \n"
	+ "and o.project_id=?2 \n"
	+ "and o.name like CONCAT('%',?3,'%')\n"
	+ "and o.id not in \n"
	+ "(\n"
	+ "select \n"
	+ "o.id\n"
	+ "from user_vendor_mapping_object_mapping uvmom,  \n"
	+ "object o\n"
	+ "where \n"
	+ "o.id=uvmom.object_id   \n"
	+ "and uvmom.user_vendor_mapping_id=?1 \n"
	+ "and uvmom.status=1 \n"
	+ "and o.status=1 and o.is_dummy=0  \n"
	+ "and o.project_id=?2 \n"
	+ "and o.name like CONCAT('%',?3,'%')\n"
	+ ")\n"
	+ ")\n"
	+ ") a order by a.id"
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getExistingForNonGroupedSearch.count",
query = "select sum(a.count) as count  from \n" +
        " ((select count(o.id) as count from object o where o.name like CONCAT('%',?3,'%') and o.id not in (select o.id from object o, user_vendor_mapping_object_mapping uvmom where uvmom.object_id=o.id and uvmom.user_vendor_mapping_id=?1) ) union ("+
		" select count(o.id) as count  "+
		"from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "where uvmom.user_vendor_mapping_id=?1 and o.name like CONCAT('%',?3,'%') and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 ))a",resultSetMapping = "Mapping.ObjectDTOCount"
)




@NamedNativeQuery(name="Object.getExistingForGrouped",
query = "select \n" +
    "o.id," +
    "o.project_id as projectId,"+
    "uvmom.id as userVendorMappingObjectMappingId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "uvmom.total_uploaded as totalUploaded, \n" +
    "uvmom.total_approved as totalApproved, \n" +
    "uvmom.total_rejected as totalRejected, \n" +
    "uvmom.total_pending as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from user_vendor_mapping_object_mapping uvmom  \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.object_acquired_by_uvmom_id=uvmom.id  and o.project_id=?2 order by o.id  "
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getExistingForGrouped.count",
query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)


@NamedNativeQuery(name="Object.getExistingForGroupedSearch",
query = "select \n" +
    "o.id," +
    "o.project_id as projectId,"+
    "uvmom.id as userVendorMappingObjectMappingId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "uvmom.total_uploaded as totalUploaded, \n" +
    "uvmom.total_approved as totalApproved, \n" +
    "uvmom.total_rejected as totalRejected, \n" +
    "uvmom.total_pending as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from user_vendor_mapping_object_mapping uvmom  \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0  and o.project_id=?2 and o.name like CONCAT('%',?3,'%') order by o.id  "
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")




@NamedNativeQuery(name="Object.getExistingForGroupedSearch.count",
query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "where uvmom.user_vendor_mapping_id=?1 and o.name like CONCAT('%',?3,'%') and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)





@NamedNativeQuery(name="Object.getAllObjectsWithUvmom",
query = "select \n" +
    "o.id," +
    "uvmom.id as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "uvmom.total_uploaded as totalUploaded, \n" +
    "uvmom.total_approved as totalApproved, \n" +
    "uvmom.total_rejected as totalRejected, \n" +
    "uvmom.total_pending as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from user_vendor_mapping_object_mapping uvmom  \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0  and o.project_id=?2"
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getAllObjectsWithUvmom.count",
query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)







@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned",
    query = "select \n" +
        "o.id," +
        "uvmom.id as userVendorMappingObjectMappingId,"+
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_uploads_required as numberOfUploadsRequired," +
        "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
        "o.total_required as totalRequired," +
        "uvmom.id as userVendorMappingObjectMappingId," +
        "uvmom.total_uploaded as totalUploaded, \n" +
        "uvmom.total_approved as totalApproved, \n" +
        "uvmom.total_rejected as totalRejected, \n" +
        "uvmom.total_pending as totalPending,\n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) "
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned.count",
    query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) ",resultSetMapping = "Mapping.ObjectDTOCount"
)




@NamedNativeQuery(name="Object.getNewObjectsDto",
query = "select \n" +
    "o.id," +
    "-1 as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 as totalApproved, \n" +
    "0 as totalRejected, \n" +
    "0 as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from object o \n" +
    "where o.status=1 and o.is_dummy=0 and o.project_id=?1 and o.object_acquired_by_uvmom_id is  null order by o.id desc "
,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getNewObjectsDtoListForGrouped",
query = "select \n" +
    "o.id," +
    "-1 as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 as totalApproved, \n" +
    "0 as totalRejected, \n" +
    "0 as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from object o \n" +
    "where o.status=1 and o.is_dummy=0 and o.project_id=?1 and o.object_acquired_by_uvmom_id is  null and o.id>-1 order by o.id desc limit ?2 for update"
,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getNewObjectsDtoListForNonGrouped",
query = "select \n" +
    "o.id," +
    "-1 as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 as totalApproved, \n" +
    "0 as totalRejected, \n" +
    "0 as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from object o \n" +
    "where o.status=1 and o.is_dummy=0 and o.project_id=?1 and o.object_acquired_by_uvmom_id is  null and o.id>-1 order by o.id desc limit ?2 "
,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getNewObjectsDto.count",
query = "select ?2 as count  \n" +
    "from object o " +
    "where o.status=1 and o.is_dummy=0 and o.project_id=?1  and o.object_acquired_by_uvmom_id is null limit ?2",resultSetMapping = "Mapping.ObjectDTOCount"
)



@NamedNativeQuery(name="Object.getFreshObjects",
query = "select \n" +
    "o.id," +
    "-1 as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 as totalApproved, \n" +
    "0 as totalRejected, \n" +
    "0 as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType, " +
    "o.object_description_link as objectDescriptionLink " +
    "from object o \n" +
    "where o.status=1 and o.is_dummy=0 and o.project_id=?1 and o.object_acquired_by_uvmom_id is null order by o.id desc  limit ?2 for update"
,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getFreshObjects.count",
query = "select count(o.id)+?2 as count  \n" +
    "from object o,user_vendor_mapping_object_mapping uvmom " +
    "where uvmom.object_id=o.id and uvmom.user_vendor_mapping_id=?3 and o.status=1 and o.is_dummy=0 and o.project_id=?1  ",resultSetMapping = "Mapping.ObjectDTOCount"
)





@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectForDropdown",
    query = "select \n" +
        "o.id," +
        "uvmom.id as userVendorMappingObjectMappingId,"+
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_uploads_required as numberOfUploadsRequired," +
        "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
        "o.total_required as totalRequired," +
        "uvmom.total_uploaded as totalUploaded, \n" +
        "uvmom.total_approved as totalApproved, \n" +
        "uvmom.total_rejected as totalRejected, \n" +
        "uvmom.total_pending as totalPending,\n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2"
        ,resultSetMapping = "Mapping.ObjectDTOForDropdown")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectForDropdown.count",
    query ="select count(o.id) as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)


@NamedNativeQuery(
    name = "Object.getAllObjectDTOsOfProjectOld",
    query="select id,name from object where status=1 and is_dummy=0 and project_id=?1",
    resultSetMapping = "Mapping.getAllObjectDTOsOfProject"
)

@NamedNativeQuery(
    name = "Object.getAllObjectDTOsOfProject",
    query="select o.id,o.name from object o, consolidated_user_vendor_mapping_object_mapping_view cuvmomv where o.status=1 and o.is_dummy=0 and o.id=cuvmomv.object_id and cuvmomv.uvmom_status>0 and o.project_id=?1 ",
    resultSetMapping = "Mapping.getAllObjectDTOsOfProject"
)



@NamedNativeQuery(
	    name = "Object.getAllObjectDTOsOfProjectForMetadata",
	    query="select\n"
	    		+ "o.id,\n"
	    		+ "o.name\n"
	    		+ "from upload_meta_data umd, "
	    		+ "upload u,project_property pp,"
	    		+ "property pr,user_vendor_mapping_object_mapping uvmom, "
	    		+ "object o,user_vendor_mapping uvm,project p \n"
	    		+ "where \n"
	    		+ "umd.upload_id=u.id \n"
	    		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id \n"
	    		+ "and uvmom.object_id=o.id \n"
	    		+ "and o.project_id=p.id\n"
	    		+ "and uvmom.user_vendor_mapping_id=uvm.id \n"
	    		+ "and umd.project_property_id=pp.id \n"
	    		+ "and pp.property_id=pr.id \n"
	    		+ "and project_property_id is not null \n"
	    		+ "and uvmom.object_id=o.id and o.project_id=?1 \n"
	    		+ "and uvm.id=?2 \n"
	    		+ "and  u.metadata_status=0 group by o.id order by o.id",
	    resultSetMapping = "Mapping.getAllObjectDTOsOfProject"
	)


@NamedNativeQuery(
	    name = "Object.getAllObjectDTOsOfProjectForMetadataForOtherThanVendorUser",
	    query="select\n"
	    		+ "o.id,\n"
	    		+ "o.name\n"
	    		+ "from upload_meta_data umd, "
	    		+ "upload u,project_property pp,"
	    		+ "property pr,user_vendor_mapping_object_mapping uvmom, "
	    		+ "object o,project p \n"
	    		+ "where \n"
	    		+ "umd.upload_id=u.id \n"
	    		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id \n"
	    		+ "and uvmom.object_id=o.id \n"
	    		+ "and o.project_id=p.id\n"
	    		+ "and umd.project_property_id=pp.id \n"
	    		+ "and pp.property_id=pr.id \n"
	    		+ "and project_property_id is not null \n"
	    		+ "and uvmom.object_id=o.id and o.project_id=?1 \n"
	    		+ "and  u.metadata_status=0 group by o.id order by o.id",
	    resultSetMapping = "Mapping.getAllObjectDTOsOfProject"
	)


@Entity
@Table(name = "object",indexes = {
    @Index(name="idx_object_parent_object",columnList = "parent_object_id"),
    @Index(name="idx_object_project",columnList = "project_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_object_project_name",columnNames={"name", "project_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "object")
@Audited
public class Object extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", length = 500, nullable = true)
    private String name;

    @Column(name="buffer_percent")
    private Integer bufferPercent=20;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "number_of_uploads_required", nullable = true)
    private Integer numberOfUploadsRequired=0;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_object_project"))
    private Project project;

    @Column(name="is_dummy")
    private Integer dummy=0;
    @ManyToOne(optional = true)
    @JsonIgnoreProperties(value = { "project","customer" }, allowSetters = true)
    @JoinColumn(name = "parent_object_id", nullable = true, foreignKey = @ForeignKey(name="fk_object_parent_object"))
    private Object parentObject;

    @Column(name ="total_uploaded",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalUploaded=0;

    @Column(name ="total_approved",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalApproved=0;

    @Column(name ="total_rejected",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalRejected=0;

    @Column(name ="total_pending",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalPending=0;

    @Column(name ="total_required",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalRequired=0;

    @Column(name ="number_of_buffered_uploads_required",columnDefinition = "integer default 0")
    private Integer numberOfBufferedUploadsRequired=0;


    @Column(name="image_type")
    @JsonProperty
    private String imageType="";
    @Column(name="video_type")
    @JsonProperty
    private String videoType="";
    @Column(name="audio_type")
    @JsonProperty
    private String audioType="";

    @OneToMany(mappedBy = "object",cascade = CascadeType.ALL)
    @Filter(name = "objectPropertyStatusFilter",condition="status = 1")
    @JsonIgnoreProperties(value = { "object" }, allowSetters = true)
    private Set<ObjectProperty> objectProperties = new HashSet<>();

    //This is userVendorMappingObjectMappingId not the userId
    @Column(name="object_acquired_by_uvmom_id" ,columnDefinition = "integer default null")
    private Long objectAcquiredByUvmomId;

    
    @Column(name="qc_start_status" ,columnDefinition = "integer default null")
    private Integer qcStartStatus=0;

    @Column(name="current_qc_level" ,columnDefinition = "integer default null")
    private Integer currentQcLevel=0;

    @Column(name="object_description_link")
    private String objectDescriptionLink;
    
    public String getObjectDescriptionLink() {
		return objectDescriptionLink;
	}

	public void setObjectDescriptionLink(String objectDescriptionLink) {
		this.objectDescriptionLink = objectDescriptionLink;
	}

	public Integer getCurrentQcLevel() {
		return currentQcLevel;
	}

	public void setCurrentQcLevel(Integer currentQcLevel) {
		this.currentQcLevel = currentQcLevel;
	}

	public Integer getQcStartStatus() {
        return qcStartStatus;
    }

    public void setQcStartStatus(Integer qcStartStatus) {
        this.qcStartStatus = qcStartStatus;
    }

    @Transient
    @JsonProperty
    private Long userVendorMappingObjectMappingId;



    public Long getUserVendorMappingObjectMappingId() {
        return userVendorMappingObjectMappingId;
    }

    public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId) {
        this.userVendorMappingObjectMappingId = userVendorMappingObjectMappingId;
    }
/*@Column(name="batch_status" ,columnDefinition = "integer default null")
    private Integer batchStatus;

    public Integer getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(Integer batchStatus) {
        this.batchStatus = batchStatus;
    }*/

    public Long getObjectAcquiredByUvmomId() {
        return objectAcquiredByUvmomId;
    }

    public void setObjectAcquiredByUvmomId(Long objectAcquiredByUvmomId) {
        this.objectAcquiredByUvmomId = objectAcquiredByUvmomId;
    }

    public Integer getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Integer totalRequired) {
        this.totalRequired = totalRequired;
    }

    public Integer getDummy() {
        return dummy;
    }

    public void setDummy(Integer dummy) {
        this.dummy = dummy;
    }

    public Object getParentObject() {
        return parentObject;
    }

    public void setParentObject(Object parentObject) {
        this.parentObject = parentObject;
    }

    public Object getParentAidasObject() {
        return parentObject;
    }

    public void setParentAidasObject(Object parentObject) {
        this.parentObject = parentObject;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public Integer getTotalUploaded() {
        return totalUploaded;
    }

    public void setTotalUploaded(Integer totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    public Integer getTotalApproved() {
        return totalApproved;
    }

    public void setTotalApproved(Integer totalApproved) {
        this.totalApproved = totalApproved;
    }

    public Integer getTotalRejected() {
        return totalRejected;
    }

    public void setTotalRejected(Integer totalRejected) {
        this.totalRejected = totalRejected;
    }

    public Integer getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(Integer totalPending) {
        this.totalPending = totalPending;
    }

    public Integer getBufferPercent() {
        return bufferPercent;
    }

    public void setBufferPercent(Integer bufferPercent) {
        this.bufferPercent = bufferPercent;
    }

    public Set<ObjectProperty> getObjectProperties() {
        return objectProperties;
    }

    public void setObjectProperties(Set<ObjectProperty> objectProperties) {
        this.objectProperties = objectProperties;
    }

    public void addAidasObjectProperty(ObjectProperty objectProperty){
        this.objectProperties.add(objectProperty);
    }

    public void removeAidasObjectProperty(ObjectProperty objectProperty){
        this.objectProperties.remove(objectProperty);
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object id(Long id) {
        this.setId(id);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object name(String name) {
        this.setName(name);
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object description(String description) {
        this.setDescription(description);
        return this;
    }

    public Integer getNumberOfUploadsRequired() {
        return numberOfUploadsRequired;
    }

    public void setNumberOfUploadsRequired(Integer numberOfUploadsRequired) {
        this.numberOfUploadsRequired = numberOfUploadsRequired;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Object project(Project project) {
        this.setProject(project);
        return this;
    }

    public Integer getNumberOfBufferedUploadsRequired() {
        return numberOfBufferedUploadsRequired;
    }

    public void setNumberOfBufferedUploadsRequired(Integer numberOfBufferedUploadsRequired) {
        this.numberOfBufferedUploadsRequired = numberOfBufferedUploadsRequired;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Object)) {
            return false;
        }
        return id != null && id.equals(((Object) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AidasObject{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", numberOfUploadReqd=" + getNumberOfUploadsRequired() +
            "}";
    }
}
