package com.cajama.malaria.encryption;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by GMGA on 8/4/13.
 */
public class AES {
    SecretKeySpec sk;
    int size;
    static final String TAG = "SymmetricAlgorithmAES";
    public AES(SecretKeySpec sk){
        this.sk = sk;
    }

    public void encryptAES(File clearTextFile, File cipherTextFile){
        Log.v(TAG,"Start encryption");



        try{
            Cipher cipher = Cipher.getInstance("AES");
            Log.v(TAG,"New cipher" + sk);
            cipher.init(Cipher.ENCRYPT_MODE,sk);
            Log.v(TAG,"Cipher created");

            FileInputStream fis = new FileInputStream(clearTextFile);
            FileOutputStream fos = new FileOutputStream(cipherTextFile);
            CipherOutputStream cos = new CipherOutputStream(fos,cipher);
            Log.v(TAG,"Streams created");
            byte[] block = new byte[8];
            while ((size = fis.read(block)) != -1) {
                cos.write(block, 0, size);
            }
            cos.close();

        } catch (Exception e){ Log.v(TAG,"AES encryption error");}
    }

    public void decryptAES(File cipherTextFile,File clearTextFile){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,sk);

            FileInputStream fis = new FileInputStream(cipherTextFile);
            CipherInputStream cis = new CipherInputStream(fis,cipher);
            FileOutputStream fos = new FileOutputStream(clearTextFile);

            byte[] block = new byte[8];
            while ((size = cis.read(block)) != -1) {
                fos.write(block, 0, size);
            }
            fos.close();

        } catch (Exception e){  Log.v(TAG,"AES decryption error"); }
    }

}
