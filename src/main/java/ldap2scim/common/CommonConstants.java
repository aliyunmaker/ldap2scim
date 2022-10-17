package ldap2scim.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonConstants {

    private static final Logger logger = LoggerFactory.getLogger(CommonConstants.class);

    public static final DateTimeFormatter DateTimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String SCIM_KEY;
    public static final String SCIM_URL;

    public static final String LDAP_URL;
    public static final String LDAP_UserName;
    public static final String LDAP_Password;
    public static final String LDAP_Searchbase;
    public static final String LDAP_Searchfilter;

    public static final String SCIM_ATTR_GIVEN_NAME;
    public static final String SCIM_ATTR_FAMILY_NAME;
    public static final String SCIM_ATTR_EMAIL;
    public static final String SCIM_ATTR_EXTERNALID;
    public static final String SCIM_ATTR_DISPLAYNAME;
    public static final String SCIM_ATTR_USERNAME;

    public static final boolean SCIM_SYNC_CRON_ENABLED;
    public static final String SCIM_SYNC_CRON_EXPRESSION;

    public static final String CONFIG_FILE_NAME = File.separator + "ldap2scim.properties";

    static {
        Properties properties = loadProperties();
        SCIM_KEY = properties.getProperty("scim_key");
        SCIM_URL = properties.getProperty("scim_url");
        LDAP_URL = properties.getProperty("ldap_url");
        LDAP_UserName = properties.getProperty("ldap_username");
        LDAP_Password = properties.getProperty("ldap_password");
        LDAP_Searchbase = properties.getProperty("ldap_searchbase");
        LDAP_Searchfilter = properties.getProperty("ldap_searchfilter");
        SCIM_ATTR_GIVEN_NAME = properties.getProperty("scim_attr_givenname");
        SCIM_ATTR_FAMILY_NAME = properties.getProperty("scim_attr_familyname");
        SCIM_ATTR_EMAIL = properties.getProperty("scim_attr_email");
        SCIM_ATTR_EXTERNALID = properties.getProperty("scim_attr_externalid");
        SCIM_ATTR_DISPLAYNAME = properties.getProperty("scim_attr_displayname");
        SCIM_ATTR_USERNAME = properties.getProperty("scim_attr_username");
        SCIM_SYNC_CRON_ENABLED = Boolean.parseBoolean(properties.getProperty("scim_sync_cron_enabled"));
        SCIM_SYNC_CRON_EXPRESSION = properties.getProperty("scim_sync_cron_expression");
        logger.info("============================CONFIG=========================");
        logger.info("SCIM_URL:" + SCIM_URL);
        logger.info("SCIM_KEY:" + SCIM_KEY);
        logger.info("-----------------------------------------------------------");
        logger.info("LDAP_URL:" + LDAP_URL);
        logger.info("LDAP_UserName:" + LDAP_UserName);
        logger.info("LDAP_Password:" + LDAP_Password);
        logger.info("LDAP_Searchbase:" + LDAP_Searchbase);
        logger.info("LDAP_Searchfilter:" + LDAP_Searchfilter);
        logger.info("-----------------------------------------------------------");
        logger.info("SCIM_ATTR_GIVEN_NAME:" + SCIM_ATTR_GIVEN_NAME);
        logger.info("SCIM_ATTR_FAMILY_NAME:" + SCIM_ATTR_FAMILY_NAME);
        logger.info("SCIM_ATTR_EMAIL:" + SCIM_ATTR_EMAIL);
        logger.info("SCIM_ATTR_EXTERNALID:" + SCIM_ATTR_EXTERNALID);
        logger.info("SCIM_ATTR_DISPLAYNAME:" + SCIM_ATTR_DISPLAYNAME);
        logger.info("SCIM_ATTR_USERNAME:" + SCIM_ATTR_USERNAME);
        logger.info("-----------------------------------------------------------");
        logger.info("SCIM_SYNC_CRON_ENABLED:" + SCIM_SYNC_CRON_ENABLED);
        logger.info("SCIM_SYNC_CRON_EXPRESSION:" + SCIM_SYNC_CRON_EXPRESSION);
        logger.info("============================================================");
    }

    /**
     * 如果configPath不为空,则优先使用该路径,若为空则继续查找 <br>
     *
     * @return null
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            File file;
            String configPath = System.getProperty("configPath");
            if (StringUtils.isNotBlank(configPath)) {
                file = new File(configPath);
                if (!file.exists()) {
                    logger.info("[1]can not find config file[-D]:" + configPath);
                    throw new RuntimeException("can not find config file!");
                }
            } else {
                configPath = System.getProperty("user.dir") + CONFIG_FILE_NAME;
                file = new File(configPath);
                if (!file.exists()) {
                    configPath = System.getProperty("user.home") + File.separator + "config" + CONFIG_FILE_NAME;
                    file = new File(configPath);
                }
                if (!file.exists()) {
                    logger.info("[2]can not find config file[user.dir]:" + configPath);
                    logger.info("[3]can not find config file[user.home]:" + configPath);
                    throw new RuntimeException("can not find config file!");
                }
            }

            InputStream ins = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(ins, StandardCharsets.UTF_8);
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
