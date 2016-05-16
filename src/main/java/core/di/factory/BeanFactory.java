package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private Injector injector = new DefaultListableInjector(this);

    private Set<Class<?>> preInstanticateBeans;

	private Map<Class<?>, Object> beans = Maps.newHashMap();
	private Set<Class<?>> preInstantiateBeans;

	public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> requiredType) {
		return (T)beans.get(requiredType);
	}

    void addBean(Class<?> beanFrame, Object object) {
        beans.put(beanFrame, object);
    }

    public void initialize() {

        for (Class<?> clazz : preInstanticateBeans) {
            if (beans.get(clazz) == null) {
            	logger.debug("instantiated Class : {}", clazz);
            	injector.inject(clazz);
            }
        }
    }

	public Map<Class<?>, Object> getControllers() {
		Map<Class<?>, Object> controllers = Maps.newHashMap();
		for (Class<?> clazz : preInstanticateBeans) {
			Annotation annotation = clazz.getAnnotation(Controller.class);
			if (annotation != null) {
				controllers.put(clazz, beans.get(clazz));
			}
		}
		return controllers;
	}

	public Set<Class<?>> getPreInstantiateBeans() {
		return preInstantiateBeans;
	}
}
