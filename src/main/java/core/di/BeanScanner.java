package core.di;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import core.annotation.Controller;

public class BeanScanner {
	private static final Logger log = LoggerFactory.getLogger(BeanScanner.class);

	private Reflections reflections;

	public BeanScanner(Object... basePackage) {
		reflections = new Reflections(basePackage);
	}

	private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
		Set<Class<?>> beans = Sets.newHashSet();
		for (Class<? extends Annotation> annotation : annotations) {
			beans.addAll(reflections.getTypesAnnotatedWith(annotation));
		}
		return beans;
	}

	public Set<Class<?>> scan() {
		return getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
	}
}
