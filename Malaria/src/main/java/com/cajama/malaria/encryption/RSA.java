package com.cajama.malaria.encryption;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by GMGA on 8/5/13.
 */
public class RSA {
    static final String TAG = "AsymmetricAlgorithmRSA";
    int bitSize = 1024;
    PublicKey publicKey = null;
    PrivateKey privateKey = null;


    GetPublicKeyTask.OnGetKeyResult onAsyncResult = new GetPublicKeyTask.OnGetKeyResult() {
        @Override
        public void onResult(int resultCode, String message) {
            try {
                byte[] byteKey = Base64.decode(message,Base64.DEFAULT);
                X509EncodedKeySpec x = new X509EncodedKeySpec(byteKey);
                publicKey = KeyFactory.getInstance("RSA").generatePublic(x);
                //publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(message.getBytes()));
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "public key = " + message);
        }
    };

    public RSA(){
        // Generate key pair for 1024-bit RSA encryption and decryption
        try {
            GetPublicKeyTask getPublicKeyTask = new GetPublicKeyTask();
            getPublicKeyTask.setOnResultListener(onAsyncResult);
            getPublicKeyTask.execute();

            while (publicKey == null) {
                Thread.sleep(5000);
            }


            Log.v("RSA",publicKey.toString() + "\n" + privateKey.toString());
        } catch (Exception e) {
            Log.e(TAG, "RSA key pair error" + publicKey.toString() + "\n" + privateKey.toString());
        }
    }

    public RSA(int temp){
        String message = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDrN/8c/33vLA93S9d1oXurrpN57Okl0lJuQO5i+g0qVJ/mffTEVp18ECu5ACA1tnM8VvSXYdg/xd2LwHxdVzm8IpUjEWbE4mgx+w5IN6GeSeOjcbigRetq3T2x6WWvlirdnCAyQQKMmfojjj/RP/7J5w9umHU0LiAqdYrjZhJH6wIDAQAB";
        try {
            byte[] byteKey = Base64.decode(message,Base64.DEFAULT);
            X509EncodedKeySpec x = new X509EncodedKeySpec(byteKey);
            publicKey = KeyFactory.getInstance("RSA").generatePublic(x);
            //publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(message.getBytes()));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public RSA(String temp){
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
    }

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
