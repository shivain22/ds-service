package com.ainnotate.aidas.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Download;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.ObjectProperty;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.dto.ObjectForJson;
import com.ainnotate.aidas.dto.ProjectForJson;
import com.ainnotate.aidas.dto.UploadForJson;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.DownloadRepository;
import com.ainnotate.aidas.repository.ObjectPropertyRepository;
import com.ainnotate.aidas.repository.ObjectRepository;
import com.ainnotate.aidas.repository.ProjectPropertyRepository;
import com.ainnotate.aidas.repository.ProjectRepository;
import com.ainnotate.aidas.repository.UploadRepository;
import com.ainnotate.aidas.repository.UserVendorMappingObjectMappingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class DownloadUploadJson  implements  Runnable{

    private String resource;
    private String status;
    private List<Long> uploadedFileIds;
    private Project project;
    private Object object;
    private String tempFolder;
    private String zipFile;
    private String zipFileKey;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getZipFileKey() {
        return zipFileKey;
    }

    public void setZipFileKey(String zipFileKey) {
        this.zipFileKey = zipFileKey;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getUploadedFileIds() {
        return uploadedFileIds;
    }

    public void setUploadedFileIds(List<Long> uploadedFileIds) {
        this.uploadedFileIds = uploadedFileIds;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ObjectRepository objectRepository;
    @Autowired
    private UploadRepository uploadRepository;
    @Autowired
    private DownloadRepository downloadRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ProjectPropertyRepository projectPropertyRepository;
    @Autowired
    private ObjectPropertyRepository objectPropertyRepository;
    @Autowired
    private AppPropertyRepository appPropertyRepository;
    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;


    private String globalDownloadAccessKey ="";
    private String globalDownloadAccessSecret ="";
    private String globalDownloadRegion = "";
    private String globalDownloadBucketName = "";
    private String globalDownloadPrefix;

    private String globalUploadAccessKey ="";
    private String globalUploadAccessSecret ="";
    private String globalUploadRegion = "";
    private String globalUploadBucketName = "";
    private String globalUploadPrefix;

    private String fromEmail;
    private String emailToken;

    public DownloadUploadJson(){

    }

    private void setProperties(Set<AppProperty> appProperties){
        for(AppProperty appProperty :appProperties){
        	try {
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_ACCESS_KEY_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadAccessKey = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_ACCESS_SECRET_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadAccessSecret = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_REGION_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadRegion = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_BUCKETNAME_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadBucketName = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_PREFIX_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadPrefix = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);


            if(appProperty.getName().equals(AidasConstants.UPLOAD_ACCESS_KEY_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadAccessKey = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.UPLOAD_ACCESS_SECRET_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadAccessSecret = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.UPLOAD_REGION_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadRegion = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.UPLOAD_BUCKETNAME_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadBucketName = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
            if(appProperty.getName().equals(AidasConstants.UPLOAD_PREFIX_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadPrefix = AESCBCPKCS5Padding.decrypt(appProperty.getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);

            
        	}catch(Exception e) {
        		
        	}
        	
        }
        AppProperty app  = appPropertyRepository.getAppProperty(-1l,"fromEmail");
        fromEmail = app.getValue();
        app = appPropertyRepository.getAppProperty(-1l,"emailToken");
        emailToken = app.getValue();
    }
    private void setGlobalDefaultValues(){
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            setProperties(appPropertyRepository.getAppPropertyOfUser(user.getId()));
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)){
            setProperties(appPropertyRepository.getAppPropertyOfOrganisation(user.getOrganisation().getId()));
        }
        if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            setProperties(appPropertyRepository.getAppPropertyOfCustomer(user.getCustomer().getId()));
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            setProperties(appPropertyRepository.getAppPropertyOfVendor(user.getVendor().getId()));
        }
    }
    public void setUp(Project project, String status){
        this.resource = "project";
        this.status = status;
        this.project = project;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+"aidas_"+ user.getId()+"_"+ project.getId()+"_"+ project.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"aidas_"+ user.getId()+"_"+ project.getId()+"_"+ project.getName()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "aidas_"+ user.getId()+"_"+ project.getId()+"_"+ project.getName()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }
        setGlobalDefaultValues();
    }

    public void setUp(Object object, String status){
        this.resource = "object";
        this.status = status;
        this.object = object;
        this.project = this.object.getProject();
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+"aidas_"+ user.getId()+"_"+ object.getId()+"_"+ object.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"aidas_"+ user.getId()+"_"+ object.getId()+"_"+ object.getName()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "aidas_"+ user.getId()+"_"+ object.getId()+"_"+ object.getName()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }
        setGlobalDefaultValues();
    }

    public void setUp(List<Long> uploadedFileIds){
        this.resource = "uploads";
        this.uploadedFileIds= uploadedFileIds;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+"aidas_"+ user.getId()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"aidas_"+ user.getId()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "aidas_"+ user.getId()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }
        setGlobalDefaultValues();
    }

    @Override
    public void run() {
        List<Upload> uploads=null;

        try {
            if (this.resource.equals("project")) {
                if (this.status.equals("approved")) {
                	if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
                		uploads = this.uploadRepository.getAidasUploadsByGroupedProjectApproved(this.project.getId());
                	}else {
                		uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId(), AidasConstants.AIDAS_UPLOAD_APPROVED);	
                	}
                } else if (this.status.equals("rejected")) {
                	if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
                		uploads = this.uploadRepository.getAidasUploadsByGroupedProjectRejected(this.project.getId());
                	}else {
                		uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId(),AidasConstants.AIDAS_UPLOAD_REJECTED);
                	}
                }else if (this.status.equals("pending")) {
                    uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId(),AidasConstants.AIDAS_UPLOAD_PENDING);
                }else if (this.status.equals("all")) {
                    uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId());
                }
            } else if (this.resource.equals("object")) {
                if (this.status.equals("approved")) {
                	if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
                		uploads = this.uploadRepository.getAidasUploadsByGroupedProjectApprovedObject(this.object.getId());
                	}else {
                		uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId(),AidasConstants.AIDAS_UPLOAD_APPROVED);
                	}
                } else if (this.status.equals("rejected")) {
                	if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
                		uploads = this.uploadRepository.getAidasUploadsByGroupedProjectRejectedObject(this.object.getId());
                	}else {
                		uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId(),AidasConstants.AIDAS_UPLOAD_REJECTED);
                	}
                } else if(this.status.equals("pending")) {
                    uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId(),AidasConstants.AIDAS_UPLOAD_PENDING);
                } else if(this.status.equals("all")) {
                    uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId());
                }
            } else if (this.resource.equals("uploads")) {
                uploads = this.uploadRepository.findAllById(uploadedFileIds);
            }
            if (uploads != null) {
            	
            	Long tmpObjId = -2l;
            	
            	ObjectForJson objectForJson=null;
                try {
                    System.out.println("Starting to download all uploads.......");
                    ProjectForJson projectForJson = new ProjectForJson();
                    projectForJson.setId(project.getId());
                    projectForJson.setName(project.getName());
                    projectForJson.setConsentFormStatus(project.getConsentFormStatus());
                    projectForJson.setGroupingProject(project.getAutoCreateObjects());
                    Map<String, String> uploadLocProps = getObjectProperties(project.getId());
                    for (Upload au : uploads) {
                    	S3Presigner presigner = S3Presigner.builder().credentialsProvider(StaticCredentialsProvider
                				.create(AwsBasicCredentials.create(uploadLocProps.get("accessKey"),uploadLocProps.get("accessSecret")))).region(Region.of(uploadLocProps.get("region"))).build();
                    	UploadForJson uploadForJson= new UploadForJson();
                    	if(!tmpObjId.equals(au.getUserVendorMappingObjectMapping().getObject().getId())) {
                    		if(!tmpObjId.equals(-2l)) {
                    			projectForJson.getObjects().add(objectForJson);
                    		}
                    		objectForJson = new ObjectForJson();
                    		objectForJson.setId(au.getUserVendorMappingObjectMapping().getObject().getId());
                    		objectForJson.setName(au.getUserVendorMappingObjectMapping().getObject().getName());
                    		if(au.getUserVendorMappingObjectMapping().getConsentFormUrl()!=null) {
                    			GetObjectRequest getObjectRequest =GetObjectRequest.builder().bucket(uploadLocProps.get("bucketName")).key(au.getUserVendorMappingObjectMapping().getConsentFormUrl()).build();
                			   	GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
                				PresignedGetObjectRequest presignedGetObjectRequest =presigner.presignGetObject(getObjectPresignRequest);
                				 
                    			objectForJson.setConsentFormUrl(presignedGetObjectRequest.url().toString());
                    		}
                    		tmpObjId = au.getUserVendorMappingObjectMapping().getObject().getId();
                    	}
                         try {
                            GetObjectRequest getObjectRequest =GetObjectRequest.builder().bucket(uploadLocProps.get("bucketName")).key(au.getUploadUrl()).build();
            			   	GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(1440)).getObjectRequest(getObjectRequest).build();
            				PresignedGetObjectRequest presignedGetObjectRequest =presigner.presignGetObject(getObjectPresignRequest);
            				 uploadForJson.setId(au.getId());
                             uploadForJson.setFileName(au.getName());
                             uploadForJson.setS3Url(presignedGetObjectRequest.url());
                             uploadForJson.setMd5(au.getUploadEtag());
                             uploadForJson.setApprovalStatus(au.getApprovalStatus());
                             
                        }catch(Exception e4){
                            System.out.println("The file being tried is "+au.getObjectKey());
                            e4.printStackTrace();
                        }
                        objectForJson.getUploads().add(uploadForJson);
                    }
                    projectForJson.getObjects().add(objectForJson);
                    writeJsonToFile(projectForJson);
                }catch(Exception e2){
                    e2.printStackTrace();
                }
                try {
                    zip();
                }catch(Exception e3){
                    e3.printStackTrace();
                }
                try {
                    
                    URL url=null;
                    try {
                        url = upload(globalDownloadAccessKey, globalDownloadAccessSecret, globalDownloadBucketName, globalDownloadRegion);
                    }catch(Exception e5){
                        e5.printStackTrace();
                    }
                    System.out.println("Finished uploading the zip file.....");
                    Download download = new Download();
                    download.setName(this.zipFileKey);
                    download.setAwsKey(globalDownloadAccessKey);
                    if(url!=null) {
                        download.setUploadUrl(url.toString());
                    }
                    download.setAwsSecret(globalDownloadAccessSecret);
                    download.setBucketName(globalDownloadBucketName);
                    download.setRegion(globalDownloadRegion);
                    download.setObjectKey(this.zipFileKey);
                    download.setDateUploaded(Instant.now());
                    if (object != null) {
                        download.setObject(object);
                    }
                    if (project != null) {
                        download.setProject(project);
                    }
                    if (uploadedFileIds != null) {
                        download.setUploadedObjectIds(uploadedFileIds.toString());
                    }
                    downloadRepository.save(download);
                    sendMail(user.getEmail(), user.getFirstName()+" "+user.getLastName(), url.toString(),this.zipFileKey,"Zip file created and ready for download.");
                }catch(Exception e1){
                    System.out.println("Unable to upload to s3 and add to download table due to "+e1.getMessage());
                    e1.printStackTrace();
                }
            
                
                
            }
        }catch(Exception e){
            e.printStackTrace();
            try {
                sendMail(user.getEmail(), user.getFirstName()+" "+user.getLastName(), " Unable to Download Exception is"+e.getMessage(),this.zipFileKey,"Error creating downloadable zip file");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    
    public void zip( ) throws IOException {
        Path zipFilePath = Paths.get(this.zipFile);
        Path sourceDirPath = Paths.get(this.tempFolder);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath));
             Stream<Path> paths = Files.walk(sourceDirPath)) {
            paths
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                    try {
                        zipOutputStream.putNextEntry(zipEntry);
                        Files.copy(path, zipOutputStream);
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                });
        }
    }

    private URL upload(String accessKey, String accessSecret, String bucketName, String region) throws MessagingException, IOException {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("x-amz-meta-file", this.zipFileKey);
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, accessSecret);
        S3Client s3client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(Region.of(region)).build();
        PutObjectRequest putOb = PutObjectRequest.builder().bucket(bucketName).key(this.zipFileKey).metadata(metadata).build();
        PutObjectResponse response = s3client.putObject(putOb, software.amazon.awssdk.core.sync.RequestBody.fromBytes(getObjectFile(this.zipFile)));

        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        S3Presigner presigner = S3Presigner.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(Region.of(region)).build();// .create();
        GetObjectRequest getObjectRequest =
            GetObjectRequest.builder()
                .bucket(bucketName)
                .key(this.zipFileKey)
                .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        File f = new File(this.tempFolder);
        if(f.exists()){
            if(f.isDirectory()){
                for(File f1:f.listFiles()){
                    f1.delete();
                }
            }
            f.delete();
        }
        return presignedGetObjectRequest.url();
    }
    
    
    private Map<String,String> getObjectProperties(Long projectId) throws Exception{
        Map<String,String> uploadLocProps = new HashMap<>();
        String accessKey = AESCBCPKCS5Padding.decrypt(projectPropertyRepository.findByProjectPropertyByPropertyName(projectId, "accessKey").getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
		String accessSecret = AESCBCPKCS5Padding.decrypt(projectPropertyRepository.findByProjectPropertyByPropertyName(projectId, "accessSecret").getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
		String bucket = AESCBCPKCS5Padding.decrypt(projectPropertyRepository.findByProjectPropertyByPropertyName(projectId, "bucketName").getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
		String region = AESCBCPKCS5Padding.decrypt(projectPropertyRepository.findByProjectPropertyByPropertyName(projectId, "region").getValue().getBytes(),AidasConstants.KEY,AidasConstants.IV_STR);
        uploadLocProps.put("accessKey",accessKey);
        uploadLocProps.put("accessSecret",accessSecret);
        uploadLocProps.put("region",region);
        uploadLocProps.put("bucketName",bucket);
        
        return uploadLocProps;
    }
    
    private static byte[] getObjectFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
    
    private void writeJsonToFile(ProjectForJson projectForJson) {
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	
		try {
			ow.writeValue(new File(this.tempFolder+"/"+this.project.getId()+"_"+this.project.getName()+".json"),projectForJson);
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    private void sendMail(String email, String  name, String downloadUrl,String fileName,String subject) throws IOException {
        String postUrl = "https://api.zeptomail.in/v1.1/email";
        BufferedReader br = null;
        HttpURLConnection conn = null;
        String output = null;
        StringBuilder sb = new StringBuilder();
        System.out.println("PHtE6r0JSujviWd78kJR5vHuQ8etNNh89b8zelIS449LCPBRHU0AqNp/kWe/qRZ5XfEUQffNzo09tbiV4O6HdD24MTxIXmqyqK3sx/VYSPOZsbq6x00ZsFwbfkPcUYLpetBo1i3Qvd6X".equals(emailToken));
        try {
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Zoho-enczapikey "+emailToken);
            JSONObject object = new JSONObject("{\n" +
                "  \"bounce_address\":\"bounce@bounce.haidata.ai\",\n" +
                "  \"from\": { \"address\": \""+fromEmail+"\"},\n" +
                "  \"to\": [{\"email_address\": {\"address\": \""+email+"\",\"name\": \""+name+"\"}}],\n" +
                "  \"subject\":\""+subject+"\",\n" +
                "  \"htmlbody\":\"<div><b>"+downloadUrl+"</b></div>\"\n" +
                "}");
            OutputStream os = conn.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}
