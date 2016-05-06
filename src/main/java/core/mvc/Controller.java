package core.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Controller {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected ModelAndView jspView(String forwardUrl) {
		return new ModelAndView(new JstlView(forwardUrl));
	}
	protected ModelAndView jsonView() {
		return new ModelAndView(new JsonView());
	}
}
