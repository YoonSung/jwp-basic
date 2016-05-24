package core.di.factory;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static core.di.factory.InjectType.INJECT_NO;

public class BeanFactory implements BeanDefinitionRegistry {
	private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);
	
	private Map<Class<?>, Object> beans = Maps.newHashMap();
	
	private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
	
    public void initialize() {
    	for (Class<?> clazz : getBeanClasses()) {
			getBean(clazz);
		}
    }
    
    public Set<Class<?>> getBeanClasses() {
    	return beanDefinitions.keySet();
    }
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> clazz) {
		Object bean = beans.get(clazz);
		if (bean != null) {
			return (T)bean;
		}
		
		Class<?> concreteClass = findConcreteClass(clazz);
		BeanDefinition beanDefinition = beanDefinitions.get(concreteClass);
		bean = inject(beanDefinition);
		beans.put(concreteClass, bean);
		return (T)bean;
	}
	
	private Class<?> findConcreteClass(Class<?> clazz) {
		Set<Class<?>> beanClasses = getBeanClasses();
    	Class<?> concreteClazz = BeanFactoryUtils.findConcreteClass(clazz, beanClasses);
        if (!beanClasses.contains(concreteClazz)) {
            throw new IllegalStateException(clazz + "는 Bean이 아니다.");
        }
        return concreteClazz;
    }

	private Object inject(BeanDefinition beanDefinition) {
		switch(beanDefinition.getResolvedInjectMode()) {
			case INJECT_NO:
				return BeanUtils.instantiate(beanDefinition.getBeanClass());
			case INJECT_FIELD:
				return injectFields(beanDefinition);
			case INJECT_CONSTRUCTOR:
				return injectConstructor(beanDefinition);
			case INJECT_CONFIGURATION:
				return injectByConfiguration(beanDefinition);
			default:
				throw new UnsupportedOperationException(String.format("Invalid BeanDefinition [class : %s]", beanDefinition.getBeanClass().getCanonicalName()));
		}
	}

	private Object injectByConfiguration(BeanDefinition beanDefinition) {
		Class<?> configurationClass = beanDefinition.getConfigurationClass();
		Class<?> beanClass = beanDefinition.getBeanClass();
		Object configInstance = getBean(configurationClass);
		Method method = BeanFactoryUtils.getBeanMethod(configurationClass, beanClass);
		try {
			List<Object> parameters = Lists.newArrayList();
			for (Class<?> parameterType : method.getParameterTypes()) {
				parameters.add(getBean(parameterType));
			}
			return method.invoke(configInstance, parameters.toArray());
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Bean Factory method is not public", e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Factory method invoke fail", e);
		}
	}

	private Object injectConstructor(BeanDefinition beanDefinition) {
		Constructor<?> constructor = beanDefinition.getInjectConstructor();
		List<Object> args = Lists.newArrayList();
		for (Class<?> clazz : constructor.getParameterTypes()) {
		    args.add(getBean(clazz));
		}
		return BeanUtils.instantiateClass(constructor, args.toArray());
	}

	private Object injectFields(BeanDefinition beanDefinition) {
		Object bean = BeanUtils.instantiate(beanDefinition.getBeanClass());
		Set<Field> injectFields = beanDefinition.getInjectFields();
		for (Field field : injectFields) {
			injectField(bean, field);
		}
		return bean;
	}

	private void injectField(Object bean, Field field) {
		log.debug("Inject Bean : {}, Field : {}", bean, field);
		try {
			field.setAccessible(true);
			field.set(bean, getBean(field.getType()));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			log.error(e.getMessage());
		}
	}

	public void clear() {
		beanDefinitions.clear();
		beans.clear();
	}

	@Override
	public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
		log.debug("register bean : {}", clazz);
		beanDefinitions.put(clazz, beanDefinition);
	}
}
