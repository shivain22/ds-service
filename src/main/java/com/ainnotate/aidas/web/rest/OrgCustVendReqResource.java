package com.ainnotate.aidas.web.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Mail;
import com.ainnotate.aidas.domain.OrgCustVendReq;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.OrgCustVendReqRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link Authority}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OrgCustVendReqResource {

    private final Logger log = LoggerFactory.getLogger(OrgCustVendReqResource.class);

    private static final String ENTITY_NAME = "orgCustVendReq";
    
    @Value("${jhipster.clientApp.name}")
	private String applicationName;
    
    @Autowired
    private OrgCustVendReqRepository orgCustVendReqRepository;

    @Autowired
    private AppPropertyRepository appPropertyRepository;
    
    @PostMapping("/aidas-org-cust-vend-req")
	public ResponseEntity<OrgCustVendReq> createAidasVendor(@RequestBody OrgCustVendReq orgCustVendReq) throws URISyntaxException {
		log.debug("REST request to save AidasVendor : {}", orgCustVendReq);
		if (orgCustVendReq.getId() != null) {
			throw new BadRequestAlertException("A new vendor cannot already have an ID", ENTITY_NAME, "idexists");
		}
		try {
			OrgCustVendReq result = orgCustVendReqRepository.save(orgCustVendReq);
			Mail mail = new Mail();
			mail.setEmail(result.getEmail());
			mail.setName(result.getFirstName()+" "+result.getLastName());
			mail.setSubject("Registration request successful");
			mail.setBody("You request was submitted successfully.  We will get in touch with you shortly.");
			sendMail(mail);
			 AppProperty app  = appPropertyRepository.getAppProperty(-1l,"adminEmail");
		        String fromEmail = app.getValue();
			mail.setEmail(fromEmail);
			mail.setName(result.getFirstName()+" "+result.getLastName());
			mail.setSubject("New Org/Cust/Vend Request.");
			mail.setBody("New request raised by <br/>"
					+ "First Name: "+result.getFirstName()+" <br/>"
							+ "Last Name:"+result.getLastName()+" <br/>"
									+ "Company: "+result.getCompanyName()+"<br/>"
											+ "Message:"+result.getMessage()+"<br/>"
													+ "Aidas ID:  "+result.getId());
			sendMail(mail);
			return ResponseEntity
					.created(new URI("/api/aidas-org-cust-vend-req/" + result.getId())).headers(HeaderUtil
							.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
					.body(result);
		} catch (Exception e) {
			throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
		}
	}
    
    
    private void sendMail(Mail mail) throws IOException {
		
		 AppProperty app  = appPropertyRepository.getAppProperty(-1l,"fromEmail");
        String fromEmail = app.getValue();
        app = appPropertyRepository.getAppProperty(-1l,"emailToken");
        String emailToken = app.getValue();
        
        
       String postUrl = "https://api.zeptomail.in/v1.1/email";
       BufferedReader br = null;
       HttpURLConnection conn = null;
       String output = null;
       StringBuilder sb = new StringBuilder();
       System.out.println(mail.getEmail());
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
               "  \"to\": [{\"email_address\": {\"address\": \""+mail.getEmail()+"\",\"name\": \""+mail.getName()+"\"}}],\n" +
               "  \"subject\":\""+mail.getSubject()+"\",\n" +
               "  \"htmlbody\":\"<div><b>"+mail.getBody()+"</b></div>\"\n" +
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
