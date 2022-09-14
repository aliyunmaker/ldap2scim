package ldap2scim.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(LdapController.class);

    @RequestMapping("/searchLdapUser.do")
    public void searchLdapUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String ldapbase = request.getParameter("ldapbase");
            String ldapfilter = request.getParameter("ldapfilter");

            List<Map<String, String>> mapList = LdapService.searchLdapUser(ldapbase, ldapfilter);
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

    @RequestMapping("/syncSearch.do")
    public void syncSearch(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String ldapbase = request.getParameter("ldapbase");
            String ldapfilter = request.getParameter("ldapfilter");
            List<Map<String, String>> ldapList = LdapService.searchLdapUser(ldapbase, ldapfilter);
            LdapService.syncLdaptoScim(ldapList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/syncChoose.do")
    public void syncChoose(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String ldapbase = request.getParameter("ldapbase");
            String ldapfilter = request.getParameter("ldapfilter");
            String distinguishedNameArray = request.getParameter("distinguishedNameArray");
            List<String> distinguishedNameList = JsonUtils.parseArray(distinguishedNameArray, String.class);
            List<Map<String, String>> ldapList = LdapService.searchLdapUser(ldapbase, ldapfilter);

            List<Map<String, String>> targetList = new ArrayList<>();
            for (Map<String, String> map : ldapList) {
                if (distinguishedNameList.contains(map.get("distinguishedName"))) {
                    targetList.add(map);
                }
            }
            LdapService.syncLdaptoScim(targetList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}