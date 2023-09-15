package com.ainnotate.aidas.service;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class AESCBCPKCS5Padding {

	
	  public static void main(String[] args) throws Exception { 
		  String key ="b693b2f6350f11eebe560242ac120002"; 
		  String iv = "1011121314151617"; 
		  String toEncrypt = "aidac-uploads";
	 
	  byte[] encrypted = encrypt(toEncrypt, key, iv); 
	  String decrypted = decrypt(encrypted, key, iv);
	 
	  System.out.println("Encrypted "+toEncrypt+"======"+ new String(encrypted)); 
	  System.out.println("Decrypted ="+decrypted); 
	  }
	 

	public static String encryptString(String plainText, String key, String ivStr)throws Exception{
		return new String(AESCBCPKCS5Padding.encrypt(plainText, key, ivStr));
	}
    public static byte[] encrypt(String plainText, String key, String ivStr) throws Exception {
        byte[] clean = plainText.getBytes();

        // Initialization Vector
        byte[] iv = ivStr.getBytes(Charset.forName("US-ASCII"));
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Key
        byte[] keyBytes = key.getBytes(Charset.forName("US-ASCII"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Encrypt.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(clean);

        byte[] encoded = Base64.getEncoder().encode(encrypted);
        return encoded;
    }

    public static String decrypt(byte[] encryptedBytes, String key, String ivStr) throws Exception {

        byte[] iv = ivStr.getBytes(Charset.forName("US-ASCII"));
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        byte[] keyBytes = key.getBytes(Charset.forName("US-ASCII"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedBytes);

        byte[] decrypted = cipherDecrypt.doFinal(decoded);

        return new String(decrypted);
    }
}
