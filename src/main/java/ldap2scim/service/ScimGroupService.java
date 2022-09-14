package ldap2scim.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import ldap2scim.common.CommonConstants;
import ldap2scim.model.Page;
import ldap2scim.model.ScimGroup;
import ldap2scim.model.ScimGroupResponse;
import ldap2scim.utils.JsonUtils;
import ldap2scim.utils.OkHttpClientUtils;

public class ScimGroupService {

    // private static Logger logger = LoggerFactory.getLogger(ScimGroupService.class);

    public static final String ScimGroupsURL = CommonConstants.SCIM_URL + "/Groups";

    private static final Map<String, String> AuthHeader = new HashMap<>();

    static {
        AuthHeader.put("Authorization", "Bearer " + CommonConstants.SCIM_KEY);
    }

    public static void main(String[] args) throws Exception {
        ScimGroup group = new ScimGroup();
        group.setDisplayName("groupTest");
        group.setExternalId("groupID-111");
        System.out.println(addGroup(group));
    }

    public static List<ScimGroup> searchScimGroup(String filter, Page page) throws Exception {
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
        ScimGroupResponse scimGroupResponse = JsonUtils.parseObject(result, ScimGroupResponse.class);
        List<ScimGroup> list = scimGroupResponse.getResources();
        if (null != page) {
            page.setTotal(scimGroupResponse.getTotalResults());
        }
        if (null == list) {
            return new ArrayList<>();
        }
        return list;
    }

    public static String addGroup(ScimGroup scimGroup) throws Exception {
        Assert.notNull(scimGroup, "scimGroup can not be null!");
        scimGroup.setSchemas(List.of("urn:ietf:params:scim:schemas:core:2.0:Group"));
        String result = OkHttpClientUtils.post(ScimGroupsURL, AuthHeader, JsonUtils.toJsonStringDefault(scimGroup));
        ScimGroup addedScimGroup = JsonUtils.parseObject(result, ScimGroup.class);
        return addedScimGroup.getId();
    }

    public static void updateGroup(ScimGroup scimGroup) throws Exception {
        Assert.notNull(scimGroup, "scimGroup can not be null!");
        Assert.hasText(scimGroup.getId(), "id can not be blank!");
        String id = scimGroup.getId();
        // user.setId(null);
        scimGroup.setSchemas(List.of("urn:ietf:params:scim:schemas:core:2.0:Group"));
        OkHttpClientUtils.put(ScimGroupsURL + "/" + id, AuthHeader, JsonUtils.toJsonStringDefault(scimGroup));

    }

    public static void deleteGroup(String id) throws Exception {
        Assert.hasText(id, "id can not be blank!");
        OkHttpClientUtils.delete(ScimGroupsURL + "/" + id, AuthHeader);
    }

    public static void removeAllMembersByGroupId(String groupId) throws Exception {
        Assert.hasText(groupId, "groupId can not be blank!");
        String jsonBody =
            "{\"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\"Operations\": [{\"op\": \"remove\",\"path\": \"members\"}]}";
        OkHttpClientUtils.patch(ScimGroupsURL + "/" + groupId, AuthHeader, jsonBody);
    }

    public static void addMembersByGroupId(String groupId, List<String> userIdList) throws Exception {
        Assert.hasText(groupId, "groupId can not be blank!");
        List<Map<String, String>> mapList = new ArrayList<>();
        for (String userId : userIdList) {
            Map<String, String> map = new HashMap<>();
            map.put("value", userId);
            mapList.add(map);
        }
        String values = JsonUtils.toJsonString(mapList);
        String jsonBody =
            "{\"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\"Operations\": [{\"name\": \"addMember\",\"op\": \"add\",\"path\": \"members\",\"value\": "
                + values + "}]}";
        OkHttpClientUtils.patch(ScimGroupsURL + "/" + groupId, AuthHeader, jsonBody);
    }

}
