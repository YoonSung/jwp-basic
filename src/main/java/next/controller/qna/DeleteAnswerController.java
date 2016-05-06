package next.controller.qna;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.annotation.Controller;
import core.annotation.RequestMapping;
import core.annotation.RequestMethod;
import next.dao.AnswerDao;
import next.model.Result;
import core.jdbc.DataAccessException;
import core.mvc.AbstractController;
import core.mvc.ModelAndView;

@Controller
public class DeleteAnswerController extends AbstractController {
	private AnswerDao answerDao = AnswerDao.getInstance();

	@Override
	@RequestMapping(value = "/api/qna/deleteAnswer", method = RequestMethod.DELETE)
	public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long answerId = Long.parseLong(request.getParameter("answerId"));
        
		ModelAndView mav = jsonView();
		try {
			answerDao.delete(answerId);
			mav.addObject("result", Result.ok());
		} catch (DataAccessException e) {
			mav.addObject("result", Result.fail(e.getMessage()));
		}
		return mav;
	}
}
