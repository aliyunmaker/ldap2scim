package cc.landingzone.dreamweb.utils;

import java.util.UUID;

public class UUIDUtils {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成api的随机accessKey
     *
     * @return
     */
    public static String generateApiAccesskeyId() {
        String uuid = generateUUID();
        return "ID" + uuid.substring(0, 10);
    }

    public static void main(String[] args) {
        System.out.println(generateApiAccesskeyId());
        System.out.println(generateUUID());
    }

}
