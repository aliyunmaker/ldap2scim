package ldap2scim.service;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import ldap2scim.common.CommonConstants;

/**
 * ldap test
 */
public class LdapTest {

    public static void main(String[] args) {
        // testAuth();
        // testGetUserInfo();
        generateLdapUserData();
    }

    public static void generateLdapUserData() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("zhang" + i + ",san" + i + ",zhangsandisplay" + i + ",zhangsan" + i + ",Test_1234");
        }
    }

    public static void testAuth() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, CommonConstants.LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, CommonConstants.LDAP_UserName);
        env.put(Context.SECURITY_CREDENTIALS, CommonConstants.LDAP_Password);
        try {
            new InitialDirContext(env);// 初始化上下文
            System.out.println("认证成功");
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("认证出错：" + e);
        }
    }

    public static void testGetUserInfo() {
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
            System.out.println("认证成功");
            // 创建搜索控件
            SearchControls searchCtls = new SearchControls();
            // 设置搜索范围
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // 设置搜索过滤条件
            String searchFilter = "(objectClass=user)";
            // String searchFilter = "(objectClass=*)";
            // 设置搜索域节点
            // String searchBase = "dc=landingzone,dc=cc";
            // String searchBase = "ou=hangzhou,dc=landingzone,dc=cc";
            String searchBase = "dc=test,dc=com";
            // 定制返回属性,不定制属性，返回所有的属性集
            String[] returningAttrs = {"url", "whenChanged", "employeeID", "name", "userPrincipalName",
                "physicalDeliveryOfficeName", "departmentNumber", "telephoneNumber", "homePhone", "mobile",
                "department", "sAMAccountName", "whenChanged", "mail", "givenname", "sn", "uid"};
            searchCtls.setReturningAttributes(returningAttrs);
            try {
                NamingEnumeration<SearchResult> searchResults = dc.search(searchBase, searchFilter, searchCtls);
                while (searchResults.hasMore()) {
                    SearchResult result = searchResults.next();
                    System.out.println("-----------------");
                    // String dn = result.getName();
                    // System.out.println("\n" + dn);
                    // System.out.println(result.getName());
                    Attributes attributes = result.getAttributes();
                    System.out.println("===================");
                    NamingEnumeration<?> attribute = attributes.getAll();
                    while (attribute.hasMore()) {
                        Attribute attr = (Attribute)attribute.next();
                        System.out.println(attr.getID() + "=" + attr.get());
                    }
                }
            } catch (Exception e) {
                System.err.println("Throw Exception : " + e);
            }
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("认证出错：" + e);
        }
    }

}
