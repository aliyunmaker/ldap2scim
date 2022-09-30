package ldap2scim.service;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import ldap2scim.common.CommonConstants;

public class LdapPagedSearchTest {

    public static void main(String[] args) {

        String searchBase = CommonConstants.LDAP_Searchbase;
        String searchFilter = CommonConstants.LDAP_Searchfilter;

        Hashtable<String, Object> env = new Hashtable<String, Object>(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, CommonConstants.LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, CommonConstants.LDAP_UserName);
        env.put(Context.SECURITY_CREDENTIALS, CommonConstants.LDAP_Password);

        try {
            LdapContext ctx = new InitialLdapContext(env, null);

            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String[] returningAttrs = {CommonConstants.SCIM_ATTR_GIVEN_NAME, CommonConstants.SCIM_ATTR_FAMILY_NAME,
                CommonConstants.SCIM_ATTR_EMAIL, CommonConstants.SCIM_ATTR_EXTERNALID,
                CommonConstants.SCIM_ATTR_DISPLAYNAME, CommonConstants.SCIM_ATTR_USERNAME, "member",
                "distinguishedName", "objectClass"};
            searchCtls.setReturningAttributes(returningAttrs);
            // Activate paged results
            int pageSize = 200;
            byte[] cookie = null;
            ctx.setRequestControls(new Control[] {new PagedResultsControl(pageSize, Control.NONCRITICAL)});
            int total;

            do {
                /* perform the search */
                NamingEnumeration<?> results = ctx.search(searchBase, searchFilter, searchCtls);

                /* for each entry print out name + all attrs and values */
                while (results != null && results.hasMore()) {
                    SearchResult entry = (SearchResult)results.next();
                    System.out.println(entry.getName());
                }

                // Examine the paged results control response
                Control[] controls = ctx.getResponseControls();
                if (controls != null) {
                    for (int i = 0; i < controls.length; i++) {
                        if (controls[i] instanceof PagedResultsResponseControl) {
                            PagedResultsResponseControl prrc =(PagedResultsResponseControl)controls[i];
                            total = prrc.getResultSize();
                            if (total != 0) {
                                System.out.println("***************** END-OF-PAGE " +
                                    "(total : " + total +
                                    ") *****************\n");
                            } else {
                                System.out.println("***************** END-OF-PAGE " +
                                    "(total: unknown) ***************\n");
                            }
                            cookie = prrc.getCookie();
                            System.out.println(new String(cookie));
                        }
                    }
                } else {
                    System.out.println("No controls were sent from the server");
                }
                // Re-activate paged results
                ctx.setRequestControls(new Control[] {
                    new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});

            } while (cookie != null);

            ctx.close();

        } catch (NamingException e) {
            System.err.println("PagedSearch failed.");
            e.printStackTrace();
        } catch (IOException ie) {
            System.err.println("PagedSearch failed.");
            ie.printStackTrace();
        }
    }

}
