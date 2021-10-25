package cc.landingzone.dreamweb.utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * 类Md5Utils.java的实现描述：md5工具类
 *
 * @author charles 2014年11月27日 上午10:37:05
 */
public class Md5Utils {

    public static String getMD5(String s) {
        byte[] b = null;
        b = s.getBytes(StandardCharsets.UTF_8);
        return getMD5(b);
    }

    public static String getMD5(byte[] b) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] str = new char[16 * 2];
        try {
            byte[] tmp = getMD5Base(b);
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return new String(str);
    }

    public static byte[] getMD5Byte(String s) {
        return getMD5Base(s.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] getMD5Base(byte[] b) {
        java.security.MessageDigest digest = null;
        try {
            digest = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //never
            throw new RuntimeException(e.getMessage());
        }
        digest.update(b);
        return digest.digest();
    }

    public static void main(String[] args) {
        System.out.println(getMD5("alimonitor"));
    }

}
