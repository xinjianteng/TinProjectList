package com.tin.projectlist.app.library.base.utils.coder;


import com.cliff.libs.util.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class Coder {

    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * BASE64解密
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64加密
     */
    public static String encryptBASE64(byte[] key) {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    /**
     * MD5加密
     */
    public static byte[] encryptMD5(byte[] data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data);

        return md5.digest();

    }

    /**
     * MD5加密
     */
    public static String encryptMD5(String data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data.getBytes());

        return String.valueOf(encodeHex(md5.digest()));

    }


    /**
     * SHA加密
     */
    public static byte[] encryptSHA(byte[] data) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);

        return sha.digest();

    }


    public static String encryptMD5(File file)  {
        if (file == null || !file.exists()) {
            return null;
        }

        FileInputStream fis = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            return String.valueOf(encodeHex(md5.digest()));
        } catch (Exception e) {
        } finally {
            StreamUtil.close(fis);
        }

        return null;

    }



    private static char[] encodeHex(byte[] bytes) {
        char[] chars = new char[32];

        for(int i = 0; i < chars.length; i += 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[b >>> 4 & 15];
            chars[i + 1] = HEX_CHARS[b & 15];
        }
        return chars;
    }
}  