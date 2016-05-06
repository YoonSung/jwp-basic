package core.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import com.sun.deploy.net.HttpRequest;
import core.annotation.RequestMethod;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import next.controller.HomeController;
import next.controller.qna.AddAnswerController;
import next.controller.qna.ApiDeleteQuestionController;
import next.controller.qna.ApiListQuestionController;
import next.controller.qna.CreateFormQuestionController;
import next.controller.qna.CreateQuestionController;
import next.controller.qna.DeleteAnswerController;
import next.controller.qna.DeleteQuestionController;
import next.controller.qna.ShowQuestionController;
import next.controller.qna.UpdateFormQuestionController;
import next.controller.qna.UpdateQuestionController;
import next.controller.user.CreateUserController;
import next.controller.user.ListUserController;
import next.controller.user.LoginController;
import next.controller.user.LogoutController;
import next.controller.user.ProfileController;
import next.controller.user.UpdateFormUserController;
import next.controller.user.UpdateUserController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestMapping {
	private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);
	private Map<String, Controller> mappings = new HashMap<>();

	/**
	 * TODO
	 * 1단계 : 기존 코드에서 url을 등록하는 과정을 Annotation에 설정된 값을 통해서 할 수 있도록 변경 (RequestMapping에 mappings.put하는 과정을 리팩토링한다)
	 * 2단계 : 지금까지는 요청 URL만을 사용했지만, 실제 웹서버 어플리케이션에서는 같은 URL요청이라도 METHOD정보가 다르면 각기 다른 처리를 할 수 있어야 한다
	 *        지금까지는 get메서드만을 사용했지만 의미별로 맞는 url과 method를 지정한다
	 *        또한 컨트롤러 클래스를 합치기 위해서는 각 요청마다 식별될 정보를 판단해야 한다. 요청별로 식별될 수 있는 값은
	 * 		  요청 "URL"과 요청 "Http Method" 정보이다. HandlerKey 클래스를 만들고 url과 method를 담을 수 있도록 리팩토링한다.
	 * 		   (mappings.put(new HandlerKey(url, method), controllerInstance);
	 * 3단계 : 매 요청마다 reflection을 통해 실행될 method를 찾는과정이 불필요해 보인다. 이 부분을 리팩토링하기 위해  실행될 메서드정보를 담고있는 HandlerExecution 클래스를 사용한다.
	 * 		   (mappings.put(new HandlerKey("/users", HttpMethod.GET), new HandlerExecution(controllerInstance, executedMethod));
	 */
	void initMapping() {
		Reflections reflections = new Reflections("next.controller");
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(core.annotation.Controller.class);
		classes.forEach(clazz -> {
			try {
				Controller instance = (Controller) clazz.newInstance();
				String prefixUrl = Optional.ofNullable(clazz.getDeclaredAnnotation(core.annotation.RequestMapping.class))
						.map(classAnnotation -> classAnnotation.value())
						.orElse("");

				Method handleMethod = clazz.getDeclaredMethod("execute", HttpServletRequest.class, HttpServletResponse.class);
				core.annotation.RequestMapping annotation = handleMethod.getDeclaredAnnotation(core.annotation.RequestMapping.class);

				if (annotation != null) {
					String requestUrl = prefixUrl + annotation.value();
					put(requestUrl, instance);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new UnsupportedOperationException("RequestMappig Initialization Exception");
			}
		});

//		mappings.put("/", new HomeController());
//	    mappings.put("/users/form", new ForwardController("/user/form.jsp"));
//	    mappings.put("/users/loginForm", new ForwardController("/user/login.jsp"));
//	    mappings.put("/users", new ListUserController());
//		mappings.put("/users/login", new LoginController());
//		mappings.put("/users/profile", new ProfileController());
//	    mappings.put("/users/logout", new LogoutController());
//	    mappings.put("/users/create", new CreateUserController());
//	    mappings.put("/users/updateForm", new UpdateFormUserController());
//	    mappings.put("/users/update", new UpdateUserController());
//		mappings.put("/qna/show", new ShowQuestionController());
//		mappings.put("/qna/form", new CreateFormQuestionController());
//		mappings.put("/qna/create", new CreateQuestionController());
//		mappings.put("/qna/updateForm", new UpdateFormQuestionController());
//		mappings.put("/qna/update", new UpdateQuestionController());
//		mappings.put("/qna/delete", new DeleteQuestionController());
//		mappings.put("/api/qna/deleteQuestion", new ApiDeleteQuestionController());
//		mappings.put("/api/qna/list", new ApiListQuestionController());
//		mappings.put("/api/qna/addAnswer", new AddAnswerController());
//		mappings.put("/api/qna/deleteAnswer", new DeleteAnswerController());

		logger.info("Initialized Request Mapping!");
	}
	
	public Controller findController(String url) {
		return mappings.get(url);
	}
	
	void put(String url, Controller controller) {
		mappings.put(url, controller);
	}
}
