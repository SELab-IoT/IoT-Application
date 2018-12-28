package kr.ac.hanyang.selab.iot_application.utill;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String SHA3_256(String key) {
        String hash = null;
        try {
            Charset charset = StandardCharsets.UTF_8;
            byte[] digest = MessageDigest.getInstance("SHA3-256").digest(key.getBytes(charset));
            hash = new String(digest, charset);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
