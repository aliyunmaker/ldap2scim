package ldap2cloudsso.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.springframework.util.Assert;

import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.types.UserResource;

import ldap2cloudsso.common.CommonConstants;
import ldap2cloudsso.model.ScimUser;

public class ScimUserService {

    private final static String SCIM_SERVER_URL_ALIYUN_CLOUDSSO =
        "https://cloudsso-scim-cn-shanghai.aliyun.com/scim/v2";
    static {
        scimService = buildScimService(SCIM_SERVER_URL_ALIYUN_CLOUDSSO, CommonConstants.KEY_ALIYUN_CLOUDSSO);
    }

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(5);

    public static void main(String[] args) throws Exception {
        // List<ScimUser> list = searchScimUser(null);
        // String result = JsonUtils.toJsonString(list);
        // System.out.println(list.size());

        for (int i = 1; i <= 1100; i++) {
            String suffix = StringUtils.leftPad("" + i, 4, '0');
            ScimUser scimUser = new ScimUser();
            scimUser.setFirstName("counttest" + suffix);
            scimUser.setLastName("counttest" + suffix);
            scimUser.setUserName("testcount" + suffix + "@chengchao.name");
            scimUser.setExternalId("testcount+" + suffix + "@chengchao.name");
            addUser(scimUser);
            Thread.sleep(10);
            System.out.println("done:" + i);
        }
    }

    private static ScimService scimService;

    public static List<ScimUser> searchScimUser(String filter) throws Exception {

        List<UserResource> userList = new ArrayList<>();
        int maxIndex = 1;
        int startIndex = 1;
        final int count = 100;
        // CloudSSO默认quota为一千条
        while (startIndex <= maxIndex) {
            RATE_LIMITER.acquire(1);
            ListResponse<UserResource> list = scimService.searchRequest("Users").filter(filter).page(startIndex, count)
                .invoke(UserResource.class);
            int totalResult = list.getTotalResults();
            if (totalResult % count == 0) {
                maxIndex = totalResult / count;
            } else {
                maxIndex = totalResult / count + 1;
            }
            List<UserResource> tmp = list.getResources();
            userList.addAll(tmp);
            startIndex = startIndex + 1;
        }
        return convertToScimUser(userList);
    }

    public static void addUser(ScimUser scimUser) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.notNull(scimUser, "scimUser can not be null!");
        Assert.hasText(scimUser.getUserName(), "username can not be blank!");
        Assert.hasText(scimUser.getExternalId(), "externalId can not be blank!");
        UserResource user = convertToUserResource(scimUser);
        scimService.create("Users", user);
    }

    public static void updateUser(ScimUser scimUser) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.notNull(scimUser, "scimUser can not be null!");
        Assert.hasText(scimUser.getUserName(), "username can not be blank!");
        Assert.hasText(scimUser.getExternalId(), "externalId can not be blank!");
        Assert.hasText(scimUser.getId(), "id can not be blank!");
        UserResource user = convertToUserResource(scimUser);

        Meta meta = new Meta();
        meta.setLocation(new URI("/Users/" + scimUser.getId()));
        user.setMeta(meta);

        scimService.replace(user);
    }

    public static void deleteUser(String id) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.hasText(id, "id can not be blank!");
        scimService.delete("Users", id);
    }

    public static ScimService buildScimService(String url, String oauthKey) {
        Assert.hasText(url, "url can not be blank!");
        Assert.hasText(oauthKey, "oauthKey can not be blank!");
        Client client = ClientBuilder.newClient().register(OAuth2ClientSupport.feature(oauthKey));
        WebTarget target = client.target(url);
        ScimService scimService = new ScimService(target);
        return scimService;

    }

    public static List<ScimUser> convertToScimUser(List<UserResource> userResourceList) {
        // Assert.notEmpty(userResourceList, "userResourceList can not be null!");
        if (userResourceList == null || userResourceList.size() == 0) {
            return new ArrayList<ScimUser>();
        }
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
        Name name = new Name().setGivenName(scimUser.getFirstName()).setFamilyName(scimUser.getLastName());
        user.setName(name);
        user.setActive(true);
        user.setDisplayName(scimUser.getDisplayName());
        Email email = new Email().setType("work").setPrimary(true).setValue(scimUser.getEmail());
        user.setEmails(Collections.singletonList(email));
        // cloudsso 不支持通过scim写入password
        // user.setPassword(user.getPassword());
        return user;
    }

}
