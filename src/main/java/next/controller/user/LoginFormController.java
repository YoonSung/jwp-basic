package next.controller.user;

import core.annotation.Controller;
import core.annotation.RequestMapping;
import core.mvc.ForwardController;
import core.mvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginFormController extends ForwardController {

    public LoginFormController() {
        super("/user/login.jsp");
    }

    @RequestMapping(value = "/users/loginForm")
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.execute(request, response);
    }
}
