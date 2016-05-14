package core.di.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

import core.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;

public class BeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
	
	private Set<Class<?>> preInstanticateBeans;

	private Map<Class<?>, Object> beans = Maps.newHashMap();

	public BeanFactory(Set<Class<?>> preInstanticateBeans) {
		this.preInstanticateBeans = preInstanticateBeans;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> requiredType) {
		return (T)beans.get(requiredType);
	}
	
	public void initialize() {
		preInstanticateBeans.forEach(clazz -> {
			if (getBean(clazz) == null) {
				registerBean(clazz, initiateBean(clazz));
			}
		});
	}

	private Object initiateBean(Class<?> preinstanticateBean) {
		Constructor diConstructor = BeanFactoryUtils.getInjectedConstructor(preinstanticateBean);
		if (diConstructor != null) {
			List<Object> parameterBeanList = new ArrayList<>();
			for (Parameter parameter : diConstructor.getParameters()) {
				Class<?> parameterClass = BeanFactoryUtils.findConcreteClass(parameter.getType(), preInstanticateBeans);

				Object bean = getBean(parameterClass);
				if (bean == null) {
					bean = initiateBean(parameterClass);
					registerBean(parameterClass, bean);
				}
				parameterBeanList.add(bean);
			}
			return BeanUtils.instantiateClass(diConstructor, parameterBeanList.toArray());
		} else {
			return BeanUtils.instantiate(preinstanticateBean);
		}
		/*
		return Optional.ofNullable(BeanFactoryUtils.getInjectedConstructor(preinstanticateBean))
				.map(diConstructor -> {
					List<Object> parameterBeanList = new ArrayList<>();
					for (Parameter parameter : diConstructor.getParameters()) {
						Class<?> parameterClass = BeanFactoryUtils.findConcreteClass(parameter.getType(), preInstanticateBeans);
						Object bean = getBean(parameterClass);
						if (bean == null) {
							bean = initiateBean(parameterClass);
							beans.put(parameterClass, bean);
						}
						parameterBeanList.add(bean);
					}
					return (Object) BeanUtils.instantiateClass(diConstructor, parameterBeanList.toArray());
				})
				.orElse(BeanUtils.instantiate(preinstanticateBean));
		*/
	}

	private void registerBean(Class<?> parameterClass, Object bean) {
		beans.put(parameterClass, bean);
	}

	public Map<Class<?>, Object> getAnnotatedBeans(Class<? extends Annotation> annotation) {
		Map<Class<?>, Object> beans = Maps.newHashMap();
		preInstanticateBeans
				.stream()
				.filter(clazz -> clazz.isAnnotationPresent(annotation))
				.filter(clazz -> getBean(clazz) != null)
				.forEach(clazz -> beans.put(clazz, getBean(clazz)));
		return beans;
	}
}
