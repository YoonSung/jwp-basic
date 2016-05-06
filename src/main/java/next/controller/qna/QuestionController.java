package next.controller.qna;

import core.annotation.RequestMapping;
import core.annotation.RequestMethod;
import core.mvc.Controller;
import core.mvc.ModelAndView;
import next.CannotDeleteException;
import next.controller.UserSessionUtils;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.service.QnaService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@core.annotation.Controller
@RequestMapping(value = "/qna")
public class QuestionController extends Controller {

    private QnaService qnaService = QnaService.getInstance();
    private AnswerDao answerDao = AnswerDao.getInstance();
    private QuestionDao questionDao = QuestionDao.getInstance();

    @RequestMapping(value = "/form")
    public ModelAndView createForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jspView("redirect:/users/loginForm");
        }
        return jspView("/qna/form.jsp");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return jspView("redirect:/users/loginForm");
        }
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        Question question = new Question(user.getUserId(), request.getParameter("title"), request.getParameter("contents"));
        questionDao.insert(question);
        return jspView("redirect:/");
    }

    @RequestMapping(value = "/updateForm")
    public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jspView("redirect:/users/loginForm");
        }

        long questionId = Long.parseLong(req.getParameter("questionId"));
        Question question = questionDao.findById(questionId);
        if (!question.isSameUser(UserSessionUtils.getUserFromSession(req.getSession()))) {
            throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }
        return jspView("/qna/update.jsp").addObject("question", question);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ModelAndView update(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jspView("redirect:/users/loginForm");
        }

        long questionId = Long.parseLong(req.getParameter("questionId"));
        Question question = questionDao.findById(questionId);
        if (!question.isSameUser(UserSessionUtils.getUserFromSession(req.getSession()))) {
            throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }

        Question newQuestion = new Question(question.getWriter(), req.getParameter("title"), req.getParameter("contents"));
        question.update(newQuestion);
        questionDao.update(question);
        return jspView("redirect:/");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jspView("redirect:/users/loginForm");
        }

        long questionId = Long.parseLong(req.getParameter("questionId"));
        try {
            qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(req.getSession()));
            return jspView("redirect:/");
        } catch (CannotDeleteException e) {
            return jspView("show.jsp")
                    .addObject("question", qnaService.findById(questionId))
                    .addObject("answers", qnaService.findAllByQuestionId(questionId))
                    .addObject("errorMessage", e.getMessage());
        }
    }

    @RequestMapping(value = "/show")
    public ModelAndView show(HttpServletRequest req, HttpServletResponse response) throws Exception {
        long questionId = Long.parseLong(req.getParameter("questionId"));

        Question question = questionDao.findById(questionId);
        List<Answer> answers = answerDao.findAllByQuestionId(questionId);

        ModelAndView mav = jspView("/qna/show.jsp");
        mav.addObject("question", question);
        mav.addObject("answers", answers);
        return mav;
    }
}
