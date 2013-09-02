package com.cajama.malaria.encryption;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.cajama.malaria.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by GMGA on 8/5/13.
 */
public class RSA {
    static final String TAG = "AsymmetricAlgorithmRSA";
    int bitSize = 1024;
    PublicKey publicKey = null;
    PrivateKey privateKey = null;
    File keyFile;

    GetPublicKeyTask.OnGetKeyResult onAsyncResult = new GetPublicKeyTask.OnGetKeyResult() {
        @Override
        public void onResult(int resultCode, String message) {
        	if (resultCode == 0) 
        		message = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDrN/8c/33vLA93S9d1oXurrpN57Okl0lJuQO5i+g0qVJ/mffTEVp18ECu5ACA1tnM8VvSXYdg/xd2LwHxdVzm8IpUjEWbE4mgx+w5IN6GeSeOjcbigRetq3T2x6WWvlirdnCAyQQKMmfojjj/RP/7J5w9umHU0LiAqdYrjZhJH6wIDAQAB";
        	
            try {
            	BufferedWriter fos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(keyFile)));
            	fos.write(message);
            	fos.close();
                byte[] byteKey = Base64.decode(message,Base64.DEFAULT);
                X509EncodedKeySpec x = new X509EncodedKeySpec(byteKey);
                publicKey = KeyFactory.getInstance("RSA").generatePublic(x);
                //publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(message.getBytes()));
            } catch (Exception e) {
            	e.printStackTrace();
            }
            Log.d(TAG, "public key = " + message);
        }
    };

    public RSA(Context c){
        // Generate key pair for 1024-bit RSA encryption and decryption
    	
    	/*try {
    		String message = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbMiQNyj/Nzj7QQBj8nAx3WKPy4r72Onnw/E9KQY1gUwepRtCKYgJz9typvvfi7zNROFp5o8OBL5mVWFShxMIh57entYRpZUGtTZtb7YT5Bx3ZM5FPKQzHcJDyPJFloJW6V0+jmmZSosvM4nrZknVW3DABdmiVadGXtdy9DNevyQIDAQAB";
			byte[] byteKey = null;
			byteKey = Base64.decode(message, Base64.DEFAULT);
			int sum = 0;
			for (int i = 0; i < byteKey.length; i++) {
				sum += byteKey[i];
			}
			System.err.println("Sum: " + sum);
			X509EncodedKeySpec x = new X509EncodedKeySpec(byteKey);
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(x);
			System.err.println(publicKey);
			System.err.println("Length: " + byteKey.length);
    		
    		File f = new File(c.getExternalFilesDir(null), "a.txt");
    		if (f.exists()) f.delete();
			f.createNewFile();
    		
    		FileWriter fileWriter = new FileWriter(f, true);
    		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    		bufferedWriter.write(publicKey.toString());
    		bufferedWriter.write("Length: " + byteKey.length);
    		bufferedWriter.write("Sum: " + sum);
    		bufferedWriter.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}*/
    	
    	keyFile = new File(c.getExternalFilesDir(null), "key.txt");
    	if (keyFile.exists()) {
    		try {
				BufferedReader br = new BufferedReader(new FileReader(keyFile));
				String message = br.readLine();
				br.close();
				byte[] byteKey = Base64.decode(message,Base64.DEFAULT);
                X509EncodedKeySpec x = new X509EncodedKeySpec(byteKey);
                publicKey = KeyFactory.getInstance("RSA").generatePublic(x);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "error in creating public key from text file");
				e.printStackTrace();
			}
    	}
    	else {
	        try {
	        	keyFile.createNewFile();
	            GetPublicKeyTask getPublicKeyTask = new GetPublicKeyTask();
	            getPublicKeyTask.setOnResultListener(onAsyncResult);
	            getPublicKeyTask.execute();
	
	            while (publicKey == null) {
	                Thread.sleep(1000);
	            }
	            
	        } catch (Exception e) {
	            Log.e(TAG, "RSA key pair error" + publicKey.toString() + "\n" + privateKey.toString());
	        }
    	}
    }

    public RSA(int temp){
        String message = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDrN/8c/33vLA93S9d1oXurrpN57Okl0lJuQO5i+g0qVJ/mffTEVp18ECu5ACA1tnM8VvSXYdg/xd2LwHxdVzm8IpUjEWbE4mgx+w5IN6GeSeOjcbigRetq3T2x6WWvlirdnCAyQQKMmfojjj/RP/7J5w9umHU0LiAqdYrjZhJH6wIDAQAB";
        try {
            byte[] byteKey = Base64.decode(message,Base64.DEFAULT);
            X509EncodedKeySpec x = new X509EncodedKeySpec(byteKey);
            publicKey = KeyFactory.getInstance("RSA").generatePublic(x);
            Log.d(TAG, "public key = " + publicKey.toString());
            //publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(message.getBytes()));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /*public RSA(String temp){
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(bitSize, new SecureRandom());
        KeyPair kp = kpg.generateKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }*/

    public String encryptRSA(byte[] clearText){
        // Encode the original data with RSA private key
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, publicKey);
            encodedBytes = c.doFinal(clearText);
        } catch (Exception e) {
            Log.e(TAG, "RSA encryption error");
        }
        //return decryptRSA(encodedBytes);
        return Base64.encodeToString(encodedBytes,Base64.DEFAULT);
    }

    public String decryptRSA(byte[] cipherText){
        // Decode the encoded data with RSA public key
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, privateKey);
            decodedBytes = c.doFinal(cipherText);

            return Base64.encodeToString(decodedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "RSA decryption error"+ e);
        }

        return "Decryption not completed";
    }
}