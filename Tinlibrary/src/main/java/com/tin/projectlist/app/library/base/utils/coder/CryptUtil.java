package com.tin.projectlist.app.library.base.utils.coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * com.txtw.base.utils.httputil.CryptUtil
 *
 * @author wulb 功能描述：加密解密工具类 创建时间 2013-7-23 下午09:12:19
 */
public class CryptUtil {

    private final static String DES = "DES";

    private static final String m_strKey = "";
    private static final String m_strIv = "123ABC4D";

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     */
    private static byte[] encrypt(byte[] src, byte[] key, byte[] keyiv) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(src);

        return bOut;
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     */
    private static byte[] decrypt(byte[] src, byte[] key, byte[] keyiv) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);

        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] bOut = cipher.doFinal(src);

        return bOut;
    }

    /**
     * 压缩
     */
    public static byte[] Deflate(byte[] source) throws IOException {
        Deflater deflater = new Deflater(-1);
        ByteArrayOutputStream stream = null;
        byte[] result;
        try {
            deflater.setInput(source);
            deflater.finish();
            stream = new ByteArrayOutputStream(source.length);
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int compressed = deflater.deflate(buffer);
                stream.write(buffer, 0, compressed);
            }
            stream.close();
            result = stream.toByteArray();
            stream = null;
        } finally {
            deflater.end();
            if (stream != null) {
                stream.close();
            }
        }
        return result;
    }

    /**
     * 解压
     */
    public static byte[] Inflate(byte[] value) throws DataFormatException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
        Inflater deCompressor = new Inflater();
        try {
            deCompressor.setInput(value);

            final byte[] buf = new byte[1024 * 8];
            while (!deCompressor.finished()) {
                int count = deCompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            deCompressor.end();
        }

        return bos.toByteArray();
    }

    public static byte[] decrypt(byte data[], String key) throws Exception {
        if (data == null) {
            return null;
        }
        byte[] bt = decrypt(data, key.getBytes());
        return bt;
    }

}
