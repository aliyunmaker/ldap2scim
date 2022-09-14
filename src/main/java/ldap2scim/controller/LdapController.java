package ldap2scim.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ldap2scim.model.WebResult;
import ldap2scim.service.LdapService;
import ldap2scim.utils.JsonUtils;

/**
 * ldap
 * 
 * @author charles
 * @date 2021-10-19
 */
@Controller
@RequestMapping("/ldap")
public class LdapController extends BaseController {

    @Autowired
    private LdapService ldapService;

    private Logger logger = LoggerFactory.getLogger(LdapController.class);

    @RequestMapping("/searchLdapUser.do")
    public void searchLdapUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String ldapbase = request.getParameter("ldapbase");
            String ldapfilter = request.getParameter("ldapfilter");

            List<Map<String, String>> mapList = ldapService.searchLdapUser(ldapbase, ldapfilter);
            List<Map<String, String>> list = new ArrayList<>();
            for (Map<String, String> map : mapList) {
                Map<String, String> itemMap = new HashMap<>();
                itemMap.put("distinguishedName", map.remove("distinguishedName"));
                itemMap.put("objectClass", map.remove("objectClass"));
                itemMap.put("jsonString", JsonUtils.toJsonString(map));
                list.add(itemMap);
            }
            result.setTotal(list.size());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/syncUser.do")
    public void syncUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // try {
        // String ldapbase = request.getParameter("ldapbase");
        // String ldapfilter = request.getParameter("ldapfilter");
        // // cloudsso
        // List<ScimUser> cloudssoUserList = ScimUserService.searchScimUser(null, null);
        // Map<String, ScimUser> cloudssoMap = new HashMap<>();
        // for (ScimUser scimUser : cloudssoUserList) {
        // cloudssoMap.put(scimUser.getEmail(), scimUser);
        // }
        //
        // List<ScimUser> list = ldapService.searchLdapUser(ldapbase, ldapfilter);
        // String emails = request.getParameter("emails");
        // List<String> emailList = JsonUtils.parseArray(emails, String.class);
        // for (ScimUser scimUser : list) {
        // if (emailList.contains(scimUser.getEmail())) {
        // if (cloudssoMap.get(scimUser.getEmail()) == null) {
        // ScimUserService.addUser(scimUser);
        // logger.info("add success,email:" + scimUser.getEmail());
        // } else {
        // scimUser.setId(cloudssoMap.get(scimUser.getEmail()).getId());
        // ScimUserService.updateUser(scimUser);
        // logger.info("update success,email:" + scimUser.getEmail());
        // }
        //
        // }
        // }
        // } catch (Exception e) {
        // logger.error(e.getMessage(), e);
        // result.setSuccess(false);
        // result.setErrorMsg(e.getMessage());
        // }
        outputToJSON(response, result);
    }

}