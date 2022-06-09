package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.AidasDownloadRepository;
import com.ainnotate.aidas.repository.AidasObjectRepository;
import com.ainnotate.aidas.repository.AidasProjectRepository;
import com.ainnotate.aidas.repository.AidasUploadRepository;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
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
    private AidasProject aidasProject;
    private AidasObject aidasObject;
    private String tempFolder;
    private String zipFile;
    private String zipFileKey;
    private AidasUser aidasUser;

    public AidasUser getAidasUser() {
        return aidasUser;
    }

    public void setAidasUser(AidasUser aidasUser) {
        this.aidasUser = aidasUser;
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

    public AidasProject getAidasProject() {
        return aidasProject;
    }

    public void setAidasProject(AidasProject aidasProject) {
        this.aidasProject = aidasProject;
    }

    public AidasObject getAidasObject() {
        return aidasObject;
    }

    public void setAidasObject(AidasObject aidasObject) {
        this.aidasObject = aidasObject;
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
    private AidasProjectRepository aidasProjectRepository;
    @Autowired
    private AidasObjectRepository aidasObjectRepository;
    @Autowired
    private AidasUploadRepository aidasUploadRepository;
    @Autowired
    private AidasDownloadRepository aidasDownloadRepository;
    @Autowired
    private JavaMailSender javaMailSender;




    public DownloadUploadS3(){

    }

    public void setUp(AidasProject aidasProject,String status){
        this.resource = "project";
        this.status = status;
        this.aidasProject= aidasProject;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+"tmp_"+aidasUser.getId()+"_"+aidasProject.getId()+"_"+aidasProject.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"tmp_"+aidasUser.getId()+"_"+aidasProject.getId()+"_"+aidasProject.getName()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "tmp_"+aidasUser.getId()+"_"+aidasProject.getId()+"_"+aidasProject.getName()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }

    }

    public void setUp(AidasObject aidasObject,String status){
        this.resource = "object";
        this.status = status;
        this.aidasObject= aidasObject;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+"tmp_"+aidasUser.getId()+"_"+aidasObject.getId()+"_"+aidasObject.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"tmp_"+aidasUser.getId()+"_"+aidasObject.getId()+"_"+aidasObject.getName()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "tmp_"+aidasUser.getId()+"_"+aidasObject.getId()+"_"+aidasObject.getName()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }
    }

    public void setUp(List<Long> uploadedFileIds){
        this.resource = "uploads";
        this.uploadedFileIds= uploadedFileIds;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+"tmp_"+aidasUser.getId()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+"tmp_"+aidasUser.getId()+"_"+createDate+"_"+this.status+".zip";
        this.zipFileKey = "tmp_"+aidasUser.getId()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }
    }

    @Override
    public void run() {
        List<AidasUpload> uploads=null;
        String accessKey ="";
        String accessSecret ="";
        String region = "";
        String bucketName = "";
        try {
            if (this.resource.equals("project")) {
                if (this.status.equals("approved")) {
                    uploads = this.aidasUploadRepository.getAidasUploadsByProject(this.aidasProject.getId(), AidasConstants.AIDAS_UPLOAD_APPROVED);
                } else if (this.status.equals("rejected")) {
                    uploads = this.aidasUploadRepository.getAidasUploadsByProject(this.aidasProject.getId(),AidasConstants.AIDAS_UPLOAD_REJECTED);
                } else {
                    uploads = this.aidasUploadRepository.getAidasUploadsByProject(this.aidasProject.getId(),AidasConstants.AIDAS_UPLOAD_PENDING);
                }
            } else if (this.resource.equals("object")) {
                if (this.status.equals("approved")) {
                    uploads = this.aidasUploadRepository.getAidasUploadsByObject(this.aidasObject.getId(),AidasConstants.AIDAS_UPLOAD_APPROVED);
                } else if (this.status.equals("rejected")) {
                    uploads = this.aidasUploadRepository.getAidasUploadsByObject(this.aidasObject.getId(),AidasConstants.AIDAS_UPLOAD_REJECTED);
                } else {
                    uploads = this.aidasUploadRepository.getAidasUploadsByObject(this.aidasObject.getId(),AidasConstants.AIDAS_UPLOAD_PENDING);
                }
            } else if (this.resource.equals("uploads")) {
                uploads = this.aidasUploadRepository.findAllById(uploadedFileIds);
            }
            if (uploads != null) {
                for (AidasUpload au : uploads) {
                    for (AidasObjectProperty aop : au.getAidasUserAidasObjectMapping().getAidasObject().getAidasObjectProperties()) {
                        if (aop.getAidasProperties().getName().equals("accessKey")) {
                            accessKey = aop.getValue();
                        }
                        if (aop.getAidasProperties().getName().equals("accessSecret")) {
                            accessSecret = aop.getValue();
                        }
                        if (aop.getAidasProperties().getName().equals("region")) {
                            region = aop.getValue();
                        }
                        if (aop.getAidasProperties().getName().equals("bucketName")) {
                            bucketName = aop.getValue();
                        }
                    }
                    System.out.println("About to get file "+au.getObjectKey()+" from s3");
                    download(accessKey, accessSecret, bucketName, region, au.getObjectKey());
                   }
                    zip();
                   URL url =  upload(accessKey, accessSecret, bucketName, region);

                AidasDownload aidasDownload = new AidasDownload();
                aidasDownload.setName(this.zipFile);
                aidasDownload.setAwsKey(accessKey);
                aidasDownload.setUploadUrl(url.toString());
                aidasDownload.setAwsSecret(accessSecret);
                aidasDownload.setBucketName(bucketName);
                aidasDownload.setRegion(region);
                aidasDownload.setObjectKey(this.zipFile);
                aidasDownload.setDateUploaded(Instant.now());
                if (aidasObject != null) {
                    aidasDownload.setAidasObject(aidasObject);
                }
                if (aidasProject != null) {
                    aidasDownload.setAidasProject(aidasProject);
                }
                if (uploadedFileIds != null) {
                    aidasDownload.setUploadedObjectIds(uploadedFileIds.toString());
                }
                aidasDownloadRepository.save(aidasDownload);
            }
        }catch(Exception e){
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(msg, true);
                helper.setTo(aidasUser.getEmail());
                helper.setSubject("Unable to create downloadable objects"+this.zipFileKey);
                helper.setText("<h1>Unable to create downloadable zip.  Please try again</h1>"+e.getMessage(), true);
                javaMailSender.send(msg);
                e.printStackTrace();
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        }
    }



    private void download(String accessKey, String accessSecret, String bucketName, String region, String key) throws IOException {
        Path dest = Paths.get(this.tempFolder+"/"+key);
        PipedOutputStream os = new PipedOutputStream();
        PipedInputStream is = new PipedInputStream(os);
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, accessSecret);
        S3Client s3client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(Region.of(region)).build();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key("uploads/"+key).build();
        File f = new File(this.tempFolder+"/"+key);
        System.out.println("About to get file from s3. File "+key+" status: "+f.exists() );
        if(f.exists()){
            System.out.println("File exists");
            f.delete();
        }

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
        helper.setTo(aidasUser.getEmail());
        helper.setSubject("Download objects"+this.zipFileKey);
        helper.setText("<h1>Check attachment for zipfiles!</h1>"+presignedGetObjectRequest.url(), true);
        FileSystemResource res = new FileSystemResource(new File(this.zipFile));
        helper.addAttachment(this.zipFileKey, res);
        File f = new File(this.tempFolder);
        if(f.exists()){
            if(f.isDirectory()){
                for(File f1:f.listFiles()){
                    f1.delete();
                }
            }
            f.delete();
        }
        javaMailSender.send(msg);
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
