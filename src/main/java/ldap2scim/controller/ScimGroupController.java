package ldap2scim.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ldap2scim.model.ScimGroup;
import ldap2scim.model.Page;
import ldap2scim.model.WebResult;
import ldap2scim.service.ScimGroupService;
import ldap2scim.utils.JsonUtils;

@Controller
@RequestMapping("/scimGroup")
public class ScimGroupController extends BaseController {

    @RequestMapping("/searchScimGroup.do")
    public void searchScimGroup(HttpServletRequest request, HttpServletResponse response) {
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
            List<ScimGroup> list = ScimGroupService.searchScimGroup(simpleSearch, page);
            result.setTotal(page.getTotal());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addGroup.do")
    public void addGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimGroup scimGroup = JsonUtils.parseObject(formString, ScimGroup.class);
            ScimGroupService.addGroup(scimGroup);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateGroup.do")
    public void updateGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimGroup scimGroup = JsonUtils.parseObject(formString, ScimGroup.class);
            ScimGroupService.updateGroup(scimGroup);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteGroup.do")
    public void deleteGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idArray = request.getParameter("idArray");
            List<String> idList = JsonUtils.parseArray(idArray, String.class);
            // String id = request.getParameter("id");
            for (String id : idList) {
                ScimGroupService.deleteGroup(id);
            }
            result.setData("删除成功!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
