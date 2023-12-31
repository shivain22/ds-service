package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.UploadSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.AESCBCPKCS5Padding;
import com.ainnotate.aidas.service.DownloadUploadS3;
import com.ainnotate.aidas.service.UploadMetaDataService;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectWriter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Upload}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UploadResource {

	private final Logger log = LoggerFactory.getLogger(UploadResource.class);

	private static final String ENTITY_NAME = "ainnotateserviceAidasUpload";

	@Value("${jhipster.clientApp.name}")
	private String applicationName;

	private static final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ObjectRepository objectRepository;
	@Autowired
	private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

	@Autowired
	private ProjectPropertyRepository projectPropertyRepository;

	@Autowired
	UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;
	@Autowired
	private PropertyRepository propertyRepository;
	@Autowired
	private ObjectPropertyRepository objectPropertyRepository;

	private final UploadRepository uploadRepository;

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private QcProjectMappingRepository qcProjectMappingRepository;

	@Autowired
	private UploadQcProjectMappingBatchInfoRepository uploadQcProjectMappingBatchInfoRepository;

	private final UploadSearchRepository aidasUploadSearchRepository;

	@Autowired
	private UploadMetaDataRepository uploadMetaDataRepository;

	@Autowired
	private UploadRejectReasonRepository uploadRejectReasonRepository;

	@Autowired
	private UploadRejectReasonMappingRepository uploadRejectReasonMappingRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectQcLevelConfigurationsRepository projectQcLevelConfigurationsRepository;

	@Autowired
	private QcProjectMappingBatchMappingRepository qcProjectMappingBatchMappingRepository;

	@Autowired
	private UserVendorMappingRepository userVendorMappingRepository;

	/*
	 * @Autowired UploadMetaDataService uploadMetaDataService;
	 */

	public UploadResource(UploadRepository uploadRepository, UploadSearchRepository aidasUploadSearchRepository) {
		this.uploadRepository = uploadRepository;
		this.aidasUploadSearchRepository = aidasUploadSearchRepository;
	}

	Integer totalApproved = 0;
	Integer totalRejected = 0;
	Integer totalPending = 0;

	/**
	 * {@code POST  /aidas-uploads} : Create a new upload.
	 *
	 * @param uploadMetadataDTOS the list of uploadMetadata to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new upload, or with status {@code 400 (Bad Request)} if the
	 *         upload has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/aidas-uploads/metadata")
	public ResponseEntity<List<UploadMetadataDTO>> submitMetadata(
			@RequestBody List<UploadMetadataDTO> uploadMetadataDTOS) throws URISyntaxException {
		log.debug("REST request to save AidasUpload : {}", uploadMetadataDTOS);
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		Authority authority = user.getAuthority();
		Map<Long, UploadDTO> udtoMap = new HashMap<>();
		Map<Long, Integer> ppOptional = new HashMap<>();
		Map<Long, Integer> opOptional = new HashMap<>();
		if (authority.getName().equals(AidasConstants.VENDOR_USER)) {

			for (UploadMetadataDTO umd : uploadMetadataDTOS) {
				if (udtoMap.get(umd.getUploadId()) == null) {
					UploadDTO uploadDTO = new UploadDTO();
					List<java.lang.Object[]> upload = uploadRepository
							.findAllByUserAndProjectAllForMetadataObjectWiseNew(umd.getUploadId());
					uploadDTO.setUploadId(umd.getUploadId());
					uploadDTO.setName(upload.get(0)[1].toString());
					uploadDTO.setObjectKey(upload.get(0)[2].toString());
					udtoMap.put(umd.getUploadId(), uploadDTO);
				}
				if (umd.getProjectProperty() != null && umd.getProjectProperty()
						&& umd.getProjectPropertyId() != null) {
					if (ppOptional.get(umd.getProjectPropertyId()) == null) {
						ppOptional.put(umd.getProjectPropertyId(),
								projectPropertyRepository.getOptionalOfProjectProperty(umd.getProjectPropertyId()));
					}
					if (ppOptional.get(umd.getProjectPropertyId()).equals(0)) {
						if (umd.getValue() == null || (umd.getValue() != null && umd.getValue().trim().length() == 0)) {
							umd.setFailed(true);
						}
					}
					uploadMetaDataRepository.updateUploadMetadataProjectProperty(umd.getValue(), umd.getUploadId(),
							umd.getProjectPropertyId());
					umd.setProjectPropertyId(umd.getProjectPropertyId());

				}
				if (umd.getProjectProperty() != null && !umd.getProjectProperty()
						&& umd.getObjectPropertyId() != null) {
					if (opOptional.get(umd.getProjectPropertyId()) == null) {
						opOptional.put(umd.getObjectPropertyId(),
								objectPropertyRepository.getOptionalOfObjectProperty(umd.getObjectPropertyId()));
					}
					if (opOptional.get(umd.getProjectPropertyId()).equals(0)) {
						if (umd.getValue() == null || (umd.getValue() != null && umd.getValue().trim().length() == 0)) {
							umd.setFailed(true);
						}
					}
					uploadMetaDataRepository.updateUploadMetadataObjectProperty(umd.getValue(), umd.getUploadId(),
							umd.getObjectPropertyId());
					umd.setObjectPropertyId(umd.getObjectPropertyId());

				}
				umd.setUploadDTO(udtoMap.get(umd.getUploadId()));
			}
			return ResponseEntity.ok().body(uploadMetadataDTOS);
		}
		throw new BadRequestAlertException("You can not upload as there was an error internally", ENTITY_NAME,
				"idexists");
	}

	/**
	 * {@code POST  /aidas-uploads} : Create a new upload.
	 *
	 * @param uploadMetadataDTOS the list of uploadMetadata to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new upload, or with status {@code 400 (Bad Request)} if the
	 *         upload has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/aidas-uploads/qc")
	public ResponseEntity<List<UploadMetadataDTO>> submitForQc(@RequestBody List<UploadMetadataDTO> uploadMetadataDTOS)
			throws URISyntaxException {
		List<UploadMetadataDTO> uploadMetadataDTOS1 = new ArrayList<>();
		List<Upload> successUploads = new ArrayList<>();
		log.debug("REST request to save AidasUpload : {}", uploadMetadataDTOS);
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		Authority authority = user.getAuthority();
		Map<Long, UploadDTO> udtoMap = new HashMap<>();
		Map<Long, Integer> ppOptional = new HashMap<>();
		Map<Long, Integer> opOptional = new HashMap<>();
		if (authority.getName().equals(AidasConstants.VENDOR_USER)) {
			int j = 0;
			for (UploadMetadataDTO umd : uploadMetadataDTOS) {
				int i = 0;
				if (udtoMap.get(umd.getUploadId()) == null) {
					UploadDTO uploadDTO = new UploadDTO();
					List<java.lang.Object[]> upload = uploadRepository
							.findAllByUserAndProjectAllForMetadataObjectWiseNew(umd.getUploadId());
					uploadDTO.setUploadId(umd.getUploadId());
					uploadDTO.setName(upload.get(0)[1].toString());
					uploadDTO.setObjectKey(upload.get(0)[2].toString());
					udtoMap.put(umd.getUploadId(), uploadDTO);
				}
				if (umd.getProjectProperty() != null && umd.getProjectProperty()
						&& umd.getProjectPropertyId() != null) {
					if (ppOptional.get(umd.getProjectPropertyId()) == null) {
						ppOptional.put(umd.getProjectPropertyId(),
								projectPropertyRepository.getOptionalOfProjectProperty(umd.getProjectPropertyId()));
					}
					if (ppOptional.get(umd.getProjectPropertyId()).equals(0)) {
						if (umd.getValue() == null || (umd.getValue() != null && umd.getValue().trim().length() == 0)) {
							umd.setFailed(true);
							i++;
							j++;
						}
					}
					uploadMetaDataRepository.updateUploadMetadataProjectProperty(umd.getValue(), umd.getUploadId(),
							umd.getProjectPropertyId());
					umd.setProjectPropertyId(umd.getProjectPropertyId());

				}
				if (umd.getProjectProperty() != null && !umd.getProjectProperty()
						&& umd.getObjectPropertyId() != null) {
					if (opOptional.get(umd.getProjectPropertyId()) == null) {
						opOptional.put(umd.getObjectPropertyId(),
								objectPropertyRepository.getOptionalOfObjectProperty(umd.getObjectPropertyId()));
					}
					if (opOptional.get(umd.getProjectPropertyId()).equals(0)) {
						if (umd.getValue() == null || (umd.getValue() != null && umd.getValue().trim().length() == 0)) {
							umd.setFailed(true);
							i++;
							j++;
						}
					}
					uploadMetaDataRepository.updateUploadMetadataObjectProperty(umd.getValue(), umd.getUploadId(),
							umd.getObjectPropertyId());
					umd.setObjectPropertyId(umd.getObjectPropertyId());

				}
				umd.setUploadDTO(udtoMap.get(umd.getUploadId()));
				if (i == 0) {
					Upload upload = uploadRepository.getUploadQcDoneByIsNull(umd.getUploadId());
					successUploads.add(upload);
				} else {
					uploadMetadataDTOS1.add(umd);
				}

			}
			if (j > 0) {
				return ResponseEntity.ok().body(uploadMetadataDTOS);
			}
			for (Upload u : successUploads) {
				u = uploadRepository.getUploadQcDoneByIsNull(u.getId());
				uploadRepository.updateMetadataStatus(u.getId(), AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
				uploadRepository.updateUvmomQcStatus(u.getUserVendorMappingObjectMapping().getId());
				uploadRepository.updateObjectQcStatus(u.getUserVendorMappingObjectMapping().getObject().getId());
				uploadRepository
						.updateProjectQcStatus(u.getUserVendorMappingObjectMapping().getObject().getProject().getId());
			}
		}
		return ResponseEntity.ok().body(uploadMetadataDTOS1);
	}

	@GetMapping("/aidas-objects/{id}/more-required")
	public ResponseEntity<Integer> getObjectStatus(@PathVariable(value = "id", required = false) final Long objectId) {
		log.debug("REST request to get a page of AidasObjects");
		Integer moreRequired = objectRepository.getMoreRequiredForUpload(objectId);
		return ResponseEntity.ok().body(moreRequired);
	}

	/**
	 * {@code POST  /aidas-uploads/dto} : Create a new upload.
	 *
	 * @param uploadDto the upload to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new upload, or with status {@code 400 (Bad Request)} if the
	 *         upload has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/aidas-uploads/dto")
	public ResponseEntity<Boolean> createAidasUploadFromDto(@RequestBody UploadDTO uploadDto)
			throws URISyntaxException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(uploadDto);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Object object = objectRepository.getByIdForUpdate(uploadDto.getObjectId());

		UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository
				.getById(uploadDto.getUserVendorMappingObjectMappingId());
		UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository
				.findByUserVendorMappingIdProjectIdForUpload(uvmom.getUserVendorMapping().getId(),
						uvmom.getObject().getProject().getId());
		Upload upload = uploadRepository.getUploadByFileNameUvmomId(uvmom.getId(), uploadDto.getObjectKey());
		if (uploadDto.getConsentFormUrl() != null && uploadDto.getConsentFormUrl().trim().length() > 0) {
			try {
				uvmom.setConsentFormUrl(uploadDto.getConsentFormUrl());
				String tempFolder = System.getProperty("java.io.tmpdir");

				String accessKey = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(uvmom.getObject().getProject().getId(), "accessKey")
						.getValue().getBytes(), AidasConstants.KEY, AidasConstants.IV_STR);
				String accessSecret = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(uvmom.getObject().getProject().getId(), "accessSecret")
						.getValue().getBytes(), AidasConstants.KEY, AidasConstants.IV_STR);
				String region = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(uvmom.getObject().getProject().getId(), "region")
						.getValue().getBytes(), AidasConstants.KEY, AidasConstants.IV_STR);
				String bucketName = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(uvmom.getObject().getProject().getId(), "bucketName")
						.getValue().getBytes(), AidasConstants.KEY, AidasConstants.IV_STR);

				Path dest = Paths.get(tempFolder + "/" + bucketName + "/" + uploadDto.getConsentFormUrl());
				if (Files.exists(dest)) {
					Files.delete(dest);
				}
				Files.createDirectories(dest.getParent());
				PipedOutputStream os = new PipedOutputStream();
				PipedInputStream is = new PipedInputStream(os);
				AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, accessSecret);
				S3Client s3client = S3Client.builder().region(Region.of(region))
						.credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
				GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName)
						.key(uploadDto.getConsentFormUrl()).build();

				s3client.getObject(getObjectRequest, ResponseTransformer.toFile(dest));
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> consentFormFields = objectMapper.readValue(Files.readAllBytes(dest), Map.class);

				for (Map.Entry<String, String> entry : consentFormFields.entrySet()) {
					if (!entry.getKey().equals("signature") && !entry.getKey().equals("isChecked")) {
						Property property = propertyRepository.getByNameAndUserIdAndCategory(entry.getKey(),
								object.getProject().getCustomer().getId(), object.getProject().getCategory().getId());
						if (property == null || property.getId() == null) {
							property = new Property();
							property.setName(entry.getKey());
							property.setCustomer(object.getProject().getCustomer());
							property.setPropertyType(AidasConstants.AIDAS_SYSTEM_PROPERTY);
							property.setValue("");
							property.setAddToMetadata(AidasConstants.STATUS_ENABLED);
							property.setShowToVendorUser(AidasConstants.STATUS_DISABLED);
							property.setCategory(object.getProject().getCategory());
							property.setDefaultProp(AidasConstants.STATUS_DISABLED);
							property.setDescription(entry.getKey());
							property.setPassedFromApp(AidasConstants.STATUS_ENABLED);
							property.setOptional(AidasConstants.AIDAS_PROPERTY_REQUIRED);
							propertyRepository.save(property);
						}
						ProjectProperty projectProperty = projectPropertyRepository
								.findByProjectPropertyByPropertyName(object.getProject().getId(), entry.getKey());
						if (projectProperty == null || property.getId() == null) {
							projectProperty = new ProjectProperty();
							projectProperty.setProject(object.getProject());
							projectProperty.setStatus(AidasConstants.STATUS_ENABLED);
							projectProperty.setAddToMetadata(AidasConstants.STATUS_ENABLED);
							projectProperty.setDefaultProp(AidasConstants.STATUS_DISABLED);
							projectProperty.setOptional(AidasConstants.AIDAS_PROPERTY_REQUIRED);
							projectProperty.setValue("");
							projectProperty.setProperty(property);
							projectPropertyRepository.save(projectProperty);
						}
						List<Upload> uploads = uploadRepository.getUploadByUvmomId(uvmom.getId());
						for (Upload u : uploads) {
							UploadMetaData umd = uploadMetaDataRepository
									.getUploadMetaDataByProjectPropertyId(u.getId(), projectProperty.getId());
							if (umd == null) {
								umd = new UploadMetaData();
								umd.setProjectProperty(projectProperty);
								if(entry.getValue()!=null) {
									umd.setValue(entry.getValue().toString());
								}else {
									umd.setValue("");
								}
								umd.setUpload(u);

							} else {
								if(entry.getValue()!=null) {
									umd.setValue(entry.getValue().toString());
								}else {
									umd.setValue("");
								}
							}
							uploadMetaDataRepository.save(umd);
						}
					}
				}
				userVendorMappingObjectMappingRepository.save(uvmom);
				return ResponseEntity.ok().body(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        else {
            if (object.getTotalRequired() > 0) {
                if (upload != null) {
                    throw new BadRequestAlertException(
                        "Upload with object key " + upload.getObjectKey() + " exists.  duplicate entry", ENTITY_NAME,
                        "idexists");
                }
                try {
                    upload = new Upload();
                    upload.setUserVendorMappingObjectMapping(uvmom);
                    upload.setDateUploaded(Instant.now());
                    upload.setName(uploadDto.getObjectKey());
                    upload.setUploadUrl(uploadDto.getUploadUrl());
                    upload.setUploadEtag(uploadDto.getEtag());
                    upload.setObjectKey(uploadDto.getObjectKey());
                    upload.setCurrentQcLevel(1);
                    upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
                    upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                    upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_REQUIRED);
                    upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                    upload.setCurrentBatchNumber(0);
                    upload = uploadRepository.save(upload);
                    List<String> uploadMetaDataKeys = new ArrayList<>();
                    for (Map.Entry<String, String> entry : uploadDto.getUploadMetadata().entrySet()) {
                        uploadMetaDataKeys.add(entry.getKey());
                    }
                    List<UploadMetaData> metadatas = uploadMetaDataRepository
                        .getAllUploadMetaDatasProjectProperty(upload.getId(), uploadMetaDataKeys);
                    metadatas.addAll(uploadMetaDataRepository.getAllUploadMetaDatasObjectProperty(upload.getId(),
                        uploadMetaDataKeys));
                    for (UploadMetaData umd : metadatas) {
                        if (umd.getProjectProperty() != null) {
                            umd.setValue(
                                uploadDto.getUploadMetadata().get(umd.getProjectProperty().getProperty().getName()));
                        }
                        if (umd.getObjectProperty() != null) {
                            umd.setValue(
                                uploadDto.getUploadMetadata().get(umd.getObjectProperty().getProperty().getName()));
                        }
                    }
                    uploadMetaDataRepository.saveAll(metadatas);
                    return ResponseEntity.ok().body(true);
                } catch (Exception e) {
                    System.out.println(uploadDto.getObjectId() + "===" + uploadDto.getUserVendorMappingObjectMappingId()
                        + e.getMessage());
                    return ResponseEntity.ok().body(false);
                }

            } else {
                return ResponseEntity.ok().body(false);
            }
        }
        return ResponseEntity.ok().body(false);
	}

	/**
	 * {@code PUT  /aidas-uploads/:id} : Approve an existing upload.
	 *
	 * @param id the id of the upload to save.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated upload, or with status {@code 400 (Bad Request)} if the
	 *         upload is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the upload couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/aidas-uploads/approve/new/{id}/{qcProjectMappingId}")
	public ResponseEntity<Boolean> approveAidasUploadNew(@PathVariable(value = "id", required = false) final Long id,
			@PathVariable(value = "qcProjectMappingId", required = false) final Long qcProjectMappingId)
			throws URISyntaxException {
		Upload upload = uploadRepository.getById(id);
		QcProjectMapping qpm = qcProjectMappingRepository.getById(qcProjectMappingId);
		UploadQcProjectMappingBatchInfo uqpmbi = uploadQcProjectMappingBatchInfoRepository
				.getUploadIdByqcProjectMappingAndBatchNumber(qcProjectMappingId, qpm.getCurrentQcBatchNo(),
						upload.getId());

		if (qpm.getQcLevel().equals(qpm.getProject().getQcLevels())) {
			upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
		}
		if (upload.getApprovalStatus().equals(AidasConstants.AIDAS_UPLOAD_REJECTED)) {
			if (qpm.getQcLevel().equals(qpm.getProject().getQcLevels())) {
				upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
			} else {
				upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
			}
		} else {
			upload.setCurrentQcLevel(qpm.getQcLevel() + 1);
		}
		upload.setQcDoneBy(qpm);
		uqpmbi.setQcSeenStatus(AidasConstants.AIDAS_QC_VIWED);
		uqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
		upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
		uploadQcProjectMappingBatchInfoRepository.save(uqpmbi);
		return ResponseEntity.ok().body(true);
	}

	/**
	 * {@code PUT  /aidas-uploads/:id} : Reject an existing upload.
	 *
	 * @param qcProjectMappingId the id of the upload to save.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated upload, or with status {@code 400 (Bad Request)} if the
	 *         upload is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the upload couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/aidas-uploads/finalize-qc/{qcProjectMappingId}/{batchNumber}")
	public ResponseEntity<Boolean> finalizeQc(
			@PathVariable(value = "qcProjectMappingId", required = false) final Long qcProjectMappingId,
			@PathVariable(value = "batchNumber", required = false) final Long batchNumber) throws URISyntaxException {
		QcProjectMapping qpm = qcProjectMappingRepository.getById(qcProjectMappingId);
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

		int totalPendings = uploadQcProjectMappingBatchInfoRepository
				.getQcStatusPendingWithShowToQcEnabled(qcProjectMappingId, batchNumber);
		if (totalPendings == 0) {
			if (qpm.getProject().getQcLevels().equals(qpm.getQcLevel())) {
				uploadQcProjectMappingBatchInfoRepository.updateUqpmbiUploadFinalLevel(qcProjectMappingId, batchNumber);
			} else {
				uploadQcProjectMappingBatchInfoRepository.updateUqpmbi(qcProjectMappingId, batchNumber,
						user.getLogin());
			}

			Map<Long, UploadSummaryForQCFinalize> uvmpmBatchStatus = new HashMap<>();
			Map<Long, UploadSummaryForQCFinalize> projectBatchStatus = new HashMap<>();

			List<UploadSummaryForQCFinalize> batchStatus = uploadQcProjectMappingBatchInfoRepository
					.getUvmomObjectIdsOfBatch(qcProjectMappingId, batchNumber);
			int groupedTotalReject = 0;
			if (batchStatus != null && batchStatus.size() > 0) {
				for (UploadSummaryForQCFinalize usfqcf : batchStatus) {
					if (usfqcf.getTotalRejected() > 0 && usfqcf.getTotalShowToQc() > 0) {
						groupedTotalReject++;
						if (qpm.getProject().getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
							userVendorMappingProjectMappingRepository
									.addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForGrouped(
											usfqcf.getUvmpmId(), 1l);
							projectRepository.addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForGrouped(
									usfqcf.getProjectId(), 1l);
							userVendorMappingObjectMappingRepository
									.addTotalRejectedAndSubtractTotalPendingAddTotalRequired(usfqcf.getUvmomId(),
											usfqcf.getTotalRejected());
							objectRepository.addTotalRejectedAndSubtractTotalPendingAddTotalRequired(
									usfqcf.getObjectId(), usfqcf.getTotalRejected());
						} else {
							List<UploadSummaryForQCFinalize> batchStatus1 = uploadQcProjectMappingBatchInfoRepository
									.getUvmomObjectIdsOfBatchNonGrouped(usfqcf.getProjectId(), usfqcf.getUvmId());
							if (batchStatus1 != null && batchStatus1.size() > 0) {
								userVendorMappingProjectMappingRepository
										.addTotalRejectedAndSubtractTotalPendingAddTotalRequired(
												batchStatus1.get(0).getUvmpmId(),
												batchStatus1.get(0).getTotalRejected(),
												batchStatus1.get(0).getTotalPending());
								List<UploadSummaryForQCFinalize> batchStatus2 = uploadQcProjectMappingBatchInfoRepository
										.getUvmomObjectIdsOfBatchNonGroupedForProject(usfqcf.getProjectId());
								if (batchStatus2 != null && batchStatus2.size() > 0) {
									projectRepository.addTotalApprovedSubtractTotalPendingNonGrouped(
											batchStatus2.get(0).getProjectId(), batchStatus2.get(0).getTotalApproved(),
											batchStatus2.get(0).getTotalRejected(),
											batchStatus2.get(0).getTotalPending());
									List<UploadSummaryForQCFinalize> batchStatus4 = uploadQcProjectMappingBatchInfoRepository
											.getUvmomObjectIdsOfBatchNonGroupedForProjectNew(usfqcf.getObjectId(),
													usfqcf.getUvmomId());
									if (batchStatus4 != null && batchStatus4.size() > 0) {
										userVendorMappingObjectMappingRepository
												.addTotalRejectedAndSubtractTotalPendingAddTotalRequiredNonGrouped(
														usfqcf.getUvmomId(), batchStatus4.get(0).getTotalRejected(),
														batchStatus4.get(0).getTotalPending());
									}
									List<UploadSummaryForQCFinalize> batchStatus3 = uploadQcProjectMappingBatchInfoRepository
											.getObjectIdsOfBatchNonGroupedForProject(usfqcf.getObjectId());
									if (batchStatus3 != null && batchStatus3.size() > 0) {
										objectRepository.addTotalApprovedSubtractTotalPendingNew(usfqcf.getObjectId(),
												batchStatus3.get(0).getTotalApproved(),
												batchStatus3.get(0).getTotalRejected(),
												batchStatus3.get(0).getTotalPending());
									}
								}
							}
						}
					}
					if (qpm.getProject().getQcLevels().equals(qpm.getQcLevel())) {
						if (qpm.getProject().getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
							userVendorMappingObjectMappingRepository.addTotalApprovedSubtractTotalPending(
									usfqcf.getUvmomId(), usfqcf.getTotalApproved(), usfqcf.getTotalPending());
							objectRepository.addTotalApprovedSubtractTotalPending(usfqcf.getObjectId(),
									usfqcf.getTotalApproved());
							if (usfqcf.getTotalUploaded().equals(usfqcf.getTotalApproved())) {
								userVendorMappingProjectMappingRepository
										.addTotalApprovedSubtractTotalPendingForGrouped(usfqcf.getUvmpmId(), 1);
								projectRepository.addTotalApprovedSubtractTotalPendingForGrouped(usfqcf.getProjectId(),
										1);
							}
						} else {
							List<UploadSummaryForQCFinalize> batchStatus1 = uploadQcProjectMappingBatchInfoRepository
									.getUvmpmObjectIdsOfBatchNonGroupedForProjectNew(usfqcf.getProjectId(),
											usfqcf.getUvmpmId());
							if (batchStatus1 != null && batchStatus1.size() > 0) {
								userVendorMappingProjectMappingRepository
										.addTotalApprovedSubtractTotalPendingForNonGrouped(
												batchStatus1.get(0).getProjectId(),
												batchStatus1.get(0).getTotalApproved(),
												batchStatus1.get(0).getTotalPending());
								List<UploadSummaryForQCFinalize> batchStatus2 = uploadQcProjectMappingBatchInfoRepository
										.getUvmomObjectIdsOfBatchNonGroupedForProject(
												batchStatus.get(0).getProjectId());
								if (batchStatus2 != null && batchStatus2.size() > 0) {
									projectRepository.addTotalApprovedSubtractTotalPendingNonGrouped(
											batchStatus2.get(0).getProjectId(), batchStatus2.get(0).getTotalApproved(),
											batchStatus2.get(0).getTotalRejected(),
											batchStatus2.get(0).getTotalPending());
									List<UploadSummaryForQCFinalize> batchStatus3 = uploadQcProjectMappingBatchInfoRepository
											.getObjectIdsOfBatchNonGroupedForProject(usfqcf.getObjectId());
									if (batchStatus3 != null && batchStatus3.size() > 0) {
										objectRepository.addTotalApprovedSubtractTotalPendingNewForApproved(
												usfqcf.getObjectId(), batchStatus3.get(0).getTotalApproved(),
												batchStatus3.get(0).getTotalPending());
									}
									List<UploadSummaryForQCFinalize> batchStatus4 = uploadQcProjectMappingBatchInfoRepository
											.getUvmomObjectIdsOfBatchNonGroupedForProjectNew(usfqcf.getObjectId(),
													usfqcf.getUvmomId());
									userVendorMappingObjectMappingRepository.addTotalApprovedSubtractTotalPending(
											usfqcf.getUvmomId(), batchStatus4.get(0).getTotalApproved(),
											batchStatus4.get(0).getTotalPending());
								}
							}
						}
					}
				}
			}
			UploadSummaryForQCFinalize batchInfo = uploadQcProjectMappingBatchInfoRepository
					.countUploadsByqcProjectMappingAndBatchNumberForFinalize(qcProjectMappingId, batchNumber);
			if (batchInfo != null) {
				QcProjectMappingBatchMapping qpmbm = qcProjectMappingBatchMappingRepository.getById(batchNumber);
				if (batchInfo.getTotalUploaded().equals(batchInfo.getTotalApproved())) {
					qpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_APPROVED);
				} else if (qpm.getProject().getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)
						&& batchStatus.size() == groupedTotalReject) {
					qpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_REJECTED);
					uploadQcProjectMappingBatchInfoRepository
							.updateUqpmbiUploadFinalLevelWhenAllInBatchIsRejected(qcProjectMappingId, batchNumber);
				} else if (!qpm.getProject().getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)
						&& batchInfo.getTotalUploaded().equals(batchInfo.getTotalRejected())) {
					qpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_REJECTED);
					uploadQcProjectMappingBatchInfoRepository
							.updateUqpmbiUploadFinalLevelWhenAllInBatchIsRejected(qcProjectMappingId, batchNumber);
				} else {
					qpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_MIXED);
				}
				return ResponseEntity.ok().body(true);
			} else {
				return ResponseEntity.ok().body(false);
			}
		} else {
			return ResponseEntity.ok().body(false);
		}
	}

	/**
	 * {@code PUT  /aidas-uploads/:id} : Reject an existing upload.
	 *
	 * @param id the id of the upload to save.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated upload, or with status {@code 400 (Bad Request)} if the
	 *         upload is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the upload couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/aidas-uploads/reject/new/{id}/{qcProjectMappingId}")
	public ResponseEntity<Boolean> rejectAidasUploadNew(@PathVariable(value = "id", required = false) final Long id,
			@Valid @RequestBody List<UploadRejectReason> uploadRejectReasons,
			@PathVariable(value = "qcProjectMappingId", required = false) final Long qcProjectMappingId)
			throws URISyntaxException {
		Upload upload = uploadRepository.getById(id);

		HashMap<Long, Integer> oldQcStatus = new HashMap<>();
		oldQcStatus.put(upload.getId(), upload.getQcStatus());
		QcProjectMapping qpm = qcProjectMappingRepository.getById(qcProjectMappingId);
		UploadQcProjectMappingBatchInfo uqpmbi = uploadQcProjectMappingBatchInfoRepository
				.getUploadIdByqcProjectMappingAndBatchNumber(qcProjectMappingId, qpm.getCurrentQcBatchNo(),
						upload.getId());
		uqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
		upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
		upload.setQcEndDate(Instant.now());
		upload.setCurrentQcLevel(upload.getCurrentQcLevel() + 1);
		upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
		for (UploadRejectReason uploadRejectReason : uploadRejectReasons) {
			UploadRejectReasonMapping uploadRejectReasonMapping = new UploadRejectReasonMapping();
			if (uploadRejectReason.getId() != null) {
				uploadRejectReason = uploadRejectReasonRepository.getById(uploadRejectReason.getId());
				uploadRejectReasonMapping = uploadRejectReasonMappingRepository
						.getUploadRejectReasonMapping(upload.getId(), uploadRejectReason.getId());
				if (uploadRejectReasonMapping == null) {
					uploadRejectReasonMapping = new UploadRejectReasonMapping();
					uploadRejectReason = uploadRejectReasonRepository.getById(uploadRejectReason.getId());
					uploadRejectReasonMapping.setUpload(upload);
					uploadRejectReasonMapping.setUploadRejectReason(uploadRejectReason);
					upload.getUploadRejectMappings().add(uploadRejectReasonMapping);
				}
			} else {
				UploadRejectReason uploadRejectReason1 = new UploadRejectReason();
				uploadRejectReason1.setReason(uploadRejectReason.getReason());
				uploadRejectReason1 = uploadRejectReasonRepository.save(uploadRejectReason1);
				uploadRejectReasonMapping.setUpload(upload);
				uploadRejectReasonMapping.setUploadRejectReason(uploadRejectReason1);
				upload.getUploadRejectMappings().add(uploadRejectReasonMapping);
			}
		}
		if (qpm != null) {
			upload.setQcDoneBy(qpm);
			upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
			upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
		}
		if (uqpmbi != null) {
			uqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
			uqpmbi.setQcSeenStatus(AidasConstants.AIDAS_QC_VIWED);
		}

		upload.setQcDoneBy(qpm);
		upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
		upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
		uqpmbi.setQcSeenStatus(AidasConstants.AIDAS_QC_VIWED);
		uqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
		uploadRepository.save(upload);
		uploadQcProjectMappingBatchInfoRepository.save(uqpmbi);
		return ResponseEntity.ok().body(true);
	}

	/**
	 * {@code GET  /aidas-uploads} : get all the aidasUploads.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of aidasUploads in body.
	 */
	@GetMapping("/aidas-uploads")
	public ResponseEntity<List<Upload>> getAllAidasUploads(Pageable pageable) {
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		log.debug("REST request to get a page of AidasUploads");
		Page<Upload> page = uploadRepository.findAll(pageable);
		Authority authority = user.getAuthority();
		if (authority.getName().equals(AidasConstants.ADMIN)) {
			page = uploadRepository.findAll(pageable);
		}
		if (authority.getName().equals(AidasConstants.ORG_ADMIN)) {
			page = uploadRepository.findAidasUploadByAidasOrganisation(user.getOrganisation().getId(), pageable);
		}
		if (authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
			page = uploadRepository.findAidasUploadByAidasCustomer(user.getCustomer().getId(), pageable);
		}
		if (authority.getName().equals(AidasConstants.VENDOR_ADMIN)) {
			page = uploadRepository.findAidasUploadByAidasVendor(user.getVendor().getId(), pageable);
		}
		if (authority.getName().equals(AidasConstants.VENDOR_USER)) {
			page = uploadRepository.findAllByUser(user.getId(), pageable);
		}

		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET /aidas-uploads/{id}/{type}/{status}} : get all the aidasUploads.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of aidasUploads in body.
	 */
	@GetMapping("/aidas-uploads/{projectId}/{type}/{status}")
	public ResponseEntity<List<Upload>> getAllAidasUploads(Pageable pageable, @PathVariable Long projectId,
			@PathVariable String type, @PathVariable String status) {
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		log.debug("REST request to get a page of AidasUploads");
		Long userId = user.getId();
		try {
			Page<Upload> page = null;// uploadRepository.findAll(pageable);
			Authority authority = user.getAuthority();
			/*
			 * if (authority.getName().equals(AidasConstants.ADMIN)) { page =
			 * uploadRepository.findAll(pageable); } if
			 * (authority.getName().equals(AidasConstants.ORG_ADMIN)) { page =
			 * uploadRepository.findAidasUploadByAidasOrganisation(user.getOrganisation().
			 * getId(), pageable); } if
			 * (authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)) { page =
			 * uploadRepository.findAidasUploadByAidasCustomer(user.getCustomer().getId(),
			 * pageable); } if (authority.getName().equals(AidasConstants.VENDOR_ADMIN)) {
			 * page =
			 * uploadRepository.findAidasUploadByAidasVendor(user.getVendor().getId(),
			 * pageable); }
			 */
			// if (authority.getName().equals(AidasConstants.VENDOR_USER)) {
			if (projectId != null && type != null && status != null) {
				if (projectId != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("a")) {
					page = uploadRepository.findAllByUserAndObject(userId, projectId,
							AidasConstants.AIDAS_UPLOAD_APPROVED, pageable);
				}
				if (projectId != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("r")) {
					page = uploadRepository.findAllByUserAndObject(userId, projectId,
							AidasConstants.AIDAS_UPLOAD_REJECTED, pageable);
				}
				if (projectId != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("p")) {
					page = uploadRepository.findAllByUserAndObject(userId, projectId,
							AidasConstants.AIDAS_UPLOAD_PENDING, pageable);
				}
				if (projectId != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("all")) {
					page = uploadRepository.findAllByUserAndObject(userId, projectId, pageable);
				}
				if (projectId != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("a")) {
					page = uploadRepository.findAllByUserAndProject(userId, projectId,
							AidasConstants.AIDAS_UPLOAD_APPROVED, pageable);
				}
				if (projectId != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("r")) {
					page = uploadRepository.findAllByUserAndProject(userId, projectId,
							AidasConstants.AIDAS_UPLOAD_REJECTED, pageable);
				}
				if (projectId != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("p")) {
					page = uploadRepository.findAllByUserAndProject(userId, projectId,
							AidasConstants.AIDAS_UPLOAD_PENDING, pageable);
				}
				if (projectId != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("all")) {
					page = uploadRepository.findAllByUserAndProject(userId, projectId, pageable);
				}
			}
			if (projectId == null && type == null && status != null) {
				if (status.equalsIgnoreCase("a")) {
					page = uploadRepository.findAllByUser(userId, AidasConstants.AIDAS_UPLOAD_APPROVED, pageable);
				}
				if (status.equalsIgnoreCase("r")) {
					page = uploadRepository.findAllByUser(userId, AidasConstants.AIDAS_UPLOAD_REJECTED, pageable);
				}
				if (status.equalsIgnoreCase("p")) {
					page = uploadRepository.findAllByUser(userId, AidasConstants.AIDAS_UPLOAD_PENDING, pageable);
				}
				if (status.equalsIgnoreCase("all")) {
					page = uploadRepository.findAllByUser(userId, pageable);
				}
			}
			// }
			String accessKey = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
					.findByProjectPropertyByPropertyName(projectId, "accessKey").getValue().getBytes(),
					AidasConstants.KEY, AidasConstants.IV_STR);
			String accessSecret = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
					.findByProjectPropertyByPropertyName(projectId, "accessSecret").getValue().getBytes(),
					AidasConstants.KEY, AidasConstants.IV_STR);
			String bucket = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
					.findByProjectPropertyByPropertyName(projectId, "bucketName").getValue().getBytes(),
					AidasConstants.KEY, AidasConstants.IV_STR);
			String region = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
					.findByProjectPropertyByPropertyName(projectId, "region").getValue().getBytes(), AidasConstants.KEY,
					AidasConstants.IV_STR);
			S3Presigner presigner = S3Presigner.builder()
					.credentialsProvider(
							StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, accessSecret)))
					.region(Region.of(region)).build();
			for (Upload u : page.getContent()) {
				GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(u.getUploadUrl())
						.build();
				GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
						.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
				PresignedGetObjectRequest presignedGetObjectRequest = presigner
						.presignGetObject(getObjectPresignRequest);
				u.setPresignedUploadUrl(presignedGetObjectRequest.url().toString());
			}
			HttpHeaders headers = PaginationUtil
					.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
			return ResponseEntity.ok().headers(headers).body(page.getContent());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
		}
	}

	/**
	 * {@code GET /aidas-uploads/{id}/{type}/{status}} : get all the aidasUploads.
	 *
	 * @param projectId the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of aidasUploads in body.
	 */
	@GetMapping("/aidas-uploads/metadata/{projectId}/{objectId}")
	public ResponseEntity<List<UploadsMetadataDTO>> getAllAidasUploadsForMetadata(
			@PathVariable(required = true, name = "projectId") Long projectId,
			@PathVariable(name = "objectId", required = false) Long objectId) {
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		List<UploadsMetadataDTO> uploadsMetadataDTOList = new ArrayList<>();
		if (user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)) {
			UserVendorMapping uvm = userVendorMappingRepository.findByUserAndVendor(user.getVendor().getId(),
					user.getId());
			List<UploadMetadataDTO> pps = uploadMetaDataRepository.getAllUploadMetadataProjectProperties(uvm.getId(),
					objectId);
			List<UploadMetadataDTO> ops = uploadMetaDataRepository.getAllUploadMetadataObjectProperties(uvm.getId(),
					objectId);
			Map<Long, List<ProjectPropertyDTO>> uploadMetaDataDTOp = new HashMap<>();
			Map<Long, List<ObjectPropertyDTO>> uploadMetaDataDTOo = new HashMap<>();
			Map<Long, UploadDTO> uploadDtos = new HashMap<>();
			for (UploadMetadataDTO umdt : pps) {
				if (uploadMetaDataDTOp.get(umdt.getUploadId()) == null) {
					uploadMetaDataDTOp.put(umdt.getUploadId(), new ArrayList<>());
				}
				if (uploadMetaDataDTOo.get(umdt.getUploadId()) == null) {
					uploadMetaDataDTOo.put(umdt.getUploadId(), new ArrayList<>());
				}
				if (uploadDtos.get(umdt.getUploadId()) == null) {
					UploadDTO uploadDTO = new UploadDTO();
					uploadDTO.setUploadId(umdt.getUploadId());
					uploadDTO.setName(umdt.getObjectName());
					uploadDTO.setObjectKey(umdt.getObjectKey());
					uploadDtos.put(umdt.getUploadId(), uploadDTO);
				}
				ProjectPropertyDTO ppdt = new ProjectPropertyDTO(umdt.getProjectPropertyId(),
						umdt.getProjectPropertyName(), umdt.getOptional(), umdt.getValue());
				uploadMetaDataDTOp.get(umdt.getUploadId()).add(ppdt);
			}
			for (UploadMetadataDTO umdt : ops) {
				ObjectPropertyDTO opdt = new ObjectPropertyDTO(umdt.getProjectPropertyId(),
						umdt.getProjectPropertyName(), umdt.getOptional(), umdt.getValue());
				uploadMetaDataDTOo.get(umdt.getUploadId()).add(opdt);
			}
			try {
				String accessKey = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(projectId, "accessKey").getValue().getBytes(),
						AidasConstants.KEY, AidasConstants.IV_STR);
				String accessSecret = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(projectId, "accessSecret").getValue().getBytes(),
						AidasConstants.KEY, AidasConstants.IV_STR);
				String bucket = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(projectId, "bucketName").getValue().getBytes(),
						AidasConstants.KEY, AidasConstants.IV_STR);
				String region = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
						.findByProjectPropertyByPropertyName(projectId, "region").getValue().getBytes(),
						AidasConstants.KEY, AidasConstants.IV_STR);
				S3Presigner presigner = S3Presigner.builder()
						.credentialsProvider(
								StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, accessSecret)))
						.region(Region.of(region)).build();
				for (Map.Entry<Long, List<ProjectPropertyDTO>> entry : uploadMetaDataDTOp.entrySet()) {
					UploadsMetadataDTO umdts = new UploadsMetadataDTO();
					umdts.setUploadDTO(uploadDtos.get(entry.getKey()));
					GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket)
							.key(uploadDtos.get(entry.getKey()).getObjectKey()).build();
					GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
							.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
					PresignedGetObjectRequest presignedGetObjectRequest = presigner
							.presignGetObject(getObjectPresignRequest);
					umdts.getUploadDTO().setObjectKey(presignedGetObjectRequest.url().toString());
					umdts.setProjectProperties(entry.getValue());
					umdts.setObjectProperties(uploadMetaDataDTOo.get(entry.getKey()));
					uploadsMetadataDTOList.add(umdts);
				}
				if (uploadsMetadataDTOList.size() == 0) {
					List<UploadMetadataDTO> uploads = uploadMetaDataRepository
							.findAllByUserAndProjectAllForMetadataUploadWiseForNew(user.getId(), objectId);
					for (UploadMetadataDTO obj : uploads) {
						UploadsMetadataDTO uploadsMetadataDTO = new UploadsMetadataDTO();
						UploadDTO uploadDTO = new UploadDTO();
						uploadDTO.setUploadId(obj.getUploadId());
						uploadDTO.setName(obj.getObjectName());
						GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket)
								.key(obj.getObjectKey()).build();
						GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
								.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
						PresignedGetObjectRequest presignedGetObjectRequest = presigner
								.presignGetObject(getObjectPresignRequest);
						uploadDTO.setObjectKey(presignedGetObjectRequest.url().toString());
						uploadsMetadataDTO.setUploadDTO(uploadDTO);
						uploadsMetadataDTO.setProjectProperties(new ArrayList<>());
						uploadsMetadataDTO.setObjectProperties(new ArrayList<>());
						uploadsMetadataDTOList.add(uploadsMetadataDTO);

					}
				}
			} catch (Exception e) {

			}
		}
		return ResponseEntity.ok().body(uploadsMetadataDTOList);
	}

	/**
	 * {@code GET  /aidas-uploads/next} : get the "id" upload.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the upload, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/aidas-uploads/next/{projectId}")
	public ResponseEntity<List<QcProjectMapping>> getNextAidasUpload(@PathVariable Long projectId) {
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		log.debug("REST request to get next AidasUpload : {}");
		Project project = projectRepository.getById(projectId);
		List<QcProjectMapping> qpms = new ArrayList();// qcProjectMappingRepository.getQcProjectMappingForAdminQc(projectId,
														// project.getCustomer().getId(), user.getId());
		if (user.getAuthority().getName().equals(AidasConstants.ADMIN_QC_USER)) {
			qpms = qcProjectMappingRepository.getQcProjectMappingForAdminQc(projectId, user.getId());
		}
		if (user.getAuthority().getName().equals(AidasConstants.ORG_QC_USER)) {
			qpms = qcProjectMappingRepository.getQcProjectMappingForOrganisationQc(projectId,
					user.getOrganisation().getId(), user.getId());
		}
		if (user.getAuthority().getName().equals(AidasConstants.CUSTOMER_QC_USER)) {
			qpms = qcProjectMappingRepository.getQcProjectMappingForCustomerQc(projectId, user.getCustomer().getId(),
					user.getId());
		}
		if (user.getAuthority().getName().equals(AidasConstants.VENDOR_QC_USER)) {
			qpms = qcProjectMappingRepository.getQcProjectMappingForVendorQc(projectId, user.getVendor().getId(),
					user.getId());
		}

		return ResponseEntity.ok().body(qpms);
	}

	/**
	 * {@code GET  /aidas-uploads/next} : get the "id" upload.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the upload, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/aidas-uploads/upload-meta-data-details/{uploadId}")
	public ResponseEntity<List<UploadMetadataDTO>> getUploadDetails(
			@PathVariable(name = "uploadId", required = true) Long uploadId) {
		log.debug("REST request to get next AidasUpload : {}");
		List<UploadMetadataDTO> uploadMetaData = uploadMetaDataRepository
				.getAllUploadMetaDataForProjectPropertiesForUpload(uploadId);
		return ResponseEntity.ok().body(uploadMetaData);
	}

	@GetMapping("/aidas-uploads/next-batch/new/{projectId}/{qcProjectMappingId}/{qcLevel}")
	public ResponseEntity<Map<String, List<UploadDTOForQC>>> getNextAidasUploadDTOForQCBatch(
			@PathVariable(name = "projectId", required = true) Long projectId,
			@PathVariable(value = "qcProjectMappingId", required = true) Long qcProjectMappingId,
			@PathVariable(value = "qcLevel", required = true) Integer qcLevel) {
		return ResponseEntity.ok().body(getUploadDTOOfProjectForQc(projectId, qcProjectMappingId, qcLevel));
	}

	private Map<String, List<UploadDTOForQC>> getUploadDTOOfProjectForQc(Long projectId, Long qcProjectMappingId,
			Integer qcLevel) {
		Map<String, List<UploadDTOForQC>> uploads = new HashMap<>();
		ProjectQcLevelConfigurations pqlc = projectQcLevelConfigurationsRepository.findByProejctIdAndQcLevel(projectId,
				qcLevel);
		QcProjectMapping qpm = qcProjectMappingRepository.getById(qcProjectMappingId);
		try {
			uploads = getUploadDTOQcs(projectId, qcProjectMappingId, qcLevel, pqlc, qpm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploads;
	}

	private Map<String, List<UploadDTOForQC>> getUploadDTOQcs(Long projectId, Long qcProjectMappingId, Integer qcLevel,
			ProjectQcLevelConfigurations pqlc, QcProjectMapping qpm) throws Exception {
		List<UploadDTOForQC> uploadsDTOForQc = new ArrayList<>();
		qpm = qcProjectMappingRepository.getById(qcProjectMappingId);
		QcProjectMappingBatchMapping qpmbm = qcProjectMappingBatchMappingRepository
				.findByqcProjectMappingIdAndBatchNumber(qpm.getId(), qpm.getCurrentQcBatchNo());
		Project project = projectRepository.getById(projectId);
		List<Long> uvmomIds = new LinkedList<Long>();
		Map<String, List<UploadDTOForQC>> resultMap1 = new HashMap<>();
		Integer multFactor = 1;
		List<QbmDto> notCompletedBatches = qcProjectMappingBatchMappingRepository
				.getQcNotCompletedBatches(project.getId(), qpm.getQcLevel(), qpm.getId());

		String accessKey = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
				.findByProjectPropertyByPropertyName(projectId, "accessKey").getValue().getBytes(), AidasConstants.KEY,
				AidasConstants.IV_STR);
		String accessSecret = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
				.findByProjectPropertyByPropertyName(projectId, "accessSecret").getValue().getBytes(),
				AidasConstants.KEY, AidasConstants.IV_STR);
		String bucket = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
				.findByProjectPropertyByPropertyName(projectId, "bucketName").getValue().getBytes(), AidasConstants.KEY,
				AidasConstants.IV_STR);
		String region = AESCBCPKCS5Padding.decrypt(projectPropertyRepository
				.findByProjectPropertyByPropertyName(projectId, "region").getValue().getBytes(), AidasConstants.KEY,
				AidasConstants.IV_STR);
		S3Presigner presigner = S3Presigner.builder()
				.credentialsProvider(
						StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, accessSecret)))
				.region(Region.of(region)).build();

		if (notCompletedBatches != null && notCompletedBatches.size() > 0) {
			uploadsDTOForQc = uploadRepository.getUploadDTOForQCPendingInBatch(notCompletedBatches.get(0).getQbmId(),
					qpmbm.getCurrentPageNumber(), pqlc.getQcLevelBatchSize() * project.getNumberOfUploadsRequired());

			List<UploadSummaryForQCFinalize> objCompletionStatus = uploadQcProjectMappingBatchInfoRepository
					.getUvmomObjectIdsOfBatch(qpm.getId(), qpmbm.getId());
			Map<Long, UploadSummaryForQCFinalize> map = new HashMap<>();
			for (UploadSummaryForQCFinalize objStat : objCompletionStatus) {
				map.put(objStat.getUvmomId(), objStat);
			}

			for (UploadDTOForQC u : uploadsDTOForQc) {
				GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(u.getUploadUrl())
						.build();
				GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
						.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
				PresignedGetObjectRequest presignedGetObjectRequest = presigner
						.presignGetObject(getObjectPresignRequest);
				u.setUploadUrl(presignedGetObjectRequest.url().toString());
				if (u.getConsentFormUrl() != null && !u.getConsentFormUrl().isEmpty()) {
					getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(u.getConsentFormUrl()).build();
					getObjectPresignRequest = GetObjectPresignRequest.builder()
							.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
					presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
					u.setConsentFormUrl(presignedGetObjectRequest.url().toString());
				}
				int objStatus = 2;
				if (map.get(u.getUserVendorMappingObjectMappingId()) != null) {
					Integer pending = map.get(u.getUserVendorMappingObjectMappingId()).getTotalPending();
					if (pending == 0) {
						objStatus = 1;
					}
				}
				if (resultMap1.get(
						u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-" + objStatus) == null) {
					resultMap1.put(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-" + objStatus,
							new ArrayList<>());
				}
				resultMap1.get(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-" + objStatus)
						.add(u);
			}
			presigner.close();
		} else {
			List<Integer[]> mixedBatches = new ArrayList<>();
			List<Long> approvedUvmomIds = new ArrayList<>();
			List<Long> tobeShownUvmoms = new ArrayList<>();
			List<Long> tobeShownUploads = new ArrayList<>();
			if (qpm.getQcLevel() == 2) {
				mixedBatches = qcProjectMappingBatchMappingRepository.getQcMixedBatches(project.getId(),
						qpm.getQcLevel() - 1);
			} else if (qpm.getQcLevel() == 3) {
				mixedBatches = qcProjectMappingBatchMappingRepository.getQcMixedBatchesForLevelThree(project.getId(),
						qpm.getQcLevel() - 1);
			} else if (qpm.getQcLevel() > 3) {
				mixedBatches = qcProjectMappingBatchMappingRepository
						.getQcMixedBatchesForLevelGreaterThanThree(project.getId(), qpm.getQcLevel() - 1);
			}
			Integer approvedUploadsCount = 1;
			Float numOfUploads = 1f;
			int numOfUploadsToBeShown = 1;
			Double ceiledNumberOfuploads = 1d;
			QcProjectMappingBatchMapping qpmbm1 = null;
			if (mixedBatches != null && mixedBatches.size() > 0) {
				approvedUploadsCount = mixedBatches.get(0)[1];
				List<UploadDTOForQC> tempUploadIds = uploadRepository.getUploadDTOForQCInBatch(mixedBatches.get(0)[0]);
				if (project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
					approvedUvmomIds = qcProjectMappingBatchMappingRepository.getQcApprovedUvmomIds(project.getId(),
							qpm.getQcLevel() - 1, mixedBatches.get(0)[0]);
					if (approvedUvmomIds.size() == 0) {
						for (Integer[] i : mixedBatches) {
							approvedUvmomIds = qcProjectMappingBatchMappingRepository
									.getQcApprovedUvmomIds(project.getId(), qpm.getQcLevel() - 1, i[0]);
							tempUploadIds = uploadRepository.getUploadDTOForQCInBatch(i[0]);
							if (approvedUvmomIds.size() > 0)
								break;
						}
					}
					numOfUploads = (pqlc.getQcLevelAcceptancePercentage().floatValue() / 100f)
							* approvedUvmomIds.size();
					ceiledNumberOfuploads = Math.ceil(numOfUploads);
					numOfUploadsToBeShown = ceiledNumberOfuploads.intValue();
					if (numOfUploadsToBeShown == 0) {
						numOfUploadsToBeShown = 1;
					}
					if (approvedUvmomIds != null && approvedUvmomIds.size() > 0) {
						if (approvedUvmomIds.size() <= numOfUploadsToBeShown)
							tobeShownUvmoms = approvedUvmomIds;
						else
							tobeShownUvmoms = approvedUvmomIds.subList(0, numOfUploadsToBeShown);
					}
				} else {
					List<Long> approvedUploadIds = qcProjectMappingBatchMappingRepository
							.getApprovedUploadIds(mixedBatches.get(0)[0]);
					numOfUploads = (pqlc.getQcLevelAcceptancePercentage().floatValue() / 100f)
							* approvedUploadIds.size();
					ceiledNumberOfuploads = Math.ceil(numOfUploads);
					numOfUploadsToBeShown = ceiledNumberOfuploads.intValue();
					if (numOfUploadsToBeShown == 0) {
						if (approvedUploadIds != null && approvedUploadIds.size() > 0)
							tobeShownUploads = approvedUploadIds.subList(0, 1);
					} else {
						if (approvedUploadIds != null && approvedUploadIds.size() > 0)
							if (approvedUploadIds.size() <= numOfUploadsToBeShown)
								tobeShownUploads = approvedUploadIds;
							else
								tobeShownUploads = approvedUploadIds.subList(0, numOfUploadsToBeShown);
					}

				}
				if (qpmbm == null) {
					qpmbm = new QcProjectMappingBatchMapping();
					qpmbm.setqcProjectMapping(qpm);
					qpmbm.setCurrentPageNumber(0);
					qcProjectMappingBatchMappingRepository.save(qpmbm);
					if (qpm.getQcLevel() == 2) {
						qpmbm1 = qcProjectMappingBatchMappingRepository.getById(mixedBatches.get(0)[0].longValue());
						qpmbm1.setNextLevelBatchNumber(qpmbm.getId());
						qpmbm.setPreviousLevelBatchNumber(qpmbm1.getId());
						qcProjectMappingBatchMappingRepository.save(qpmbm1);
					} else if (qpm.getQcLevel() > 2) {
						qpmbm1 = qcProjectMappingBatchMappingRepository.getById(mixedBatches.get(0)[0].longValue());
						qpmbm.setPreviousLevelBatchNumber(qpmbm1.getId());
						qpmbm1.setNextLevelBatchNumber(qpmbm.getId());
						qcProjectMappingBatchMappingRepository.save(qpmbm1);
					}
					qpmbm.setBatchNo(qpmbm.getId().intValue());
					qpm.setCurrentQcBatchNo(qpmbm.getId().intValue());
					qcProjectMappingRepository.save(qpm);
					qcProjectMappingBatchMappingRepository.save(qpmbm);

				}
				List<Long> showToQcUps = new ArrayList<>();
				List<Long> notShowToQcUps = new ArrayList<>();
				List<Long> rejectedUps = new ArrayList<>();

				for (UploadDTOForQC u : tempUploadIds) {
					if (u.getQcStatus().equals(AidasConstants.AIDAS_UPLOAD_QC_REJECTED)) {
						rejectedUps.add(u.getUploadId());
					} else {
						GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket)
								.key(u.getUploadUrl()).build();
						GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
								.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
						PresignedGetObjectRequest presignedGetObjectRequest = presigner
								.presignGetObject(getObjectPresignRequest);
						u.setUploadUrl(presignedGetObjectRequest.url().toString());
						if (u.getConsentFormUrl() != null && !u.getConsentFormUrl().isEmpty()) {
							getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(u.getConsentFormUrl())
									.build();
							getObjectPresignRequest = GetObjectPresignRequest.builder()
									.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest)
									.build();
							presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
							u.setConsentFormUrl(presignedGetObjectRequest.url().toString());
						}

						u.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
						u.setBatchNumber(qpmbm.getId());
						if (project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
							if (tobeShownUvmoms.contains(u.getUserVendorMappingObjectMappingId())) {
								if (resultMap1.get(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName()
										+ "-" + qpmbm.getBatchCompletionStatus()) == null) {
									resultMap1.put(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName()
											+ "-" + qpmbm.getBatchCompletionStatus(), new ArrayList<>());
								}
								showToQcUps.add(u.getUploadId());
								resultMap1.get(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-"
										+ qpmbm.getBatchCompletionStatus()).add(u);
							} else {
								notShowToQcUps.add(u.getUploadId());
							}
						} else {
							if (tobeShownUploads.contains(u.getUploadId())) {
								if (resultMap1.get(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName()
										+ "-" + qpmbm.getBatchCompletionStatus()) == null) {
									resultMap1.put(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName()
											+ "-" + qpmbm.getBatchCompletionStatus(), new ArrayList<>());
								}
								showToQcUps.add(u.getUploadId());
								resultMap1.get(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-"
										+ qpmbm.getBatchCompletionStatus()).add(u);
							} else {
								notShowToQcUps.add(u.getUploadId());
							}
						}
					}
				}
				uploadQcProjectMappingBatchInfoRepository.insertUploadqpmBatchInfo(rejectedUps, qpmbm.getId(),
						qpm.getId(), AidasConstants.AIDAS_UPLOAD_NO_SHOW_TO_QC,
						AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
				uploadQcProjectMappingBatchInfoRepository.insertUploadqpmBatchInfo(showToQcUps, qpmbm.getId(),
						qpm.getId(), AidasConstants.AIDAS_UPLOAD_SHOW_TO_QC, AidasConstants.AIDAS_UPLOAD_QC_PENDING);
				uploadQcProjectMappingBatchInfoRepository.insertUploadqpmBatchInfo(notShowToQcUps, qpmbm.getId(),
						qpm.getId(), AidasConstants.AIDAS_UPLOAD_NO_SHOW_TO_QC, AidasConstants.AIDAS_UPLOAD_QC_PENDING);
				uploadRepository.updateUploadQcStatus(AidasConstants.AIDAS_UPLOAD_SHOW_TO_QC,
						AidasConstants.AIDAS_UPLOAD_PENDING, qpm.getId(), Instant.now(), qpmbm.getId(),
						Stream.of(notShowToQcUps, showToQcUps, rejectedUps).flatMap(Collection::stream)
								.collect(Collectors.toList()));

			} else {
				List<UploadDTOForQC> uploadIds = new ArrayList<>();
				if (project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
					List<java.lang.Object[]> tempUvmomIds = uploadRepository.findAllUvmomsQcNotStartedGrouped(projectId,
							qpm.getQcLevel());

					Map<Long, List<Long>> map = new LinkedHashMap<>();
					for (java.lang.Object[] tempUvmomId : tempUvmomIds) {
						if (map.get(Long.parseLong(tempUvmomId[0].toString())) != null) {
							map.get(Long.parseLong(tempUvmomId[0].toString()))
									.add(Long.parseLong(tempUvmomId[1].toString()));
						} else {
							List<Long> uvmom = new ArrayList<>();
							uvmom.add(Long.parseLong(tempUvmomId[1].toString()));
							map.put(Long.parseLong(tempUvmomId[0].toString()), uvmom);
						}
					}
					int i = 0;
					for (Map.Entry<Long, List<Long>> entry : map.entrySet()) {
						if (i == 0) {
							List<Long> tmpList = entry.getValue();
							if (tmpList.size() > pqlc.getQcLevelBatchSize()) {
								uvmomIds = tmpList.subList(0, pqlc.getQcLevelBatchSize());
							} else {
								uvmomIds = tmpList;
							}
							break;
						}
						i++;
					}
					uploadIds = uploadRepository.findAllUploadIdsGroupedNew(uvmomIds);
					approvedUploadsCount = uvmomIds.size();
					numOfUploads = (pqlc.getQcLevelAcceptancePercentage().floatValue() / 100f) * approvedUploadsCount;
					ceiledNumberOfuploads = Math.ceil(numOfUploads);
					numOfUploadsToBeShown = ceiledNumberOfuploads.intValue();
					tobeShownUvmoms = uvmomIds.subList(0, numOfUploadsToBeShown);
				} else {
					List<java.lang.Object[]> tempUvmomIds = uploadRepository.findAllUvmomsQcNotStartedGrouped(projectId,
							qpm.getQcLevel());

					Map<Long, List<Long>> map = new LinkedHashMap<>();
					for (java.lang.Object[] tempUvmomId : tempUvmomIds) {
						if (map.get(Long.parseLong(tempUvmomId[0].toString())) != null) {
							map.get(Long.parseLong(tempUvmomId[0].toString()))
									.add(Long.parseLong(tempUvmomId[1].toString()));
						} else {
							List<Long> uvmom = new ArrayList<>();
							uvmom.add(Long.parseLong(tempUvmomId[1].toString()));
							map.put(Long.parseLong(tempUvmomId[0].toString()), uvmom);
						}
					}
					int i = 0;
					for (Map.Entry<Long, List<Long>> entry : map.entrySet()) {
						if (i == 0) {
							List<Long> tmpList = entry.getValue();
							if (tmpList.size() > pqlc.getQcLevelBatchSize()) {
								uvmomIds = tmpList.subList(0, 1);
							} else {
								uvmomIds = tmpList.subList(0, 1);
								;
							}
							break;
						}
						i++;
					}
					uploadIds = uploadRepository.findAllUploadIdsNonGroupedNew(uvmomIds, pqlc.getQcLevelBatchSize());
					approvedUploadsCount = uploadIds.size();
					numOfUploads = (pqlc.getQcLevelAcceptancePercentage().floatValue() / 100f) * approvedUploadsCount;
					ceiledNumberOfuploads = Math.ceil(numOfUploads);
					numOfUploadsToBeShown = ceiledNumberOfuploads.intValue();
					if (numOfUploadsToBeShown > 0) {
						multFactor = approvedUploadsCount / numOfUploadsToBeShown;
					}
				}
				if (multFactor == 0) {
					multFactor = 1;
				}
				if (uvmomIds != null && uvmomIds.size() > 0) {
					if (qpmbm == null) {
						qpmbm = new QcProjectMappingBatchMapping();
						qpmbm.setqcProjectMapping(qpm);
						qpmbm.setCurrentPageNumber(0);
						qcProjectMappingBatchMappingRepository.save(qpmbm);
						if (mixedBatches != null && mixedBatches.size() > 0) {
							if (qpm.getQcLevel() == 2) {
								qpmbm1 = qcProjectMappingBatchMappingRepository
										.getById(mixedBatches.get(0)[0].longValue());
								qpmbm1.setNextLevelBatchNumber(qpmbm.getId());
								qpmbm.setPreviousLevelBatchNumber(qpmbm1.getId());
							} else if (qpm.getQcLevel() > 2) {
								qpmbm1 = qcProjectMappingBatchMappingRepository
										.getById(mixedBatches.get(0)[0].longValue());
								qpmbm.setPreviousLevelBatchNumber(qpmbm1.getId());
								qpmbm1.setNextLevelBatchNumber(qpmbm.getId());
							}
						}
						qpmbm.setBatchNo(qpmbm.getId().intValue());
						qpm.setCurrentQcBatchNo(qpmbm.getId().intValue());
						qcProjectMappingRepository.save(qpm);
					}
				}
				Iterator it = null;
				List<Long> ups = new ArrayList<>();
				for (UploadDTOForQC u : uploadIds) {
					GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(u.getUploadUrl())
							.build();
					GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
							.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
					PresignedGetObjectRequest presignedGetObjectRequest = presigner
							.presignGetObject(getObjectPresignRequest);
					u.setUploadUrl(presignedGetObjectRequest.url().toString());
					if (u.getConsentFormUrl() != null && !u.getConsentFormUrl().isEmpty()) {
						getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(u.getConsentFormUrl()).build();
						getObjectPresignRequest = GetObjectPresignRequest.builder()
								.signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
						presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
						u.setConsentFormUrl(presignedGetObjectRequest.url().toString());
					}
					ups.add(u.getUploadId());
					u.setBatchNumber(qpmbm.getId());
					if (resultMap1.get(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-"
							+ qpmbm.getBatchCompletionStatus()) == null) {
						resultMap1.put(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-"
								+ qpmbm.getBatchCompletionStatus(), new ArrayList<>());
					}
					resultMap1.get(u.getUserVendorMappingObjectMappingId() + "-" + u.getObjectName() + "-"
							+ qpmbm.getBatchCompletionStatus()).add(u);
				}
				if (qpmbm != null) {
					uploadQcProjectMappingBatchInfoRepository.insertUploadqpmBatchInfo(ups, qpmbm.getId(), qpm.getId(),
							AidasConstants.AIDAS_UPLOAD_SHOW_TO_QC, AidasConstants.AIDAS_UPLOAD_QC_PENDING);
					uploadRepository.updateUploadQcStatus(AidasConstants.AIDAS_UPLOAD_SHOW_TO_QC,
							AidasConstants.AIDAS_UPLOAD_PENDING, qpm.getId(), Instant.now(), qpmbm.getId(), ups);
				}
			}
		}
		return resultMap1;
	}

	/**
	 * {@code SEARCH  /_search/aidas-uploads?query=:query} : search for the upload
	 * corresponding to the query.
	 *
	 * @param query    the query of the upload search.
	 * @param pageable the pagination information.
	 * @return the result of the search.
	 */
	@GetMapping("/_search/aidas-uploads")
	public ResponseEntity<List<Upload>> searchAidasUploads(@RequestParam String query, Pageable pageable) {
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		log.debug("REST request to search for a page of AidasUploads for query {}", query);
		Authority authority = user.getAuthority();
		Page<Upload> page = aidasUploadSearchRepository.search(query, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	@Autowired
	TaskExecutor taskExecutor;

	@Autowired
	DownloadUploadS3 downloadUploadS3;

	/**
	 * {@code GET  /download/uploads/} : download objects with the "id" object and
	 * provided status. User "all" for download both.
	 *
	 * @param uploadIds array of upload ids to download.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the object, or with status {@code 404 (Not Found)}.
	 */
	@PostMapping("/download/uploads")
	public void downloadUploadedObjects(@RequestBody List<Long> uploadIds) {
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		downloadUploadS3.setUser(user);
		downloadUploadS3.setUp(uploadIds);
		taskExecutor.execute(downloadUploadS3);
	}
}
