package ldap2scim.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ldap2scim.model.Page;
import ldap2scim.model.ScimUser;
import ldap2scim.model.WebResult;
import ldap2scim.service.ScimUserService;
import ldap2scim.utils.JsonUtils;

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
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Integer pageNum = Integer.valueOf(request.getParameter("page"));
            Page page = new Page(start, limit, pageNum);
            List<ScimUser> list = ScimUserService.searchScimUser(simpleSearch, page);
            // Collections.sort(list);
            result.setTotal(page.getTotal());
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
            String idArray = request.getParameter("idArray");
            List<String> idList = JsonUtils.parseArray(idArray, String.class);
            // String id = request.getParameter("id");
            if (idList.size() <= 20) {
                for (String id : idList) {
                    ScimUserService.deleteUser(id);
                }
                result.setData("删除成功!");
            } else {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        int deleteCount = 0;
                        try {
                            for (String id : idList) {
                                Thread.sleep(10);
                                ScimUserService.deleteUser(id);
                                deleteCount++;
                                logger.info("delete success[" + deleteCount + "]:" + id);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        } finally {
                            logger.info("expect delete count: " + idList.size());
                            logger.info("actual delete count: " + deleteCount);
                        }

                    }
                }, "deleteSCIMUser" + System.currentTimeMillis()).start();
                result.setData("后台删除中,请查看后台日志!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
