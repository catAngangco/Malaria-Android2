package com.cajama.malaria.encryption;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

/**
 * Created by GMGA on 8/5/13.
 */
public class RSA {
    static final String TAG = "AsymmetricAlgorithmRSA";
    int bitSize = 1024;
    PublicKey publicKey = null;
    PrivateKey privateKey = null;

    public RSA(){
        // Generate key pair for 1024-bit RSA encryption and decryption
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(bitSize, new SecureRandom());
            KeyPair kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
            Log.e(TAG, "RSA key pair error");
        }
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
