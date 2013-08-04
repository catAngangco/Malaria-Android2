package com.cajama.malaria.newreport;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.cajama.malaria.encryption.AES;
import com.cajama.malaria.encryption.RSA;

import java.io.File;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by GMGA on 8/5/13.
 */
public class AssembleData {
    ArrayList<String> entryList,fileList, accountData;
    String USERNAME;
    Context c;

    private static final String PATIENT_TXT_FILENAME = "textData.txt";
    private static final String ACCOUNT_TXT_FILENAME = "accountData.txt";
    private static final String PATIENT_ZIP_FILENAME = "entryData.zip";
    private static final String AES_FILENAME = "cipherZipFile.zip";

    public AssembleData(Context c,ArrayList<String> entryList,ArrayList<String> fileList, ArrayList<String> accountData, String USERNAME){
        this.c = c;
        this.entryList = entryList;
        this.fileList = fileList;
        this.accountData = accountData;
        this.USERNAME = USERNAME;
    }
    private String[] getFirstZipArray(){
        try{
            fileList.add(0,c.getExternalFilesDir(null).getPath() + "/" + PATIENT_TXT_FILENAME);
        }
        catch (Exception e){
            Log.v("Error", "arrayList error");
        }
        String[] entryData = new String[fileList.size()];

        return fileList.toArray(entryData);

    }

    private String[] getSecondZipArray(){
        String[] travelData = new String[2];
        travelData[0] = c.getExternalFilesDir(null).getPath() + "/" + ACCOUNT_TXT_FILENAME;
        travelData[1] = c.getExternalFilesDir(null).getPath() + "/" + PATIENT_ZIP_FILENAME;
        return travelData;
    }
    public void start(){

        //create patient details file
        File entryFile = new File (c.getExternalFilesDir(null), PATIENT_TXT_FILENAME);
        MakeTextFile patient = new MakeTextFile(entryFile,entryList, false);
        patient.writeTextFile();

        //compress patient data file and images to a zip file
        File zipFile1 = new File (c.getExternalFilesDir(null), PATIENT_ZIP_FILENAME);
        Compress firstZip = new Compress(getFirstZipArray(),zipFile1.getPath());
        firstZip.zip();

        //AES encrypt patient zip file
        byte[] skByte = accountData.get(1).getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(skByte, "AES");
        AES aes = new AES(secretKey);
        File AESFile = new File(c.getExternalFilesDir(null),AES_FILENAME);
        aes.encryptAES(zipFile1,AESFile);

        //decryption test
        File test = new File(c.getExternalFilesDir(null),"clearZip.zip");
        aes.decryptAES(AESFile,test);

        //RSA encrypt private key
        RSA rsa = new RSA();
        accountData.set(1, rsa.encryptRSA(accountData.get(1)));

        //RSA decryption test
        accountData.add(rsa.decryptRSA(accountData.get(1)));

        //create private key text file
        File accountFile = new File(c.getExternalFilesDir(null),ACCOUNT_TXT_FILENAME);
        MakeTextFile account = new MakeTextFile(accountFile,accountData, false);
        account.writeTextFile();

        //compress patient zip file and private key text file to a 2nd zip file
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        File zipFile2 = new File (c.getExternalFilesDir("ZipFiles"), today.format("%m%d%Y_%H%M%S")+"_"+ USERNAME + ".zip");
        Compress secondZip = new Compress(getSecondZipArray(),zipFile2.getPath());
        secondZip.zip();
    }
}