package ldap2scim.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ldap2scim.common.CommonConstants;
import ldap2scim.model.WebResult;

@Controller
@RequestMapping("/system")
public class SystemController extends BaseController implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        CommonConstants.loadProperties();
    }

    @RequestMapping("/getIndexLogoPage.do")
    public void getIndexLogoPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WebResult result = new WebResult();
        try {
            String username = "welcome";
            String logoDiv = null;
            String version = "LDAP to SCIM";
            logoDiv =
                "<div align=\"center\"><i style=\"font-size:30px;margin-top:5px;color:#CFDEEF;animation-duration: 1s;\" class=\"fa fa-sun-o fa-spin\" aria-hidden=\"true\"></i></div><div align='center' style='background-color:rgb(93,168,48);margin-top:5px;font-size: 12px;"
                    + "'><font style='color: white;'>" + username + "<br>" + version + "</font></div>";
            result.setData(logoDiv);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
