package core.config;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanDefinition;
import core.di.factory.BeanDefinitionRegistry;

import java.lang.reflect.Method;

import static org.reflections.ReflectionUtils.*;


public class ConfigurationBeanDefinitionScanner {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public  ConfigurationBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public void doScan(Class<?> configurationClass) {
        validate(configurationClass);
        beanDefinitionRegistry.registerBeanDefinition(configurationClass, new BeanDefinition(configurationClass));
        for (Method factoryMethod : getAllMethods(configurationClass, withAnnotation(Bean.class))) {
            Class<?> beanClassType = factoryMethod.getReturnType();
            beanDefinitionRegistry.registerBeanDefinition(beanClassType, new BeanDefinition(beanClassType, configurationClass));
        }
    }

    private String[] getBasePackages(Class<?> configurationClass) {
        return configurationClass.getAnnotation(ComponentScan.class).value();
    }

    private void validate(Class<?> configurationClass) {
        if (!configurationClass.isAnnotationPresent(Configuration.class)) {
            throw new IllegalArgumentException("Invalid Parameter [@Configuration annotation is missing]");
        }

        if (!configurationClass.isAnnotationPresent(ComponentScan.class)) {
            throw new IllegalArgumentException("Invalid Parameter [@ComponentScan annotation is missing]");
        }

        String[] basePackages = getBasePackages(configurationClass);
        if (basePackages.length == 0) {
            throw new IllegalArgumentException("Invalid Parameter [@ComponentScan({value}) is missing]");
        }
    }
}
