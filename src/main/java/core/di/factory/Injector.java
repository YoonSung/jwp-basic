package core.di.factory;

import com.google.common.collect.Lists;
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
    int priority();
}

class DefaultListableInjector extends AbstractInjector {

    private final List<Injector> injectorList;

    public DefaultListableInjector(BeanFactory beanFactory) {
        this(beanFactory, Arrays.asList(new Injector[]{
                new ConstructorInjector(beanFactory),
                new SetterInjector(beanFactory),
                new DirectFieldInjector(beanFactory)
        }));
    }

    public DefaultListableInjector(BeanFactory beanFactory, List<Injector> injectorList) {
        super(beanFactory);
        reorder(injectorList);
        this.injectorList = injectorList;
    }

    private void reorder(List<Injector> injectorList) {
        Collections.sort(injectorList, (o1, o2) -> {
            if (o1.priority() > o2.priority())
                return -1;
            else if (o1.priority() == o2.priority())
                return 0;
            else
                return 1;
        });
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

    @Override
    public int priority() {
        return 2;
    }
}

class ConstructorInjector extends AbstractInjector {

    public ConstructorInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void inject(Class<?> beanFrame) {
        instantiateClass(beanFrame);
    }

    @Override
    public int priority() {
        return 1;
    }
}

class SetterInjector extends AbstractInjector {

    public SetterInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void inject(Class<?> beanFrame) {
        Object bean = instantiateClass(beanFrame);
        Set<Method> methods = ReflectionUtils.getMethods(beanFrame, ReflectionUtils.withAnnotation(Inject.class));

        for(Iterator<Method> iterator = methods.iterator(); iterator.hasNext();) {
            Method method = iterator.next();
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

    @Override
    public int priority() {
        return 2;
    }
}

class DirectFieldInjector extends AbstractInjector {
    public DirectFieldInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void inject(Class<?> beanFrame) {
        Object bean = instantiateClass(beanFrame);
        Set<Field> fields = ReflectionUtils.getAllFields(beanFrame, ReflectionUtils.withAnnotation(Inject.class));
        for(Iterator<Field> iterator = fields.iterator(); iterator.hasNext();) {
            Field field = iterator.next();
            field.setAccessible(true);
            try {
                field.set(bean, instantiateClass(field.getType()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                log.error("Cannot access to field. Bean DI is Fail");
            }
        }
    }

    @Override
    public int priority() {
        return 3;
    }
}