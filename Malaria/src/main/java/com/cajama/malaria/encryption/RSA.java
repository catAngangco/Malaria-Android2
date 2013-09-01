package com.cajama.malaria.encryption;

import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


public class RSA {
    static final String TAG = "AsymmetricAlgorithmRSA";
    int bitSize = 1024;
    PublicKey publicKey = null;
    PrivateKey privateKey = null;
    RSAPublicKey rsaPublicKey = null;
    RSAPrivateKey rsaPrivateKey = null;
    String message = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDrN/8c/33vLA93S9d1oXurrpN57Okl0lJuQO5i+g0qVJ/mffTEVp18ECu5ACA1tnM8VvSXYdg/xd2LwHxdVzm8IpUjEWbE4mgx+w5IN6GeSeOjcbigRetq3T2x6WWvlirdnCAyQQKMmfojjj/RP/7J5w9umHU0LiAqdYrjZhJH6wIDAQAB";
    String modulus = "009b32240dca3fcdce3ed04018fc9c0c7758a3f2e2bef63a79f0fc4f4a418d605307a946d08a620273f6dca9bef7e2ef3351385a79a3c3812f99955854a1c4c221e7b7a7b584696541ad4d9b5bed84f9071dd933914f290cc77090f23c9165a095ba574fa39a6652a2cbcce27ad9927556dc300176689569d197b5dcbd0cd7afc9";

    GetPublicKeyTask.OnGetKeyResult onAsyncResult = new GetPublicKeyTask.OnGetKeyResult() {
        @Override
        public void onResult(int resultCode, String message) {
            BigInteger mod = new BigInteger(message,16);
            BigInteger exp = new BigInteger("10001",16);

            try {
                rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(mod,exp));
            } catch (Exception e) {
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

    public RSA(byte[] bytePrivateKey){
        //private key
        try {
            //privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytePrivateKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //public key

        BigInteger mod = new BigInteger(modulus,16);
        BigInteger exp = new BigInteger("10001",16);

        try {
            rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(mod,exp));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.v("ENCRYPTION","Public key 2:" + rsaPublicKey);
    }

    public String encryptRSA(byte[] clearText){
        // Encode the original data with RSA private key
        byte[] encodedBytes = null;
        Cipher c = null;
        try {
            c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            encodedBytes = c.doFinal(clearText);
        } catch (Exception e) {
            e.printStackTrace();
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
