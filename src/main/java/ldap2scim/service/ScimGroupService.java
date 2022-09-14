package ldap2scim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import ldap2scim.common.CommonConstants;
import ldap2scim.model.GroupResource;
import ldap2scim.model.Page;
import ldap2scim.model.ScimGroupResult;
import ldap2scim.utils.JsonUtils;
import ldap2scim.utils.OkHttpClientUtils;

public class ScimGroupService {

    private static Logger logger = LoggerFactory.getLogger(ScimGroupService.class);

    public static final String ScimGroupsURL = CommonConstants.SCIM_URL + "/Groups";

    private static final Map<String, String> AuthHeader = new HashMap<>();

    static {
        AuthHeader.put("Authorization", "Bearer " + CommonConstants.SCIM_KEY);
    }

    public static List<GroupResource> searchScimGroup(String filter, Page page) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (null != page) {
            // FIXME 服务端有bug待修复,startIndex先当做page使用
            params.put("startIndex", String.valueOf(page.getPage()));
            params.put("count", String.valueOf(page.getLimit()));
        }
        if (StringUtils.isNotBlank(filter)) {
            params.put("filter", filter);
        }
        String result = OkHttpClientUtils.get(ScimGroupsURL, params, AuthHeader);
        ScimGroupResult scimGroupResult = JsonUtils.parseObject(result, ScimGroupResult.class);
        List<GroupResource> list = scimGroupResult.getResources();
        page.setTotal(scimGroupResult.getTotalResults());
        logger.info("filter:" + filter);
        logger.info("==================================");
        logger.info(String.valueOf(list.size()));
        // return list;
        return list;
    }

    public static void addGroup(GroupResource groupResource) throws Exception {
        Assert.notNull(groupResource, "groupResource can not be null!");
        groupResource.setSchemas(List.of("urn:ietf:params:scim:schemas:core:2.0:Group"));
        OkHttpClientUtils.post(ScimGroupsURL, AuthHeader, JsonUtils.toJsonStringDefault(groupResource));
    }

    public static void updateGroup(GroupResource groupResource) throws Exception {
        Assert.notNull(groupResource, "groupResource can not be null!");
        Assert.hasText(groupResource.getId(), "id can not be blank!");
        String id = groupResource.getId();
        // user.setId(null);
        groupResource.setSchemas(List.of("urn:ietf:params:scim:schemas:core:2.0:Group"));
        OkHttpClientUtils.put(ScimGroupsURL + "/" + id, AuthHeader, JsonUtils.toJsonStringDefault(groupResource));

    }

    public static void deleteGroup(String id) throws Exception {
        Assert.hasText(id, "id can not be blank!");
        OkHttpClientUtils.delete(ScimGroupsURL + "/" + id, AuthHeader);
    }

}
