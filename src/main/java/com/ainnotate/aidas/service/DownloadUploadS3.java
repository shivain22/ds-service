package com.ainnotate.aidas.service;

import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadUploadS3  implements  Runnable{

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
    private AppPropertyRepository appPropertyRepository;


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

    public DownloadUploadS3(){

    }

    private void setGlobalDefaultValues(){
        if(user.getAuthority().getId().equals(AidasConstants.ADMIN)){
            Set<AppProperty> aidasAppProperties = user.getAppProperties();
            for(AppProperty appProperty1 :aidasAppProperties){
                if(appProperty1.getName().equals(AidasConstants.DEFAULT_STORAGE_KEY_NAME) && appProperty1.getName().equals(AidasConstants.S3)){
                    AppProperty aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_ACCESS_KEY_KEY_NAME);
                    if(aap!=null)
                        globalDownloadAccessKey = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_ACCESS_SECRET_KEY_NAME);
                    if(aap!=null)
                        globalDownloadAccessSecret = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_REGION_KEY_NAME);
                    if(aap!=null)
                        globalDownloadRegion = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_BUCKETNAME_KEY_NAME);
                    if(aap!=null)
                        globalDownloadBucketName = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_PREFIX_KEY_NAME);
                    if(aap!=null)
                        globalDownloadPrefix = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.UPLOAD_PREFIX_KEY_NAME);
                    if(aap!=null)
                        globalUploadPrefix = aap.getValue();
                }
            }
        }
        if(user.getAuthority().getId().equals(AidasConstants.ORG_ADMIN)){
            Set<AppProperty> aidasAppProperties = user.getOrganisation().getAppProperties();
            for(AppProperty appProperty1 :aidasAppProperties){
                if(appProperty1.getName().equals(AidasConstants.DEFAULT_STORAGE_KEY_NAME) && appProperty1.getName().equals(AidasConstants.S3)){
                    AppProperty aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_ACCESS_KEY_KEY_NAME);
                    if(aap!=null)
                        globalDownloadAccessKey = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_ACCESS_SECRET_KEY_NAME);
                    if(aap!=null)
                        globalDownloadAccessSecret = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_REGION_KEY_NAME);
                    if(aap!=null)
                        globalDownloadRegion = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_BUCKETNAME_KEY_NAME);
                    if(aap!=null)
                        globalDownloadBucketName = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_PREFIX_KEY_NAME);
                    if(aap!=null)
                        globalDownloadPrefix = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.UPLOAD_PREFIX_KEY_NAME);
                    if(aap!=null)
                        globalUploadPrefix = aap.getValue();
                }
            }
        }
        if(user.getAuthority().getId().equals(AidasConstants.CUSTOMER_ADMIN)){
            Set<AppProperty> aidasAppProperties = user.getCustomer().getAppProperties();
            for(AppProperty appProperty1 :aidasAppProperties){
                if(appProperty1.getName().equals(AidasConstants.DEFAULT_STORAGE_KEY_NAME) && appProperty1.getName().equals(AidasConstants.S3)){
                    AppProperty aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_ACCESS_KEY_KEY_NAME);
                    if(aap!=null)
                        globalDownloadAccessKey = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_ACCESS_SECRET_KEY_NAME);
                    if(aap!=null)
                        globalDownloadAccessSecret = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_REGION_KEY_NAME);
                    if(aap!=null)
                        globalDownloadRegion = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_BUCKETNAME_KEY_NAME);
                    if(aap!=null)
                        globalDownloadBucketName = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.DOWNLOAD_PREFIX_KEY_NAME);
                    if(aap!=null)
                        globalDownloadPrefix = aap.getValue();
                    aap = appPropertyRepository.getAppProperty(user.getId(),AidasConstants.UPLOAD_PREFIX_KEY_NAME);
                    if(aap!=null)
                        globalUploadPrefix = aap.getValue();
                }
            }
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
        this.tempFolder =  tmpdir+separator+"tmp_"+ user.getId()+"_"+ project.getId()+"_"+ project.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"tmp_"+ user.getId()+"_"+ project.getId()+"_"+ project.getName()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "tmp_"+ user.getId()+"_"+ project.getId()+"_"+ project.getName()+"_"+createDate+"_"+this.status+".zip";
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
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+"tmp_"+ user.getId()+"_"+ object.getId()+"_"+ object.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"tmp_"+ user.getId()+"_"+ object.getId()+"_"+ object.getName()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "tmp_"+ user.getId()+"_"+ object.getId()+"_"+ object.getName()+"_"+createDate+"_"+this.status+".zip";
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
        this.tempFolder =  tmpdir+separator+"tmp_"+ user.getId()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"tmp_"+ user.getId()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "tmp_"+ user.getId()+"_"+createDate+"_"+this.status+".zip";
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
                    uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId(), AidasConstants.AIDAS_UPLOAD_APPROVED);
                } else if (this.status.equals("rejected")) {
                    uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId(),AidasConstants.AIDAS_UPLOAD_REJECTED);
                }else if (this.status.equals("pending")) {
                    uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId());
                }else if (this.status.equals("all")) {
                    uploads = this.uploadRepository.getAidasUploadsByProject(this.project.getId());
                }
            } else if (this.resource.equals("object")) {
                if (this.status.equals("approved")) {
                    uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId(),AidasConstants.AIDAS_UPLOAD_APPROVED);
                } else if (this.status.equals("rejected")) {
                    uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId(),AidasConstants.AIDAS_UPLOAD_REJECTED);
                } else if(this.status.equals("pending")) {
                    uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId(),AidasConstants.AIDAS_UPLOAD_PENDING);
                } else if(this.status.equals("all")) {
                    uploads = this.uploadRepository.getAidasUploadsByObject(this.object.getId());
                }
            } else if (this.resource.equals("uploads")) {
                uploads = this.uploadRepository.findAllById(uploadedFileIds);
            }
            if (uploads != null) {
                for (Upload au : uploads) {
                    Map<String,String> uploadLocProps = getObjectProperties(au);
                    download(uploadLocProps.get("accessKey"), uploadLocProps.get("accessSecret"), uploadLocProps.get("bucketName"), uploadLocProps.get("region"), au.getObjectKey());
                }
                zip();
                URL url =  upload(globalDownloadAccessKey, globalDownloadAccessSecret, globalDownloadBucketName, globalDownloadRegion);
                Download download = new Download();
                download.setName(this.zipFile);
                download.setAwsKey(globalDownloadAccessKey);
                download.setUploadUrl(url.toString());
                download.setAwsSecret(globalDownloadAccessSecret);
                download.setBucketName(globalDownloadBucketName);
                download.setRegion(globalDownloadRegion);
                download.setObjectKey(this.zipFile);
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
            }
        }catch(Exception e){
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(msg, true);
                helper.setTo(user.getEmail());
                helper.setSubject("Unable to create downloadable objects"+this.zipFileKey);
                helper.setText("<h1>Unable to create downloadable zip.  Please try again</h1>"+e.getMessage(), true);
                javaMailSender.send(msg);
                e.printStackTrace();
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        }
    }


    private Map<String,String> getObjectProperties(Upload au){
        Map<String,String> uploadLocProps = new HashMap<>();
        for (ObjectProperty aop : au.getUserVendorMappingObjectMapping().getObject().getObjectProperties()) {
            if (aop.getProperty().getName().equals("accessKey")) {
                uploadLocProps.put("accessKey",aop.getValue());
            }
            if (aop.getProperty().getName().equals("accessSecret")) {
                uploadLocProps.put("accessSecret",aop.getValue());
            }
            if (aop.getProperty().getName().equals("region")) {
                uploadLocProps.put("region",aop.getValue());
            }
            if (aop.getProperty().getName().equals("bucketName")) {
                uploadLocProps.put("bucketName",aop.getValue());
            }
        }
        return uploadLocProps;
    }

    private void download(String accessKey, String accessSecret, String bucketName, String region, String key) throws IOException {
        Path dest = Paths.get(this.tempFolder+"/"+key);
        PipedOutputStream os = new PipedOutputStream();
        PipedInputStream is = new PipedInputStream(os);
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, accessSecret);
        S3Client s3client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(Region.of(region)).build();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(globalUploadPrefix+"/"+key).build();
        s3client.getObject(getObjectRequest, ResponseTransformer.toFile(dest));
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

    private URL upload(String accessKey, String accessSecret, String bucketName, String region) throws MessagingException {
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

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Download objects"+this.zipFileKey);
        helper.setText("<h1>Check attachment for zipfiles!</h1>"+presignedGetObjectRequest.url(), true);
        FileSystemResource res = new FileSystemResource(new File(this.zipFile));
        helper.addAttachment(this.zipFileKey, res);
        javaMailSender.send(msg);
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
}
