package com.ainnotate.aidas.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.DownloadRepository;
import com.ainnotate.aidas.repository.ObjectPropertyRepository;
import com.ainnotate.aidas.repository.ObjectRepository;
import com.ainnotate.aidas.repository.ProjectRepository;
import com.ainnotate.aidas.repository.UploadRepository;

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
    private ObjectPropertyRepository objectPropertyRepository;
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

    private String fromEmail;
    private String emailToken;

    public DownloadUploadJson(){

    }

    private void setProperties(Set<AppProperty> appProperties){
        for(AppProperty appProperty :appProperties){
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_ACCESS_KEY_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadAccessKey = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_ACCESS_SECRET_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadAccessSecret = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_REGION_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadRegion = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_BUCKETNAME_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadBucketName = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.DOWNLOAD_PREFIX_KEY_NAME) && appProperty.getValue()!=null)
                globalDownloadPrefix = appProperty.getValue();


            if(appProperty.getName().equals(AidasConstants.UPLOAD_ACCESS_KEY_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadAccessKey = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.UPLOAD_ACCESS_SECRET_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadAccessSecret = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.UPLOAD_REGION_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadRegion = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.UPLOAD_BUCKETNAME_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadBucketName = appProperty.getValue();
            if(appProperty.getName().equals(AidasConstants.UPLOAD_PREFIX_KEY_NAME) && appProperty.getValue()!=null)
                globalUploadPrefix = appProperty.getValue();

            AppProperty app  = appPropertyRepository.getAppProperty(-1l,"fromEmail");
            fromEmail = app.getValue();
            app = appPropertyRepository.getAppProperty(-1l,"emailToken");
            emailToken = app.getValue();

        }
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

    private void sendMail(String email, String  name, String downloadUrl,String fileName,String subject) throws IOException {
        String postUrl = "https://api.zeptomail.in/v1.1/email";
        BufferedReader br = null;
        HttpURLConnection conn = null;
        String output = null;
        StringBuilder sb = new StringBuilder();
        
        try {
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Zoho-enczapikey "+emailToken);
            JSONObject object = new JSONObject("{\n" +
                "  \"bounce_address\":\"aidas@bounce.ainnotate.com\",\n" +
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
