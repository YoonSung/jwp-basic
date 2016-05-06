package next.controller.qna;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.annotation.Controller;
import core.annotation.RequestMapping;
import core.annotation.RequestMethod;
import next.controller.UserSessionUtils;
import next.dao.QuestionDao;
import next.model.Question;
import next.model.User;
import core.mvc.AbstractController;
import core.mvc.ModelAndView;

@Controller
public class CreateQuestionController extends AbstractController {
	private QuestionDao questionDao = QuestionDao.getInstance();
	
	@Override
	@RequestMapping(value = "/qna/create", method = RequestMethod.POST)
	public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	if (!UserSessionUtils.isLogined(request.getSession())) {
			return jspView("redirect:/users/loginForm");
		}
    	User user = UserSessionUtils.getUserFromSession(request.getSession());
    	Question question = new Question(user.getUserId(), request.getParameter("title"), request.getParameter("contents"));
    	questionDao.insert(question);
		return jspView("redirect:/");
	}

}
