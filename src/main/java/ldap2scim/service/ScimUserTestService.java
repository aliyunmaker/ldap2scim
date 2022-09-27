package ldap2scim.service;

import org.apache.commons.lang3.StringUtils;

import ldap2scim.model.ScimGroup;
import ldap2scim.model.ScimUser;

public class ScimUserTestService {

    public static void main(String[] args) throws Exception {
        // generateTestScimUser(200);
        generateTestScimGroup(200);
    }

    public static void generateTestScimGroup(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            String suffix = StringUtils.leftPad("" + i, 4, '0');
            ScimGroup scimGroup = new ScimGroup();
            scimGroup.setDisplayName("displayName" + suffix);
            scimGroup.setExternalId("externalId+" + suffix + "@test.com");
            ScimGroupService.addGroup(scimGroup);
            System.out.println("[group]done:" + i);
        }
    }

    public static void generateTestScimUser(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            String suffix = StringUtils.leftPad("" + i, 4, '0');
            ScimUser scimUser = new ScimUser();
            scimUser.setDisplayName("displayName" + suffix);
            scimUser.setGivenName("givenName" + suffix);
            scimUser.setFamilyName("familyName" + suffix);
            scimUser.setUserName("userName" + suffix);
            scimUser.setEmail("email" + suffix + "@test.com");
            scimUser.setExternalId("externalId+" + suffix + "@test.com");
            ScimUserService.addUser(scimUser);
            // 用RateLimiter控制
            // Thread.sleep(10);
            System.out.println("[user]done:" + i);
        }
    }

}
