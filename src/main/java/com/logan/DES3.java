package com.logan;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author logan
 * @version 1.0
 * @date 2022/10/22
 */
public class DES3 {

    /**
     * CBC加密
     *
     * @param key   密钥
     * @param keyiv IV
     * @param data  明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static String encodeCBC(byte[] key, byte[] keyiv, byte[] data) throws Exception {

        SecretKey deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] out = cipher.doFinal(data);
        return new String(Base64.getEncoder().encode(out), StandardCharsets.UTF_8);
    }

    /**
     * CBC解密
     *
     * @param key   密钥
     * @param keyiv IV
     * @param data  Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static String decodeCBC(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        data = Base64.getDecoder().decode(data);
        SecretKey deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);

        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] out = cipher.doFinal(data);

        return new String(out, StandardCharsets.UTF_8);

    }

    /**
     * 输出到文件
     */
    public static void outFile(String str, String fileName) throws FileNotFoundException {

        try (PrintWriter printWriter = new PrintWriter(new File(fileName))) {
            printWriter.print(str);
        }

    }

}
