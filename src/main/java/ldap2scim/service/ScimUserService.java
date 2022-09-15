package ldap2scim.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.google.common.util.concurrent.RateLimiter;

import ldap2scim.common.CommonConstants;
import ldap2scim.model.Page;
import ldap2scim.model.ScimUser;
import ldap2scim.model.ScimUserResource;
import ldap2scim.model.ScimUserResourceEmail;
import ldap2scim.model.ScimUserResourceName;
import ldap2scim.model.ScimUserResponse;
import ldap2scim.utils.JsonUtils;
import ldap2scim.utils.OkHttpClientUtils;

public class ScimUserService {

    public static final String ScimUsersURL = CommonConstants.SCIM_URL + "/Users";
    private static final Map<String, String> AuthHeader = new HashMap<>();

    static {
        AuthHeader.put("Authorization", "Bearer " + CommonConstants.SCIM_KEY);
    }

    // private static Logger logger = LoggerFactory.getLogger(ScimUserService.class);

    public static final RateLimiter RATE_LIMITER = RateLimiter.create(5);

    public static void main(String[] args) throws Exception {
        ScimUser scimUser = new ScimUser();
        scimUser.setGivenName("二");
        scimUser.setFamilyName("程");
        scimUser.setUserName("cheng333");
        scimUser.setEmail("cheng333@landingzone.cc");
        scimUser.setExternalId("33CN=程二,OU=hangzhou,DC=landingzone,DC=cc");
        addUser(scimUser);
    }

    public static void main2(String[] args) throws Exception {
        for (int i = 15; i <= 30; i++) {
            String suffix = StringUtils.leftPad("" + i, 4, '0');
            ScimUser scimUser = new ScimUser();
            scimUser.setGivenName("givenName" + suffix);
            scimUser.setFamilyName("familyName" + suffix);
            scimUser.setUserName("userName" + suffix);
            scimUser.setExternalId("externalId+" + suffix + "@chengchao.name");
            addUser(scimUser);
            Thread.sleep(10);
            System.out.println("done:" + i);
        }
    }

    public static List<ScimUser> searchScimUser(String filter, Page page) throws Exception {
        RATE_LIMITER.acquire(1);
        Map<String, String> params = new HashMap<>();
        if (null != page) {
            // FIXME 服务端有bug待修复,startIndex先当做page使用
            params.put("startIndex", String.valueOf(page.getPage()));
            params.put("count", String.valueOf(page.getLimit()));
        }
        if (StringUtils.isNotBlank(filter)) {
            params.put("filter", filter);
        }
        String result = OkHttpClientUtils.get(ScimUsersURL, params, AuthHeader);
        ScimUserResponse scimUserResponse = JsonUtils.parseObject(result, ScimUserResponse.class);
        List<ScimUserResource> list = scimUserResponse.getResources();
        if (null != page) {
            page.setTotal(scimUserResponse.getTotalResults());
        }
        // logger.info("filter:" + filter);
        // logger.info("==================================");
        // logger.info(String.valueOf(list.size()));
        return convertToScimUserList(list);
    }

    public static String addUser(ScimUser scimUser) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.notNull(scimUser, "scimUser can not be null!");
        ScimUserResource user = convertToUserResource(scimUser);
        String result = OkHttpClientUtils.post(ScimUsersURL, AuthHeader, JsonUtils.toJsonStringDefault(user));
        ScimUser addedScimUser = JsonUtils.parseObject(result, ScimUser.class);
        return addedScimUser.getId();
    }

    public static void updateUser(ScimUser scimUser) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.notNull(scimUser, "scimUser can not be null!");
        Assert.hasText(scimUser.getId(), "id can not be blank!");
        ScimUserResource user = convertToUserResource(scimUser);
        String id = user.getId();
        // user.setId(null);
        OkHttpClientUtils.put(ScimUsersURL + "/" + id, AuthHeader, JsonUtils.toJsonStringDefault(user));
    }

    public static void deleteUser(String id) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.hasText(id, "id can not be blank!");
        OkHttpClientUtils.delete(ScimUsersURL + "/" + id, AuthHeader);
    }

    public static List<ScimUser> convertToScimUserList(List<ScimUserResource> userResourceList) {
        if (userResourceList == null || userResourceList.isEmpty()) {
            return new ArrayList<>();
        }
        Assert.notEmpty(userResourceList, "userResourceList can not be null!");
        List<ScimUser> list = new ArrayList<ScimUser>();
        for (ScimUserResource userResource : userResourceList) {
            list.add(convertToScimUser(userResource));
        }
        return list;
    }

    public static List<ScimUserResource> convertToUserResourceList(List<ScimUser> scimUserList) {
        Assert.notEmpty(scimUserList, "scimUserList can not be null!");
        List<ScimUserResource> list = new ArrayList<ScimUserResource>();
        for (ScimUser scimUser : scimUserList) {
            list.add(convertToUserResource(scimUser));
        }
        return list;
    }

    public static ScimUser convertToScimUser(ScimUserResource userResource) {
        Assert.notNull(userResource, "userResource can not be null!");

        ScimUser scimUser = new ScimUser();
        scimUser.setId(userResource.getId());
        scimUser.setExternalId(userResource.getExternalId());
        scimUser.setUserName(userResource.getUserName());
        scimUser.setDisplayName(userResource.getDisplayName());
        if (userResource.getName() != null) {
            scimUser.setGivenName(userResource.getName().getGivenName());
            scimUser.setFamilyName(userResource.getName().getFamilyName());
        }
        if (userResource.getEmails() != null && userResource.getEmails().size() > 0) {
            scimUser.setEmail(userResource.getEmails().get(0).getValue());
        }
        return scimUser;
    }

    public static ScimUserResource convertToUserResource(ScimUser scimUser) {
        Assert.notNull(scimUser, "scimUser can not be null!");
        ScimUserResource user = new ScimUserResource();
        user.setId(scimUser.getId());
        user.setExternalId(scimUser.getExternalId());
        user.setUserName(scimUser.getUserName());
        user.setName(new ScimUserResourceName(scimUser.getFamilyName(), scimUser.getGivenName()));
        user.setActive(true);
        user.setDisplayName(scimUser.getDisplayName());
        ScimUserResourceEmail email = new ScimUserResourceEmail();// Email().setType("work").setPrimary(true).setValue(scimUser.getEmail());
        email.setPrimary(true);
        email.setType("work");
        email.setValue(scimUser.getEmail());
        user.setEmails(Collections.singletonList(email));
        user.setSchemas(List.of("urn:ietf:params:scim:schemas:core:2.0:User"));
        return user;
    }

}
