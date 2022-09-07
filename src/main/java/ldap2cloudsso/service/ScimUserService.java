package ldap2cloudsso.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import ldap2cloudsso.common.CommonConstants;
import ldap2cloudsso.model.ScimResult;
import ldap2cloudsso.model.ScimUser;
import ldap2cloudsso.model.UserResource;
import ldap2cloudsso.model.UserResourceEmail;
import ldap2cloudsso.model.UserResourceName;
import ldap2cloudsso.utils.HttpClientUtils;
import ldap2cloudsso.utils.JsonUtils;
import ldap2cloudsso.utils.UUIDUtils;

public class ScimUserService {

    private static String ScimURL = "https://cloudsso-scim-cn-shanghai.aliyun.com/scim/v2";
    private static String ScimKey = CommonConstants.KEY_ALIYUN_CLOUDSSO;

    private static Logger logger = LoggerFactory.getLogger(ScimUserService.class);

    public static void main(String[] args) throws Exception {
        ScimUser scimUser = new ScimUser();
        scimUser.setDisplayName("cheng1234");
        scimUser.setExternalId(UUIDUtils.generateUUID());
        scimUser.setUserName("cheng1234");
        addUser(scimUser);
    }

    public static List<ScimUser> searchScimUser(String filter) throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + ScimKey);
        Map<String, String> params = new HashMap<>();
        params.put("startIndex", "0");
        params.put("count", "100");
        if (StringUtils.isNotBlank(filter)) {
            params.put("filter", filter);
        }
        String result = HttpClientUtils.getDataAsStringFromUrlWithHeader(
            ScimURL + "/Users", params, header);
        ScimResult scimResult = JsonUtils.parseObject(result, ScimResult.class);
        List<UserResource> list = scimResult.getResources();
        logger.info("filter:" + filter);
        logger.info("==================================");
        logger.info(JsonUtils.toJsonString(list));
        return convertToScimUser(list);
    }

    public static void addUser(ScimUser scimUser) throws Exception {
        Assert.notNull(scimUser, "scimUser can not be null!");
        UserResource user = convertToUserResource(scimUser);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + ScimKey);
        HttpClientUtils.postUrlAndStringBodyWithHeader(ScimURL + "/Users", JsonUtils.toJsonStringDefault(user), header);
    }

    public static void updateUser(ScimUser scimUser) throws Exception {
        Assert.notNull(scimUser, "scimUser can not be null!");
        Assert.hasText(scimUser.getId(), "id can not be blank!");
        UserResource user = convertToUserResource(scimUser);
        String id = user.getId();
        // user.setId(null);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + ScimKey);
        HttpClientUtils.putUrlAndStringBodyWithHeader(ScimURL + "/Users/" + id, JsonUtils.toJsonStringDefault(user),
            header);
    }

    public static void deleteUser(String id) throws Exception {
        Assert.hasText(id, "id can not be blank!");
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + ScimKey);
        HttpClientUtils.deleteFromUrlWithHeader(ScimURL + "/Users/" + id, header);

    }

    public static List<ScimUser> convertToScimUser(List<UserResource> userResourceList) {
        if (userResourceList == null || userResourceList.isEmpty()) {
            return new ArrayList<>();
        }
        Assert.notEmpty(userResourceList, "userResourceList can not be null!");
        List<ScimUser> list = new ArrayList<ScimUser>();
        for (UserResource userResource : userResourceList) {
            list.add(convertToScimUser(userResource));
        }
        return list;
    }

    public static List<UserResource> convertToUserResource(List<ScimUser> scimUserList) {
        Assert.notEmpty(scimUserList, "scimUserList can not be null!");
        List<UserResource> list = new ArrayList<UserResource>();
        for (ScimUser scimUser : scimUserList) {
            list.add(convertToUserResource(scimUser));
        }
        return list;
    }

    public static ScimUser convertToScimUser(UserResource userResource) {
        Assert.notNull(userResource, "userResource can not be null!");

        ScimUser scimUser = new ScimUser();
        scimUser.setId(userResource.getId());
        scimUser.setExternalId(userResource.getExternalId());
        scimUser.setUserName(userResource.getUserName());
        scimUser.setDisplayName(userResource.getDisplayName());
        if (userResource.getName() != null) {
            scimUser.setFirstName(userResource.getName().getGivenName());
            scimUser.setLastName(userResource.getName().getFamilyName());
        }
        if (userResource.getEmails() != null && userResource.getEmails().size() > 0) {
            scimUser.setEmail(userResource.getEmails().get(0).getValue());
        }
        return scimUser;
    }

    public static UserResource convertToUserResource(ScimUser scimUser) {
        Assert.notNull(scimUser, "scimUser can not be null!");
        UserResource user = new UserResource();
        user.setId(scimUser.getId());
        user.setExternalId(scimUser.getExternalId());
        user.setUserName(scimUser.getUserName());
        user.setName(new UserResourceName(scimUser.getFirstName(), scimUser.getLastName()));
        user.setActive(true);
        user.setDisplayName(scimUser.getDisplayName());
        UserResourceEmail email = new UserResourceEmail();// Email().setType("work").setPrimary(true).setValue(scimUser.getEmail());
        email.setPrimary(true);
        email.setType("work");
        email.setValue(scimUser.getEmail());
        user.setEmails(Collections.singletonList(email));
        user.setSchemas(List.of("urn:ietf:params:scim:schemas:core:2.0:User"));
        return user;
    }

    public static UserResource convertToUserResourceForRAM(ScimUser scimUser) {
        Assert.notNull(scimUser, "scimUser can not be null!");
        UserResource user = new UserResource();
        user.setId(scimUser.getId());
        user.setExternalId(scimUser.getExternalId());
        user.setUserName(scimUser.getUserName());
        user.setDisplayName(scimUser.getDisplayName());
        user.setSchemas(List.of("urn:ietf:params:scim:schemas:core:2.0:User"));
        return user;
    }

}
