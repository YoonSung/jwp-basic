package core.mvc;

import core.annotation.RequestMethod;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

public class RequestMapping {
	private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);
	private Map<HandlerKey, HandlerExecution> mappings = new HashMap<>();

	/**
	 * TODO
	 * 1단계 : 기존 코드에서 url을 등록하는 과정을 Annotation에 설정된 값을 통해서 할 수 있도록 변경 (RequestMapping에 mappings.put하는 과정을 리팩토링한다)
	 * 2단계 : 지금까지는 요청 URL만을 사용했지만, 실제 웹서버 어플리케이션에서는 같은 URL요청이라도 METHOD정보가 다르면 각기 다른 처리를 할 수 있어야 한다
	 *        지금까지는 get메서드만을 사용했지만 의미별로 맞는 url과 method를 지정한다
	 *        또한 컨트롤러 클래스를 합치기 위해서는 각 요청마다 식별될 정보를 판단해야 한다. 요청별로 식별될 수 있는 값은
	 * 		  요청 "URL"과 요청 "Http Method" 정보이다. HandlerKey 클래스를 만들고 url과 method를 담을 수 있도록 리팩토링한다.
	 * 		   (mappings.put(new HandlerKey(url, method), controllerInstance);
	 * 		   (결과적으로 qna 패키지에는 QnaController가, user 패키지에는 UserController만 포함하도록 한다)
	 * 3단계 : 계속 증가되는 컨트롤러를 줄이기 위해, 하나의 컨트롤러에서 여러개의 RequestMapping을 처리할 수 있도록 한다.
	 * 		  이 부분을 리팩토링하기 위해  실행될 메서드정보를 담고있는 HandlerExecution 클래스를 사용한다.
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

				Arrays.asList(clazz.getDeclaredMethods())
						.stream()
						.filter(method -> {
							core.annotation.RequestMapping annotation = method.getDeclaredAnnotation(core.annotation.RequestMapping.class);
							return annotation != null && annotation.value().startsWith("/");
						})
						.forEach(method -> {
							core.annotation.RequestMapping annotation = method.getDeclaredAnnotation(core.annotation.RequestMapping.class);
							String requestUrl = prefixUrl + annotation.value();
							RequestMethod requestMethod = annotation.method();
							put(new HandlerKey(requestUrl, requestMethod), new HandlerExecution(instance, method));
						});
			} catch (Exception e) {
				e.printStackTrace();
				throw new UnsupportedOperationException("RequestMapping Initialization Exception");
			}
		});

		logger.info("Initialized Request Mapping!");
	}

	public HandlerExecution findController(String url, RequestMethod method) {
		return mappings.get(new HandlerKey(url, method));
	}

	void put(HandlerKey key, HandlerExecution execution) {
		mappings.put(key, execution);
	}
}