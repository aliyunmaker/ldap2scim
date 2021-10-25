package cc.landingzone.dreamweb.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.model.ScimUser;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ScimUserService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scimUser")
public class ScimUserController extends BaseController {

    @RequestMapping("/searchScimUser.do")
    public void searchScimUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String simpleSearch = request.getParameter("simpleSearch");
            if (StringUtils.isBlank(simpleSearch)) {
                simpleSearch = null;
            }
            List<ScimUser> list = ScimUserService.searchScimUser(simpleSearch);
            Collections.sort(list);
            result.setTotal(list.size());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addUser.do")
    public void addUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimUser scimUser = JsonUtils.parseObject(formString, ScimUser.class);

            scimUser.setEmail(scimUser.getUserName());
            scimUser.setDisplayName(scimUser.getFirstName() + " " + scimUser.getLastName());

            ScimUserService.addUser(scimUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateUser.do")
    public void updateUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimUser scimUser = JsonUtils.parseObject(formString, ScimUser.class);

            scimUser.setEmail(scimUser.getUserName());
            scimUser.setDisplayName(scimUser.getFirstName() + " " + scimUser.getLastName());

            ScimUserService.updateUser(scimUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteUser.do")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String id = request.getParameter("id");
            ScimUserService.deleteUser(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
