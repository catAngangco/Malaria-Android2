package com.cajama.malaria.encryption;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

/**
 * Created by GMGA on 8/5/13.
 */
public class RSA {
    static final String TAG = "AsymmetricAlgorithmRSA";
    int bitSize = 128;
    Key publicKey = null;
    Key privateKey = null;

    public RSA(){
        // Generate key pair for 128-bit RSA encryption and decryption
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(bitSize);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
            Log.e(TAG, "RSA key pair error");
        }
    }

    public String encryptRSA(String clearText){
        // Encode the original data with RSA private key
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, privateKey);
            encodedBytes = c.doFinal(clearText.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "RSA encryption error");
        }

        return  Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }

    public String decryptRSA(String cipherText){
        // Decode the encoded data with RSA public key
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, publicKey);
            decodedBytes = c.doFinal(cipherText.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "RSA decryption error");
        }

        return Base64.encodeToString(decodedBytes, Base64.DEFAULT);
    }
}
