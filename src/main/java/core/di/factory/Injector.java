package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import core.annotation.Inject;
import core.di.InjectorUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public interface Injector {
    void inject(Class<?> beanFrame);
}

class DefaultListableInjector implements Injector {
    private final List<Injector> injectorList;
    public DefaultListableInjector(BeanFactory beanFactory) {
        this(Arrays.asList(new Injector[]{
                new ConstructorInjector(beanFactory),
                new SetterInjector(beanFactory),
                new DirectFieldInjector(beanFactory)
        }));
    }

    public DefaultListableInjector(List<Injector> injectorList) {
        this.injectorList = injectorList;
    }

    @Override
    public void inject(Class<?> beanFrame) {
        injectorList.forEach(injector -> injector.inject(beanFrame));
    }
}

abstract class AbstractInjector implements Injector {
    private final BeanFactory beanFactory;
    protected final Logger log = LoggerFactory.getLogger(AbstractInjector.class);

    public AbstractInjector(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void inject(Class<?> beanFrame) {
        Object bean = instantiateClass(beanFrame);
        Set<?> targets = getInjectTargets(beanFrame);

        for(Iterator<?> iterator = targets.iterator(); iterator.hasNext();) {
            inject(iterator.next(), bean);
        }
    }

    protected abstract Set<?> getInjectTargets(Class<?> beanFrame);

    protected abstract void inject(Object target, Object bean);

    protected Object instantiateClass(Class<?> clazz) {
        Object bean = beanFactory.getBean(clazz);
        if (bean != null) {
            return bean;
        }

        Constructor<?> injectedConstructor = InjectorUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            bean = BeanUtils.instantiate(clazz);
            beanFactory.addBean(clazz, bean);
            return bean;
        }

        log.debug("Constructor : {}", injectedConstructor);
        bean = instantiateConstructor(injectedConstructor);
        beanFactory.addBean(clazz, bean);
        return bean;
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] pTypes = constructor.getParameterTypes();
        List<Object> args = Lists.newArrayList();
        Set<Class<?>> preInstanticateBeans = beanFactory.getPreInstantiateBeans();
        for (Class<?> clazz : pTypes) {
            Class<?> concreteClazz = InjectorUtils.findConcreteClass(clazz, preInstanticateBeans);
            if (!preInstanticateBeans.contains(concreteClazz)) {
                throw new IllegalStateException(clazz + "는 Bean이 아니다.");
            }

            Object bean = beanFactory.getBean(concreteClazz);
            if (bean == null) {
                bean = instantiateClass(concreteClazz);
            }
            args.add(bean);
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }
}

class ConstructorInjector extends AbstractInjector {

    public ConstructorInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    protected Set<Constructor> getInjectTargets(Class<?> beanFrame) {
        Set<Constructor> set = Sets.newHashSet();
        set.add(InjectorUtils.getInjectedConstructor(beanFrame));
        return set;
    }

    @Override
    protected void inject(Object target, Object bean) {}
}

class SetterInjector extends AbstractInjector {

    public SetterInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    protected Set<?> getInjectTargets(Class<?> beanFrame) {
        return ReflectionUtils.getMethods(beanFrame, ReflectionUtils.withAnnotation(Inject.class));
    }

    @Override
    protected void inject(Object target, Object bean) {
        Method method = (Method) target;
        method.setAccessible(true);
        switch (method.getParameters().length) {
            case 0:
                throw new UnsupportedOperationException("set method parameter must be exist");
            case 1:
                break;
            default:
                throw new UnsupportedOperationException("set method can not have multiple parameters");
        }
        try {
            method.invoke(bean, new Object[]{instantiateClass(method.getParameters()[0].getType())});
        } catch (IllegalAccessException e) {
            log.error("Cannot access to Method. because Method is not public");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            log.error("Cannot access to Method. Bean DI is Fail");
            e.printStackTrace();
        }
    }
}

class DirectFieldInjector extends AbstractInjector {
    public DirectFieldInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    protected Set<?> getInjectTargets(Class<?> beanFrame) {
        return ReflectionUtils.getAllFields(beanFrame, ReflectionUtils.withAnnotation(Inject.class));
    }

    @Override
    protected void inject(Object target, Object bean) {
        Field field = (Field) target;
        field.setAccessible(true);
        try {
            field.set(bean, instantiateClass(field.getType()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.error("Cannot access to field. Bean DI is Fail");
        }
    }
}