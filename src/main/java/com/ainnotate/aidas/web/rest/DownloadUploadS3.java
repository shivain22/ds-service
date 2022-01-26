package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.AidasDownloadRepository;
import com.ainnotate.aidas.repository.AidasObjectRepository;
import com.ainnotate.aidas.repository.AidasProjectRepository;
import com.ainnotate.aidas.repository.AidasUploadRepository;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
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

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadUploadS3  implements  Runnable{

    private String resource;
    private String status;
    private List<Long> uploadedFileIds;
    private AidasProject aidasProject;
    private AidasObject aidasObject;
    private AidasProjectRepository aidasProjectRepository;
    private AidasObjectRepository aidasObjectRepository;
    private AidasUploadRepository aidasUploadRepository;
    private String tempFolder;
    private String zipFile;
    private AidasDownloadRepository aidasDownloadRepository;


    public DownloadUploadS3(AidasProject aidasProject,String status, AidasProjectRepository aidasProjectRepository, AidasUploadRepository aidasUploadRepository, AidasDownloadRepository aidasDownloadRepository){
        this.resource = "project";
        this.status = status;
        this.aidasProject= aidasProject;
        this.aidasProjectRepository = aidasProjectRepository;
        this.aidasUploadRepository = aidasUploadRepository;
        this.aidasDownloadRepository = aidasDownloadRepository;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+aidasProject.getId()+"_"+aidasProject.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+aidasProject.getId()+"_"+aidasProject.getName()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }

    }

    public DownloadUploadS3(AidasObject aidasObject,String status, AidasObjectRepository aidasObjectRepository, AidasUploadRepository aidasUploadRepository, AidasDownloadRepository aidasDownloadRepository){
        this.resource = "object";
        this.status = status;
        this.aidasObject= aidasObject;
        this.aidasObjectRepository = aidasObjectRepository;
        this.aidasUploadRepository = aidasUploadRepository;
        this.aidasDownloadRepository = aidasDownloadRepository;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+aidasObject.getId()+"_"+aidasObject.getName()+"_"+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+aidasObject.getId()+"_"+aidasObject.getName()+"_"+createDate+"_"+this.status+".zip";
        File f = new File(this.tempFolder);
        if(!f.exists()){
            f.mkdir();
        }
    }

    public DownloadUploadS3(List<Long> uploadedFileIds, AidasUploadRepository aidasUploadRepository, AidasDownloadRepository aidasDownloadRepository){
        this.resource = "uploads";
        this.uploadedFileIds= uploadedFileIds;
        this.aidasUploadRepository = aidasUploadRepository;
        this.aidasDownloadRepository = aidasDownloadRepository;
        String format = "yyyy_MM_dd_HH_mm";
        DateFormat dateFormatter = new SimpleDateFormat(format);
        String createDate = dateFormatter.format(new Date());
        String separator = FileSystems.getDefault().getSeparator();
        String tmpdir = System.getProperty("java.io.tmpdir");
        this.tempFolder =  tmpdir+separator+createDate+"_"+this.status;
        this.zipFile= tmpdir+separator+createDate+"_"+this.status+".zip";
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
            if(this.resource.equals("project")){
                if(this.status.equals("approved")){
                    uploads = this.aidasUploadRepository.getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsTrue(this.aidasProject);
                }else if(this.status.equals("rejected")){
                    uploads = this.aidasUploadRepository.getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsFalse(this.aidasProject);
                }else{
                    uploads = this.aidasUploadRepository.getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsNull(this.aidasProject);
                }
            }else if(this.resource.equals("object")){
                if(this.status.equals("approved")){
                    uploads = this.aidasUploadRepository.getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsTrue(this.aidasObject);
                }else if(this.status.equals("rejected")){
                    uploads = this.aidasUploadRepository.getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsFalse(this.aidasObject);
                }else{
                    uploads = this.aidasUploadRepository.getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsNull(this.aidasObject);
                }
            }else if(this.resource.equals("uploads")){
                if(uploadedFileIds.size()>0){
                    uploads = new ArrayList<>();
                }
                for(Long uploadId:uploadedFileIds){
                    uploads.add(this.aidasUploadRepository.getById(uploadId));
                }
            }
            if(uploads!=null){
                for(AidasUpload au:uploads){
                    for(AidasObjectProperty aop: au.getAidasUserAidasObjectMapping().getAidasObject().getAidasObjectProperties()){
                        if(aop.getAidasProperties().getName().equals("accessKey")){
                            accessKey = aop.getValue();
                        }
                        if(aop.getAidasProperties().getName().equals("accessSecret")){
                            accessSecret = aop.getValue();
                        }
                        if(aop.getAidasProperties().getName().equals("region")){
                            region = aop.getValue();
                        }
                        if(aop.getAidasProperties().getName().equals("bucketName")){
                            bucketName = aop.getValue();
                        }
                    }
                    try {
                        download(accessKey,accessSecret,bucketName,region,au.getObjectKey());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    zip();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                upload(accessKey,accessSecret,bucketName,region);
                AidasDownload aidasDownload = new AidasDownload();
                aidasDownload.setName(this.zipFile);
                aidasDownload.setAwsKey(accessKey);
                aidasDownload.setAwsSecret(accessSecret);
                aidasDownload.setBucketName(bucketName);
                aidasDownload.setRegion(region);
                aidasDownload.setObjectKey(this.zipFile);
                aidasDownload.setDateUploaded(Instant.now());
                if(aidasObject!=null){
                    aidasDownload.setAidasObject(aidasObject);
                }
                if(aidasProject!=null){
                    aidasDownload.setAidasProject(aidasProject);
                }
                if(uploadedFileIds!=null){
                    aidasDownload.setUploadedObjectIds(uploadedFileIds.toString());
                }
                aidasDownloadRepository.save(aidasDownload);
        }
    }

    private void download(String accessKey, String accessSecret, String bucketName, String region, String key) throws IOException {
        Path dest = Paths.get(this.tempFolder+"/"+key);
        PipedOutputStream os = new PipedOutputStream();
        PipedInputStream is = new PipedInputStream(os);
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, accessSecret);
        S3Client s3client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(Region.of(region)).build();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
        System.out.println(accessKey+" ###Secret### "+accessSecret+" ###BucketName#### "+bucketName+" ####Region### "+region+" ###ObjectKey####  "+key);
        s3client.getObject(getObjectRequest, ResponseTransformer.toFile(dest));
    }

    public void zip( ) throws IOException {
        Path zipFile = Paths.get(this.zipFile);
        Path sourceDirPath = Paths.get(this.tempFolder);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile));
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
        System.out.println("Zip is created at : "+zipFile);
    }

    private void upload(String accessKey, String accessSecret, String bucketName, String region){
        Map<String, String> metadata = new HashMap<>();
        metadata.put("x-amz-meta-file", this.zipFile);
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, accessSecret);
        S3Client s3client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(Region.of(region)).build();
        PutObjectRequest putOb = PutObjectRequest.builder().bucket(bucketName).key(this.zipFile).metadata(metadata).build();
        PutObjectResponse response = s3client.putObject(putOb, software.amazon.awssdk.core.sync.RequestBody.fromBytes(getObjectFile(this.zipFile)));
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
