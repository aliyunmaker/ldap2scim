package ldap2cloudsso.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonConstants {

    private static final Logger logger = LoggerFactory.getLogger(CommonConstants.class);

    public static final String KEY_ALIYUN_CLOUDSSO;
    public static final String LDAP_URL;
    public static final String LDAP_UserName;
    public static final String LDAP_Password;
    public static final String LDAP_Searchbase;
    public static final String LDAP_Searchfilter;

    public static final String LDAP_ATTR_FIRSTNAME;
    public static final String LDAP_ATTR_LASTNAME;
    public static final String LDAP_ATTR_EMAIL;
    public static final String LDAP_ATTR_EXTERNALID;
    public static final String LDAP_ATTR_DISPLAYNAME;
    public static final String LDAP_ATTR_USERNAME;

    public static final String CONFIG_FILE_NAME = File.separator + "ldap2cloudsso.properties";

    static {
        Properties properties = loadProperties();
        KEY_ALIYUN_CLOUDSSO = properties.getProperty("key_aliyun_cloudsso");
        LDAP_URL = properties.getProperty("ldap_url");
        LDAP_UserName = properties.getProperty("ldap_username");
        LDAP_Password = properties.getProperty("ldap_password");
        LDAP_Searchbase = properties.getProperty("ldap_searchbase");
        LDAP_Searchfilter = properties.getProperty("ldap_searchfilter");
        LDAP_ATTR_FIRSTNAME = properties.getProperty("ldap_attr_firstname");
        LDAP_ATTR_LASTNAME = properties.getProperty("ldap_attr_lastname");
        LDAP_ATTR_EMAIL = properties.getProperty("ldap_attr_email");
        LDAP_ATTR_EXTERNALID = properties.getProperty("ldap_attr_externalid");
        LDAP_ATTR_DISPLAYNAME = properties.getProperty("ldap_attr_displayname");
        LDAP_ATTR_USERNAME = properties.getProperty("ldap_attr_username");
        logger.info("============================CONFIG=========================");
        logger.info("KEY_ALIYUN_CLOUDSSO:" + KEY_ALIYUN_CLOUDSSO);
        logger.info("LDAP_URL:" + LDAP_URL);
        logger.info("LDAP_UserName:" + LDAP_UserName);
        logger.info("LDAP_Password:" + LDAP_Password);
        logger.info("LDAP_Searchbase:" + LDAP_Searchbase);
        logger.info("LDAP_Searchfilter:" + LDAP_Searchfilter);
        logger.info("-----------------------------------------------------------");
        logger.info("LDAP_ATTR_FIRSTNAME:" + LDAP_ATTR_FIRSTNAME);
        logger.info("LDAP_ATTR_LASTNAME:" + LDAP_ATTR_LASTNAME);
        logger.info("LDAP_ATTR_EMAIL:" + LDAP_ATTR_EMAIL);
        logger.info("LDAP_ATTR_EXTERNALID:" + LDAP_ATTR_EXTERNALID);
        logger.info("LDAP_ATTR_DISPLAYNAME:" + LDAP_ATTR_DISPLAYNAME);
        logger.info("LDAP_ATTR_USERNAME:" + LDAP_ATTR_USERNAME);
        logger.info("============================================================");
    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            File file = null;
            String configPath = System.getProperty("configPath");
            if (StringUtils.isNotBlank(configPath)) {
                file = new File(configPath);
            }
            if (StringUtils.isBlank(configPath) || !file.exists()) {
                configPath = System.getProperty("user.dir") + CONFIG_FILE_NAME;
                file = new File(configPath);
            }
            if (!file.exists()) {
                configPath = System.getProperty("user.home") + File.separator + "config" + CONFIG_FILE_NAME;
                file = new File(configPath);
            }
            if (!file.exists()) {
                logger.info("[1]can not find config file[-D]:" + configPath);
                logger.info("[2]can not find config file[user.dir]:" + configPath);
                logger.info("[3]can not find config file[user.home]:" + configPath);
                throw new RuntimeException("can not find config file!");
            }
            InputStream ins = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(ins, "UTF-8");
            properties.load(reader);
            ins.close();
            reader.close();
            logger.info("load config file:" + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
        return properties;
    }

}
