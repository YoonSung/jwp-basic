package core.ref;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.*;

public class Junit4TestRunner {
	private static final Logger logger = LoggerFactory.getLogger(Junit4TestRunner.class);
	@Test
	public void run() throws Exception {
		Class<Junit4Test> clazz = Junit4Test.class;
		Junit4Test target = clazz.newInstance();
		asList(clazz.getMethods())
			.forEach(method ->
				asList(method.getDeclaredAnnotations())
					.stream()
					.filter(annotation -> annotation.annotationType().isAssignableFrom(MyTest.class))
					.forEach(annotation -> {
						try {
							method.invoke(target);
						} catch (IllegalAccessException e) {
							logger.error("Test Method must be public");
						} catch (InvocationTargetException e) {
							logger.error("Test Method must have no parameter");
						}
					})
			);
	}
}
