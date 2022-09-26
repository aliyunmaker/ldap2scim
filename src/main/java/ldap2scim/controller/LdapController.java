package ldap2scim.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ldap2scim.common.CommonConstants;
import ldap2scim.model.TaskRecord;
import ldap2scim.model.WebResult;
import ldap2scim.service.LdapService;
import ldap2scim.task.Ldap2ScimTask;
import ldap2scim.utils.JsonUtils;
import ldap2scim.utils.UUIDUtils;

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
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        List<Map<String, String>> ldapList = LdapService.searchLdapUser(ldapbase, ldapfilter);
                        TaskRecord taskRecord = new TaskRecord();
                        String uuid = UUIDUtils.generateUUID();
                        taskRecord.setUuid("syncSearch_" + uuid);
                        taskRecord.setExecuteTime(LocalDateTime.now().format(CommonConstants.DateTimeformatter));
                        String taskResult = LdapService.syncLdaptoScim(ldapList);
                        taskRecord.setResult(taskResult);
                        Ldap2ScimTask.taskRecords.add(taskRecord);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }, "syncSearch" + System.currentTimeMillis()).start();
            result.setData("后台同步中,请查看后台日志!");

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

    @RequestMapping("/getSearchParams.do")
    public void getSearchParams(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("ldapbase", CommonConstants.LDAP_Searchbase);
            map.put("ldapfilter", CommonConstants.LDAP_Searchfilter);
            result.setData(map);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getTaskInfo.do")
    public void getTaskInfo(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            StringBuilder infoDiv = new StringBuilder();
            infoDiv.append("定时任务启用:" + CommonConstants.SCIM_SYNC_CRON_ENABLED);
            infoDiv.append("<br/>");
            infoDiv.append("定时任务表达式:" + CommonConstants.SCIM_SYNC_CRON_EXPRESSION);
            infoDiv.append("<br/>");
            infoDiv.append("LDAP Searchbase:" + CommonConstants.LDAP_Searchbase);
            infoDiv.append("<br/>");
            infoDiv.append("LDAP Searchfilter:" + CommonConstants.LDAP_Searchfilter);
            infoDiv.append("<br/>");
            infoDiv.append("<br/>");

            if (CommonConstants.SCIM_SYNC_CRON_ENABLED
                && StringUtils.isNotBlank(CommonConstants.SCIM_SYNC_CRON_EXPRESSION)) {
                CronExpression cronExpression = CronExpression.parse(CommonConstants.SCIM_SYNC_CRON_EXPRESSION);
                infoDiv.append(
                    "下次运行时间:" + cronExpression.next(LocalDateTime.now()).format(CommonConstants.DateTimeformatter));
                infoDiv.append("<br/>");
            }

            result.setData(infoDiv.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getTaskRecords.do")
    public void getTaskRecords(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            result.setTotal(Ldap2ScimTask.taskRecords.size());
            result.setData(Ldap2ScimTask.taskRecords);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}