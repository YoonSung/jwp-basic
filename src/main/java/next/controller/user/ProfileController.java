package next.controller.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.annotation.Controller;
import core.annotation.RequestMapping;
import next.dao.UserDao;
import core.mvc.AbstractController;
import core.mvc.ModelAndView;

@Controller
public class ProfileController extends AbstractController {
	private UserDao userDao = UserDao.getInstance();

    @Override
    @RequestMapping(value = "/users/profile")
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        ModelAndView mav = jspView("/user/profile.jsp");
        mav.addObject("user", userDao.findByUserId(userId));
        return mav;
    }
}
