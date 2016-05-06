package core.ref;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import next.model.Question;

import java.util.Arrays;

public class ReflectionTest {
	private static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);

	//리플렉션을 활용해Question 클래스의 모든 필드, 생성자, 메소드를 출력한다
	@Test
	public void showClass() throws NoSuchMethodException {
		Class<Question> clazz = Question.class;
		logger.debug("name : " + clazz.getName());
		Arrays.asList(clazz.getDeclaredConstructors()).forEach(constructor -> {
			logger.debug("constructor : " + constructor.getName());
			Arrays.asList(constructor.getParameters()).forEach(parameter -> {
				logger.debug(String.format("parameterName : %s / parameterType : %s", parameter.getName(), parameter.getType().getName()));
			});
		});

		Arrays.asList(clazz.getDeclaredFields()).forEach(field -> {
			field.setAccessible(true); 	//set accessible to private field
			logger.debug(String.format("fieldName : %s / fieldType : %s", field.getName(), field.getType().getName()));
		});

		Arrays.asList(clazz.getDeclaredMethods()).forEach(method -> {
			logger.debug("method : " + method.getName());
			Arrays.asList(method.getParameters()).forEach(parameter -> {
				logger.debug(String.format("parameterName : %s / parameterType : %s", parameter.getName(), parameter.getType().getName()));
			});
		});
	}
}
