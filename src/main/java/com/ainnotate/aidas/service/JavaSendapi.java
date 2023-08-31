package com.ainnotate.aidas.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class JavaSendapi {
    public static void main(String[] args) throws Exception {
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
            conn.setRequestProperty("Authorization", "Zoho-enczapikey PHtE6r0JSujviWd78kJR5vHuQ8etNNh89b8zelIS449LCPBRHU0AqNp/kWe/qRZ5XfEUQffNzo09tbiV4O6HdD24MTxIXmqyqK3sx/VYSPOZsbq6x00ZsFwbfkPcUYLpetBo1i3Qvd6X");
            JSONObject object = new JSONObject("{\n" + "\"bounce_address\":\"bounce@bounce.haidata.ai\",\n" + "\"from\": { \"address\": \"noreply@haidata.ai\"},\n" + "\"to\": [{\"email_address\": {\"address\": \"santhosh@ainnotate.com\",\"name\": \"Santhosh Muralidharan\"}}],\n" + "\"subject\":\"Test Email\",\n" + "\"htmlbody\":\"<div><b> Test email sent successfully.  </b></div>\"\n" + "}");
            OutputStream os = conn.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            System.out.println(sb.toString());
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
