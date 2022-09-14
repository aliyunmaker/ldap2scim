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

import ldap2scim.common.CommonConstants;

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
    public List<Map<String, String>> searchLdapUser(String searchBase, String searchFilter) {

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
            logger.info("auth success");
            // 创建搜索控件
            SearchControls searchCtls = new SearchControls();
            // 设置搜索范围
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // searchCtls.setCountLimit(3);

            String[] returningAttrs = {CommonConstants.SCIM_ATTR_GIVEN_NAME, CommonConstants.SCIM_ATTR_FAMILY_NAME,
                CommonConstants.SCIM_ATTR_EMAIL, CommonConstants.SCIM_ATTR_EXTERNALID,
                CommonConstants.SCIM_ATTR_DISPLAYNAME, CommonConstants.SCIM_ATTR_USERNAME, "member",
                "distinguishedName", "uniqueMember", "objectClass", "mail"};
            searchCtls.setReturningAttributes(returningAttrs);
            try {
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
                        } else {
                            map.put(item.getID(), item.get().toString());
                        }
                        // System.out.println("=="+item);
                    }
                    result.add(map);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

}
