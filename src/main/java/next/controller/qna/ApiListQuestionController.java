package next.controller.qna;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.annotation.Controller;
import core.annotation.RequestMapping;
import core.mvc.AbstractController;
import core.mvc.ModelAndView;
import next.dao.QuestionDao;

@Controller
public class ApiListQuestionController extends AbstractController {
	private QuestionDao questionDao = QuestionDao.getInstance();
	
	@Override
	@RequestMapping(value = "/api/qna/list")
	public ModelAndView execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return jsonView().addObject("questions", questionDao.findAll());
	}
}
