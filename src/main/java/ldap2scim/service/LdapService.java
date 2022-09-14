package ldap2scim.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import ldap2scim.common.CommonConstants;
import ldap2scim.model.ScimGroup;
import ldap2scim.model.ScimUser;
import ldap2scim.utils.JsonUtils;

/**
 * 
 * @author charles
 * @date 2021-10-19
 */
@Component
public class LdapService {

    private static Logger logger = LoggerFactory.getLogger(LdapService.class);

    private static ThreadLocal<String> TaskTraceId = new ThreadLocal<>();

    /**
     * 
     * @param searchBase
     * @param searchFilter
     * @return
     */
    public static List<Map<String, String>> searchLdapUser(String searchBase, String searchFilter) {

        if (StringUtils.isBlank(searchBase)) {
            searchBase = CommonConstants.LDAP_Searchbase;
        }
        if (StringUtils.isBlank(searchFilter)) {
            searchFilter = CommonConstants.LDAP_Searchfilter;
        }

        List<Map<String, String>> result = new ArrayList<>();
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, CommonConstants.LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, CommonConstants.LDAP_UserName);
        env.put(Context.SECURITY_CREDENTIALS, CommonConstants.LDAP_Password);
        // javax.naming.PartialResultException
        // env.put(Context.REFERRAL, "follow");
        try {
            InitialDirContext dc = new InitialDirContext(env);// 初始化上下文
            // 创建搜索控件
            SearchControls searchCtls = new SearchControls();
            // 设置搜索范围
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // searchCtls.setCountLimit(3);

            String[] returningAttrs = {CommonConstants.SCIM_ATTR_GIVEN_NAME, CommonConstants.SCIM_ATTR_FAMILY_NAME,
                CommonConstants.SCIM_ATTR_EMAIL, CommonConstants.SCIM_ATTR_EXTERNALID,
                CommonConstants.SCIM_ATTR_DISPLAYNAME, CommonConstants.SCIM_ATTR_USERNAME, "member",
                "distinguishedName", "objectClass"};
            searchCtls.setReturningAttributes(returningAttrs);
            NamingEnumeration<SearchResult> searchResults = dc.search(searchBase, searchFilter, searchCtls);
            while (searchResults.hasMore()) {
                SearchResult searchResult = searchResults.next();
                Attributes attributes = searchResult.getAttributes();
                NamingEnumeration<?> attribute = attributes.getAll();
                Map<String, String> map = new HashMap<>();
                while (attribute.hasMoreElements()) {
                    Attribute item = (Attribute)attribute.nextElement();
                    if ("objectClass".equals(item.getID())) {
                        map.put(item.getID(), item.get(item.size() - 1).toString());
                    } else if ("member".equals(item.getID())) {
                        // map.put(item.getID(), item.get(item.size() - 1).toString());
                        StringBuilder memberStringBuilder = new StringBuilder();
                        for (int i = 0; i < item.size(); i++) {
                            memberStringBuilder.append(item.get(i).toString());
                            memberStringBuilder.append("|");
                        }
                        map.put(item.getID(), memberStringBuilder.toString());
                    } else {
                        map.put(item.getID(), item.get().toString());
                    }
                }
                result.add(map);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    public static void syncLdaptoScim(List<Map<String, String>> list) {
        TaskTraceId.set("task" + System.currentTimeMillis());
        int userCount = 0;
        int groupCount = 0;
        Map<String, String> groupMemberMap = new HashMap<>();
        Map<String, String> userIdMap = new HashMap<>();
        for (Map<String, String> ldapItem : list) {
            try {
                String objectClass = ldapItem.get("objectClass");
                if ("user".equals(objectClass)) {
                    userCount++;
                    ScimUser scimUser = new ScimUser();
                    scimUser.setDisplayName(ldapItem.get(CommonConstants.SCIM_ATTR_DISPLAYNAME));
                    scimUser.setEmail(ldapItem.get(CommonConstants.SCIM_ATTR_EMAIL));
                    scimUser.setExternalId(ldapItem.get(CommonConstants.SCIM_ATTR_EXTERNALID));
                    scimUser.setFamilyName(ldapItem.get(CommonConstants.SCIM_ATTR_FAMILY_NAME));
                    scimUser.setGivenName(ldapItem.get(CommonConstants.SCIM_ATTR_GIVEN_NAME));
                    scimUser.setUserName(ldapItem.get(CommonConstants.SCIM_ATTR_USERNAME));
                    String userId = syncLdapUsertoScim(scimUser);
                    userIdMap.put(scimUser.getExternalId(), userId);
                } else if ("group".equals(objectClass)) {
                    groupCount++;
                    ScimGroup scimGroup = new ScimGroup();
                    scimGroup.setDisplayName(ldapItem.get(CommonConstants.SCIM_ATTR_DISPLAYNAME));
                    scimGroup.setExternalId(ldapItem.get(CommonConstants.SCIM_ATTR_EXTERNALID));
                    String groupId = syncLdapGrouptoScim(scimGroup);
                    String memberStr = ldapItem.get("member");
                    if (null != memberStr) {
                        groupMemberMap.put(groupId, memberStr);
                    }
                } else {
                    // only support objectClass is user or group
                    logger.info("[not support objectClass: {}]: {}", objectClass, JsonUtils.toJsonString(ldapItem));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        // 同步组成员,前置: 先在组同步的时候把组成员全部remove
        try {
            Map<String, List<String>> groupMemberResult = mergeGroupMemberMap(groupMemberMap, userIdMap);
            for (Map.Entry<String, List<String>> entry : groupMemberResult.entrySet()) {
                ScimGroupService.addMembersByGroupId(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("[{}][syncLdaptoScim] total:{},userCount:{},groupCount:{}", TaskTraceId.get(), list.size(),
            userCount, groupCount);
        TaskTraceId.remove();
    }

    public static Map<String, List<String>> mergeGroupMemberMap(Map<String, String> groupMemberMap,
        Map<String, String> userIdMap) {
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, String> entry : groupMemberMap.entrySet()) {
            String[] memberArray = entry.getValue().split("\\|");
            List<String> userIdList = new ArrayList<>();
            for (String memberDN : memberArray) {
                if (StringUtils.isNotBlank(userIdMap.get(memberDN))) {
                    userIdList.add(userIdMap.get(memberDN));
                }
            }
            if (!userIdList.isEmpty()) {
                result.put(entry.getKey(), userIdList);
            }
        }
        return result;
    }

    /**
     * 使用 userName当做查询主键
     * 
     * @param scimUser
     * @throws Exception
     */
    public static String syncLdapUsertoScim(ScimUser scimUser) throws Exception {
        Assert.notNull(scimUser, "scimUser can not be null!");
        Assert.hasText(scimUser.getUserName(), "scimUser's userName can not be blank!");
        List<ScimUser> list = ScimUserService.searchScimUser("userName eq \"" + scimUser.getUserName() + "\"", null);
        if (list.size() > 1) {
            throw new RuntimeException("find more than 1 user by userName:" + scimUser.getUserName());
        }
        String userId = null;
        // add
        if (list.isEmpty()) {
            userId = ScimUserService.addUser(scimUser);
            logger.info("[{}][user-add][{}]:{}", TaskTraceId.get(), scimUser.getUserName(),
                JsonUtils.toJsonString(scimUser));
        } else if (list.size() == 1) {
            userId = list.get(0).getId();
            // update
            scimUser.setId(userId);
            ScimUserService.updateUser(scimUser);
            logger.info("[{}][user-update][{}]:{}", TaskTraceId.get(), scimUser.getUserName(),
                JsonUtils.toJsonString(scimUser));
        }
        // 暂时不做scim端的删除
        return userId;
    }

    /**
     * 使用 displayName当做查询主键
     * 
     * @param scimGroup
     */
    public static String syncLdapGrouptoScim(ScimGroup scimGroup) throws Exception {
        Assert.notNull(scimGroup, "scimGroup can not be null!");
        Assert.hasText(scimGroup.getDisplayName(), "scimGroup's displayName can not be blank!");
        List<ScimGroup> list =
            ScimGroupService.searchScimGroup("displayName eq \"" + scimGroup.getDisplayName() + "\"", null);
        if (list.size() > 1) {
            throw new RuntimeException("find more than 1 group by displayName:" + scimGroup.getDisplayName());
        }
        String groupId = null;
        // add
        if (list.isEmpty()) {
            groupId = ScimGroupService.addGroup(scimGroup);
            logger.info("[{}][group-add][{}]:{}", TaskTraceId.get(), scimGroup.getDisplayName(),
                JsonUtils.toJsonString(scimGroup));
        } else if (list.size() == 1) {
            groupId = list.get(0).getId();
            // update
            scimGroup.setId(groupId);
            ScimGroupService.updateGroup(scimGroup);
            logger.info("[{}][group-update][{}]:{}", TaskTraceId.get(), scimGroup.getDisplayName(),
                JsonUtils.toJsonString(scimGroup));

            // remove all member from group
            ScimGroupService.removeAllMembersByGroupId(groupId);
        }
        // FIXME 暂时不做scim端的删除,会出现权限扩散的问题
        return groupId;
    }

}
