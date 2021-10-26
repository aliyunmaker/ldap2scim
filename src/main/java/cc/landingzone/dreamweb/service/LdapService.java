package cc.landingzone.dreamweb.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.model.ScimUser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author charles
 * @date 2021-10-19
 */
@Component
public class LdapService {

    private Logger logger = LoggerFactory.getLogger(LdapService.class);

    /**
     * 获取所有API账号
     *
     * @return
     */
    public List<ScimUser> searchLdapUser(String searchBase, String searchFilter) {

        if (StringUtils.isBlank(searchBase)) {
            searchBase = CommonConstants.LDAP_Searchbase;
        }
        if (StringUtils.isBlank(searchFilter)) {
            searchFilter = CommonConstants.LDAP_Searchfilter;
        }

        List<ScimUser> scimUserList = new ArrayList<>();
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
            logger.info("auth success");
            // 创建搜索控件
            SearchControls searchCtls = new SearchControls();
            // 设置搜索范围
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // 设置搜索过滤条件
            // String searchFilter = "(objectClass=user)";
            // String searchFilter = "(objectClass=*)";
            // 设置搜索域节点
            // String searchBase = "dc=landingzone,dc=cc";
            // String searchBase = CommonConstants.LDAP_Searchbase;
            // 定制返回属性,不定制属性，返回所有的属性集
            // String[] returningAttrs = { "url", "whenChanged", "employeeID", "name",
            // "userPrincipalName",
            // "physicalDeliveryOfficeName", "departmentNumber", "telephoneNumber",
            // "homePhone", "mobile",
            // "department", "sAMAccountName", "whenChanged", "mail", "givenname", "sn" };

            String[] returningAttrs = { CommonConstants.LDAP_ATTR_FIRSTNAME, CommonConstants.LDAP_ATTR_LASTNAME,
                    CommonConstants.LDAP_ATTR_EMAIL, CommonConstants.LDAP_ATTR_EXTERNALID,
                    CommonConstants.LDAP_ATTR_DISPLAYNAME, CommonConstants.LDAP_ATTR_USERNAME };
            searchCtls.setReturningAttributes(returningAttrs);
            try {
                NamingEnumeration<SearchResult> searchResults = dc.search(searchBase, searchFilter, searchCtls);
                while (searchResults.hasMore()) {
                    ScimUser scimUser = new ScimUser();
                    SearchResult result = searchResults.next();
                    Attributes attributes = result.getAttributes();
                    if (null == attributes.get(CommonConstants.LDAP_ATTR_FIRSTNAME)) {
                        scimUser.setFirstName(" ");
                    } else {
                        scimUser.setFirstName(attributes.get(CommonConstants.LDAP_ATTR_FIRSTNAME).get().toString());
                    }
                    if (null == attributes.get(CommonConstants.LDAP_ATTR_LASTNAME)) {
                        scimUser.setLastName(" ");
                    } else {
                        scimUser.setLastName(attributes.get(CommonConstants.LDAP_ATTR_LASTNAME).get().toString());
                    }
                    if (null == attributes.get(CommonConstants.LDAP_ATTR_EXTERNALID)) {
                        scimUser.setExternalId(" ");
                    } else {
                        scimUser.setExternalId(attributes.get(CommonConstants.LDAP_ATTR_EXTERNALID).get().toString());
                    }
                    if (null == attributes.get(CommonConstants.LDAP_ATTR_USERNAME)) {
                        scimUser.setUserName(" ");
                    } else {
                        scimUser.setUserName(attributes.get(CommonConstants.LDAP_ATTR_USERNAME).get().toString());
                    }
                    if (null == attributes.get(CommonConstants.LDAP_ATTR_DISPLAYNAME)) {
                        scimUser.setDisplayName(" ");
                    } else {
                        scimUser.setDisplayName(attributes.get(CommonConstants.LDAP_ATTR_DISPLAYNAME).get().toString());
                    }
                    if (null == attributes.get(CommonConstants.LDAP_ATTR_EMAIL)) {
                        scimUser.setEmail(" ");
                    } else {
                        scimUser.setEmail(attributes.get(CommonConstants.LDAP_ATTR_EMAIL).get().toString());
                    }
                    scimUserList.add(scimUser);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return scimUserList;
    }

}
