package next.controller;

import core.annotation.RequestMapping;
import core.mvc.Controller;
import core.mvc.ModelAndView;
import next.dao.QuestionDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@core.annotation.Controller
public class HomeController extends Controller {
    private QuestionDao questionDao = QuestionDao.getInstance();

    @RequestMapping(value = "/")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return jspView("index.jsp").addObject("questions", questionDao.findAll());
    }
}
