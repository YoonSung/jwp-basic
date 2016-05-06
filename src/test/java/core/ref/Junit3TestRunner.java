package core.ref;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Junit3TestRunner {
	private static final Logger logger = LoggerFactory.getLogger(Junit3TestRunner.class);
	@Test
	public void run() throws Exception {
		Class<Junit3Test> clazz = Junit3Test.class;
		Junit3Test target = clazz.newInstance();
		Arrays.asList(clazz.getDeclaredMethods()).stream().filter(method -> method.getName().startsWith("test")).forEach(method -> {
			try {
				method.invoke(target);
			} catch (IllegalAccessException e) {
				logger.error("Test Method must be public");
			} catch (InvocationTargetException e) {
				logger.error("Test Method must have no parameter");
			}
		});
	}
}
