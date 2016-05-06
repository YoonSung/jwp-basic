package core.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "dispatcher", urlPatterns = {"", "/"}, loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

	private RequestMapping rm;

	@Override
	public void init() throws ServletException {
		rm = new RequestMapping();
		rm.initMapping();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestUri = req.getRequestURI();
		logger.debug("Method : {}, Request URI : {}", req.getMethod(), requestUri);

		RequestMethod method = null;
		try {
			method = RequestMethod.valueOf(req.getMethod());
		} catch (NullPointerException e ) {
			throw new ServletException("invalid method request");
		}

		HandlerExecution handlerExecution = rm.findController(req.getRequestURI(), method);
		ModelAndView mav;
		try {
			mav = handlerExecution.execute(req, resp);
			View view = mav.getView();
			view.render(mav.getModel(), req, resp);
		} catch (Throwable e) {
			logger.error("Exception : {}", e);
			throw new ServletException(e.getMessage());
		}
	}
}
